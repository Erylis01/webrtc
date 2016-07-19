/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
package cz.cvut.fel.webrtc.resources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.kurento.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;


//import org.kurento.client.HubPort;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Lesser General Public License (LGPL)
 * version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 */

public class Room implements Closeable {

	private final Logger log = LoggerFactory.getLogger(Room.class);

	private ConcurrentMap<String, Participant> participants = new ConcurrentSkipListMap<>();

	private MediaPipeline presentationPipeline;
	private MediaPipeline compositePipeline;

	private Composite composite;
	private final String name;

	private Line line;
	private final String callId;
	private long cseq;
	private boolean closing;
	private WebUser screensharer;

	// Record
	private HubPort hubPort;

	private RecorderEndpoint recorderEndpoint;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param roomname
	 *            this is the name user want to give to the room
	 */
	protected Room(String roomName) {
		this.callId = UUID.randomUUID().toString();
		this.cseq = (new Random()).nextInt(100);
		this.name = roomName;
	}

	/**
	 * creat the room for the communication
	 * 
	 * @param roomname
	 *            this is the name user want to give to the room
	 * @kurotoClient it's the link between the WebRTC serveur and the room
	 */
	public Room(String roomName, KurentoClient kurento) {
		this(roomName);

		this.compositePipeline = kurento.createMediaPipeline();
		this.presentationPipeline = kurento.createMediaPipeline();
		this.composite = new Composite.Builder(compositePipeline).build();

		log.info("ROOM {} has been created", roomName);
	}

	@PreDestroy
	private void shutdown() {
		this.close();
	}

	/**
	 * Represents a client's request to join a room. The room must exist in
	 * order to perform the join.
	 * 
	 * @param userID
	 *            - name or identifier of the user in the room. Will be used to
	 *            identify her WebRTC media peer (from the client-side).
	 * @param session
	 *            - conversation between two web socket endpoints
	 * @param sessionClass
	 *            - it permit to instantiate the created new participant
	 * 
	 * @return an existing peers of type Participant, can be empty if first
	 */
	public Participant join(String userId, WebSocketSession session, Class<? extends Participant> sessionClass) {
		log.info("ROOM {}: adding participant {}", name, userId);

		Participant participant = null;

		try {

			participant = sessionClass.getConstructor(String.class, String.class, WebSocketSession.class,
					MediaPipeline.class, MediaPipeline.class, Hub.class).newInstance(userId, this.name, session,
							this.compositePipeline, this.presentationPipeline, this.composite);

			add(participant);
			sendInformation(participant, "compositeInfo");
			this.hubPort = new HubPort.Builder(this.composite).build();

		} catch (Exception e) {
			log.info("ROOM {}: adding participant {} failed: {}", name, userId, e);
		}

		return participant;
	}

	/**
	 * Add to a set of participant a Participant
	 * 
	 * @param participant
	 *            - instance of Participant
	 */
	public void add(Participant participant) {
		if (participant != null)
			participants.put(participant.getId(), participant);
	}

	/**
	 * Allows a participant to leave a room
	 * 
	 * @param user
	 *            - The participant
	 * @throws IOException
	 *             - on error leaving the room
	 */
	public void leave(Participant user) throws IOException {

		log.debug("PARTICIPANT {}: Leaving room {}", user.getName(), this.name);
		this.removeParticipant(user);

		if (user.equals(this.getScreensharer())) {
			this.screensharer = null;
		}

		user.close();
	}

	/**
	 * Allows a participant to leave a room via its identifier.
	 * 
	 * @param userId
	 *            - Identifier of a participant
	 * @throws IOException
	 *             - on error leaving the room
	 */
	public void leave(String userId) throws IOException {
		Participant user = participants.get(userId);

		if (user != null)
			leave(user);
	}

	/**
	 * Allows a participant to join a room.
	 * 
	 * @param newParticipant
	 *            - Instance of Participant
	 */
	public void joinRoom(Participant newParticipant) {
		final JsonObject newParticipantMsg = new JsonObject();
		newParticipantMsg.addProperty("id", "newParticipantArrived");
		newParticipantMsg.addProperty("name", newParticipant.getName());
		newParticipantMsg.addProperty("userId", newParticipant.getId());
		broadcast(newParticipantMsg, newParticipant);
	}

	/**
	 * Send a Json message to a the participants of a room
	 * 
	 * @param message
	 *            - Instance of JsonObject
	 * @param exception
	 *            - ???
	 */
	public void broadcast(JsonObject message, Participant exception) {

		for (final Participant participant : participants.values()) {

			if (!(participant.equals(exception)) || participant instanceof WebUser) {
				try {
					participant.sendMessage(message);
				} catch (final IOException e) {
					log.debug("ROOM {}: participant {} could not be notified", name, participant.getName(), e);
				}
			}
		}
	}

	/**
	 * Send a Json message to all the participants of a room
	 * 
	 * @param message
	 *            - Instance of JsonObject
	 */
	public void broadcast(JsonObject message) {
		broadcast(message, null);
	}

