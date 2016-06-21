/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.cvut.fel.webrtc.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.kurento.client.Composite;
import org.kurento.client.Continuation;
import org.kurento.client.ErrorEvent;
import org.kurento.client.EventListener;
import org.kurento.client.Hub;
import org.kurento.client.HubPort;
import org.kurento.client.IceCandidate;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.kurento.client.MediaProfileSpecType;
import org.kurento.client.RecorderEndpoint;
import org.kurento.room.exception.RoomException;
import org.kurento.room.exception.RoomException.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;


import cz.cvut.fel.webrtc.handlers.*;



/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @author Micael Gallego (micael.gallego@gmail.com)
 * @author Radu Tom Vlad (rvlad@naevatec.com)
 * @since 1.0.0
 */
public class Room {
	public static final int ASYNC_LATCH_TIMEOUT = 30;

	private final static Logger log = LoggerFactory.getLogger(Room.class);

	private final ConcurrentMap<String, Participant> participants = new ConcurrentHashMap<String, Participant>();
	private final String name;

	private MediaPipeline pipeline;
	private CountDownLatch pipelineLatch = new CountDownLatch(1);

	private KurentoClient kurentoClient;

	private RoomHandler roomHandler;

	private volatile boolean closed = false;

	private AtomicInteger activePublishers = new AtomicInteger(0);

	private Object pipelineCreateLock = new Object();
	private Object pipelineReleaseLock = new Object();
	private volatile boolean pipelineReleased = false;
	private boolean destroyKurentoClient;

	// --
	private Line line;
	private long cseq;
	private final String callId;
	private WebUser screensharer;
	private WebSocketSession session;

	// Record
	private HubPort hubPort;
	private Hub composite;
	private RecorderEndpoint recorderEndpoint;

	public Room(String roomName, KurentoClient kurentoClient, RoomHandler roomHandler, boolean destroyKurentoClient) {
		this.name = roomName;
		this.kurentoClient = kurentoClient;
		this.destroyKurentoClient = destroyKurentoClient;
		this.roomHandler = roomHandler;
		log.debug("New ROOM instance, named '{}'", roomName);
		this.callId = UUID.randomUUID().toString();
	}

	public String getName() {
		return name;
	}