	/**
	 * Remove a participant of the room and notify every members of this room.
	 * 
	 * @param participant
	 *            - Instance of Participant
	 * @throws IOException
	 *             - if the participant does not exist
	 */
	public void removeParticipant(Participant participant) throws IOException {
		participants.remove(participant.getId());

		boolean isScreensharer = (screensharer != null && participant.equals(screensharer));

		log.debug("ROOM {}: notifying all users that {} is leaving the room", this.name, participant.getName());

		final JsonObject participantLeftJson = new JsonObject();
		participantLeftJson.addProperty("id", "participantLeft");
		participantLeftJson.addProperty("userId", participant.getId());
		participantLeftJson.addProperty("name", participant.getName());
		participantLeftJson.addProperty("isScreensharer", isScreensharer);

		final JsonArray participantsArray = new JsonArray();

		for (final Participant p : this.getParticipants()) {
			final JsonElement participantName = new JsonPrimitive(p.getName());
			participantsArray.add(participantName);
		}
		participantLeftJson.add("data", participantsArray);

		for (final Participant p : participants.values()) {
			if (p instanceof WebUser) {
				if (isScreensharer)
					((WebUser) participant).cancelPresentation();

				p.sendMessage(participantLeftJson);
			}
		}

	}

	/**
	 * close the channel of video communication
	 * 
	 * 
	 * @throws IOException
	 *             - if there is no Participant
	 */
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

	/**
	 * send the names of all participants in the room exept the one who send it
	 * to an over user.
	 * 
	 * @param participant
	 *            - Instance of Participant
	 * @param id
	 *            id of the one who received the information
	 * @throws IOException
	 *             - if the participant does not exist
	 */
	public void sendInformation(Participant user, String id) throws IOException {

		final JsonArray participantsArray = new JsonArray();

		for (final Participant participant : this.getParticipants()) {
			if (!participant.equals(user)) {
				final JsonElement participantName = new JsonPrimitive(participant.getName());
				participantsArray.add(participantName);
			}
		}

		final JsonObject message = new JsonObject();
		message.addProperty("id", id);
		message.add("data", participantsArray);
		message.addProperty("existingScreensharer", (screensharer != null));

		if (line != null)
			message.addProperty("lineExtension", line.getExtension());

		if (screensharer != null) {
			message.addProperty("presenterId", screensharer.getId());
			message.addProperty("screensharer", screensharer.getName());
		}

		log.debug("PARTICIPANT {}: sending a list of {} participants", user.getName(), participantsArray.size());

		user.sendMessage(message);
	}

	/**
	 * @return a collection with all the participants in the room
	 */
	public Collection<Participant> getParticipants() {
		return participants.values();
	}

	/**
	 * @return all the information about the id
	 */
	public Participant getParticipant(String id) {
		return participants.get(id);
	}

	@Override
	public void close() {
		for (final Participant user : participants.values()) {
			try {
				user.close();
			} catch (IOException e) {
				log.debug("ROOM {}: Could not invoke close on participant {}", this.name, user.getName(), e);
			}
		}

		participants.clear();

		compositePipeline.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("ROOM {}: Released Composite Pipeline", Room.this.name);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("PARTICIPANT {}: Could not release Composite Pipeline", Room.this.name);
			}
		});

		presentationPipeline.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("ROOM {}: Released Presentation Pipeline", Room.this.name);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("PARTICIPANT {}: Could not release Presentation Pipeline", Room.this.name);
			}
		});

		log.debug("Room {} closed", this.name);
	}

	/**
	 * @return the composite pipeline
	 */
	public MediaPipeline getCompositePipeline() {
		return compositePipeline;
	}

	/**
	 * @return the presentation pipeline
	 */
	public MediaPipeline getPresentationPipeline() {
		return presentationPipeline;
	}

	/**
	 * change the one who share his screen
	 * 
	 * @param user
	 *            the new screensharer
	 */
	public void setScreensharer(WebUser user) {
		this.screensharer = user;
	}

	/**
	 * @return true if there is a sreensharer, false if not
	 */
	public boolean hasScreensharer() {
		return (screensharer != null);
	}

	/**
	 * @param the
	 *            new identification number to the room
	 */
	public long setCSeq(long cseq) {
		this.cseq = cseq;
		return cseq;
	}

	/**
	 * @return give an identification number to the room
	 */
	public long getCSeq() {
		return this.cseq;
	}

	public Line getLine() {
		return this.line;
	}

	/**
	 * @return received the identifier of the room
	 */
	public String getCallId() {
		return this.callId;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	/**
	 * inform if the room is in instance of closing
	 */
	public boolean isClosing() {
		return closing;
	}

	/**
	 * indicate that the room will be close in a few time
	 */
	public void setClosing() {
		this.closing = true;
	}

	/**
	 * @return the number of participant
	 */
	public int size() {
		return participants.size();
	}

	/**
	 * start the record and save it on a web page
	 */
	public void record() {
		log.info("Start Recording");
		this.recorderEndpoint = new RecorderEndpoint.Builder(getCompositePipeline(),
				"file:///record/" + getName() + ".mp4").withMediaProfile(MediaProfileSpecType.MP4).build();
		this.hubPort.connect(this.recorderEndpoint);
		this.recorderEndpoint.record();

	}

	/**
	 * stop the record
	 */
	public void stopRecord() {
		this.recorderEndpoint.stop();
		this.recorderEndpoint.release();
	}

	
	/**
	 * @return the screensharer
	 */
	public WebUser getScreensharer() {
		return screensharer;
	}

}