	public MediaPipeline getPipeline() {
		try {
			pipelineLatch.await(Room.ASYNC_LATCH_TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		return this.pipeline;
	}

	public void join(String participantId, String userName,String roomName, boolean webParticipant) throws RoomException {

		checkClosed();

		if (userName == null || userName.isEmpty()) {
			throw new RoomException(Code.GENERIC_ERROR_CODE, "Empty user name is not allowed");
		}
		for (Participant p : participants.values()) {
			if (p.getName().equals(userName)) {
				throw new RoomException(Code.EXISTING_USER_IN_ROOM_ERROR_CODE,
						"User '" + userName + "' already exists in room '" + name + "'");
			}
		}

		createPipeline();

		participants.put(participantId,
				new Participant(participantId, userName,roomName, this, getPipeline(), webParticipant, composite, session));

		log.info("ROOM {}: Added participant {}", name, userName);

		// Record

		if (participants.size() == 1) {
			log.info("Start Recording");
			this.hubPort = new HubPort.Builder(this.composite).build();
			this.recorderEndpoint = new RecorderEndpoint.Builder(getPipeline(), "\\Home\\wt" + getName() + ".webm")
					.withMediaProfile(MediaProfileSpecType.WEBM).build();
			this.hubPort.connect(this.recorderEndpoint);
			this.recorderEndpoint.record();
		}
	}

	public Participant join(String userId, WebSocketSession session, Class<? extends Participant> sessionClass) {
		log.info("ROOM {}: adding participant {}", name, userId);

		Participant participant = null;

		try {

			participant = sessionClass
					.getConstructor(String.class, String.class, WebSocketSession.class, MediaPipeline.class,
							MediaPipeline.class, Hub.class)
					.newInstance(userId, this.name, session, this.pipeline, this.composite);

			add(participant);
			sendInformation(participant, "compositeInfo");

		} catch (Exception e) {
			log.info("ROOM {}: adding participant {} failed: {}", name, userId, e);
		}

		return participant;
	}

	public void sendInformation(Participant user, String id) throws IOException {

		final JsonArray participantsArray = new JsonArray();

		for (final Participant participant : this.getParticipants()) {
			if (!participant.equals(user)) {
				final JsonElement participantName = new JsonPrimitive(participant.getName());
				participantsArray.add(participantName);
			}
		}
	}

	public void add(Participant participant) {
		if (participant != null)
			participants.put(participant.getId(), participant);
	}

	public void newPublisher(Participant participant) {
		registerPublisher();

		// pre-load endpoints to recv video from the new publisher
		for (Participant participant1 : participants.values()) {
			if (participant.equals(participant1)) {
				continue;
			}
			participant1.getNewOrExistingSubscriber(participant.getName());
		}

		log.debug("ROOM {}: Virtually subscribed other participants {} to new publisher {}", name,
				participants.values(), participant.getName());
	}

	public void cancelPublisher(Participant participant) {
		deregisterPublisher();

		// cancel recv video from this publisher
		for (Participant subscriber : participants.values()) {
			if (participant.equals(subscriber)) {
				continue;
			}
			subscriber.cancelReceivingMedia(participant.getName());
		}

		log.debug("ROOM {}: Unsubscribed other participants {} from the publisher {}", name, participants.values(),
				participant.getName());

	}

	public void leave(String participantId) throws RoomException {

		checkClosed();

		Participant participant = participants.get(participantId);
		if (participant == null) {
			throw new RoomException(Code.USER_NOT_FOUND_ERROR_CODE,
					"User #" + participantId + " not found in room '" + name + "'");
		}
		log.info("PARTICIPANT {}: Leaving room {}", participant.getName(), this.name);
		if (participant.isStreaming()) {
			this.deregisterPublisher();
		}
		this.removeParticipant(participant);
		participant.close();
	}

	public Collection<Participant> getParticipants() {

		checkClosed();

		return participants.values();
	}

	public Set<String> getParticipantIds() {

		checkClosed();

		return participants.keySet();
	}

	public Participant getParticipant(String participantId) {

		checkClosed();

		return participants.get(participantId);
	}

	public Participant getParticipantByName(String userName) {

		checkClosed();

		for (Participant p : participants.values()) {
			if (p.getName().equals(userName)) {
				return p;
			}
		}

		return null;
	}

	public void close() {
		if (!closed) {

			for (Participant user : participants.values()) {
				user.close();
			}

			participants.clear();

			closePipeline();

			log.debug("Room {} closed", this.name);

			if (destroyKurentoClient) {
				kurentoClient.destroy();
			}

			this.closed = true;
		} else {
			log.warn("Closing an already closed room '{}'", this.name);
		}
	}

	public void sendIceCandidate(String participantId, String endpointName, IceCandidate candidate) {
		this.roomHandler.onIceCandidate(name, participantId, endpointName, candidate);
	}

	public void sendMediaError(String participantId, String description) {
		this.roomHandler.onMediaElementError(name, participantId, description);
	}

	public boolean isClosed() {
		return closed;
	}

	private void checkClosed() {
		if (closed) {
			throw new RoomException(Code.ROOM_CLOSED_ERROR_CODE, "The room '" + name + "' is closed");
		}
	}

	private void removeParticipant(Participant participant) {

		checkClosed();

		participants.remove(participant.getId());

		log.debug("ROOM {}: Cancel receiving media from user '{}' for other users", this.name, participant.getName());
		for (Participant other : participants.values()) {
			other.cancelReceivingMedia(participant.getName());
		}
	}

	public int getActivePublishers() {
		return activePublishers.get();
	}

	public void registerPublisher() {
		this.activePublishers.incrementAndGet();
	}

	public void deregisterPublisher() {
		this.activePublishers.decrementAndGet();
	}

	private void createPipeline() {
		synchronized (pipelineCreateLock) {
			if (pipeline != null) {
				return;
			}
			log.info("ROOM {}: Creating MediaPipeline", name);
			try {
				kurentoClient.createMediaPipeline(new Continuation<MediaPipeline>() {
					@Override
					public void onSuccess(MediaPipeline result) throws Exception {
						pipeline = result;
						pipelineLatch.countDown();
						log.debug("ROOM {}: Created MediaPipeline", name);
					}

					@Override
					public void onError(Throwable cause) throws Exception {
						pipelineLatch.countDown();
						log.error("ROOM {}: Failed to create MediaPipeline", name, cause);
					}
				});
			} catch (Exception e) {
				log.error("Unable to create media pipeline for room '{}'", name, e);
				pipelineLatch.countDown();
			}
			if (getPipeline() == null) {
				throw new RoomException(Code.ROOM_CANNOT_BE_CREATED_ERROR_CODE,
						"Unable to create media pipeline for room '" + name + "'");
			}

			pipeline.addErrorListener(new EventListener<ErrorEvent>() {

				@Override
				public void onEvent(ErrorEvent event) {
					String desc = event.getType() + ": " + event.getDescription() + "(errCode=" + event.getErrorCode()
							+ ")";
					log.warn("ROOM {}: Pipeline error encountered: {}", name, desc);
					roomHandler.onPipelineError(name, getParticipantIds(), desc);
				}

			});
		}
		// Record
		this.composite = new Composite.Builder(getPipeline()).build();
	}

	private void closePipeline() {
		synchronized (pipelineReleaseLock) {
			if (pipeline == null || pipelineReleased) {
				return;
			}
			getPipeline().release(new Continuation<Void>() {

				@Override
				public void onSuccess(Void result) throws Exception {
					log.debug("ROOM {}: Released Pipeline", Room.this.name);
					pipelineReleased = true;
				}

				@Override
				public void onError(Throwable cause) throws Exception {
					log.warn("ROOM {}: Could not successfully release Pipeline", Room.this.name, cause);
					pipelineReleased = true;
				}
			});
		}
	}

	private void broadcast(JsonObject message, Participant exception) {

		for (final Participant participant : participants.values()) {

			if (participant.equals(exception) || !(participant instanceof WebUser))
				continue;

			try {
				participant.sendMessage(message);
			} catch (final IOException e) {
				log.debug("ROOM {}: participant {} could not be notified", name, participant.getName(), e);
			}

		}
	}

	public void joinRoom(Participant newParticipant) {
		final JsonObject newParticipantMsg = new JsonObject();
		newParticipantMsg.addProperty("id", "newParticipantArrived");
		newParticipantMsg.addProperty("name", newParticipant.getName());
		newParticipantMsg.addProperty("userId", newParticipant.getId());
		broadcast(newParticipantMsg, newParticipant);
	}

	public void cancelPresentation() throws IOException {
		if (screensharer != null) {
			final JsonObject cancelPresentationMsg = new JsonObject();
			cancelPresentationMsg.addProperty("id", "cancelPresentation");
			cancelPresentationMsg.addProperty("userId", screensharer.getId());

			for (final Participant participant : participants.values()) {
				if (participant instanceof WebUser) {
					final WebUser webParticipant = (WebUser) participant;
					webParticipant.cancelPresentation();
					webParticipant.sendMessage(cancelPresentationMsg);
				}
			}

			screensharer = null;
		}
	}

	public void broadcast(JsonObject message) {
		broadcast(message, null);
	}

	public Line getLine() {
		return this.line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public boolean getClosed() {
		return closed;
	}

	public long setCSeq(long cseq) {
		this.cseq = cseq;
		return cseq;
	}

	public long getCSeq() {
		return this.cseq;
	}

	public String getCallId() {
		return this.callId;
	}
	
	public void setScreensharer(WebUser user) {
		this.screensharer = user;
	}

	public boolean hasScreensharer() {
		return (screensharer != null);
}
	public int size() {
		return participants.size();
}
}
