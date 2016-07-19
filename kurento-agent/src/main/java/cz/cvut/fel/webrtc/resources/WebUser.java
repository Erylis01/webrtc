package cz.cvut.fel.webrtc.resources;

import com.google.gson.JsonObject;
import org.kurento.client.*;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Calendar;

public class WebUser extends Participant {

	private static final Logger log = LoggerFactory.getLogger(WebUser.class);

	private WebRtcEndpoint sharingMedia;
	private boolean isScreensharer = false;

	private final String userId;

	protected WebRtcEndpoint outgoingMedia;

	private final MediaPipeline compositePipeline;
	private final MediaPipeline presentationPipeline;
	private Calendar lastPing = Calendar.getInstance();

	private WebSocketSession session;

	/**
	 * Constructor of the class WebUser
	 * 
	 * @param id
	 *            - identifier of the participant
	 * @param roomName
	 *            - this is the name user want to give to the room
	 * @param session
	 *            - conversation between two web socket endpoints
	 * @param compositePipeline
	 *            - container of MediaElements, canal from kurento to user
	 * @param presentationPipeline
	 *            - container of MediaElements, canal from user to kurento
	 * @param hub
	 *            - Routing MediaObject, connect several endpoints together
	 */
	public WebUser(final String id, String roomName, WebSocketSession session, MediaPipeline compositePipeline,
			MediaPipeline presentationPipeline, Hub hub) {
		super(id, roomName, session, compositePipeline, presentationPipeline, hub);

		this.userId = id;
		this.session = session;
		this.compositePipeline = compositePipeline;

		newOutgoingMedia();
		connectOutgoingMediaToHubPort();

		this.presentationPipeline = presentationPipeline;
	}

	/**
	 * Allows to connect a WebRTCEndpoint to a Hub port
	 */
	private void connectOutgoingMediaToHubPort() {
		outgoingMedia.connect(hubPort);
		hubPort.connect(outgoingMedia);
	}

	/**
	 * Allows to disconnect a WebRTCEndpoint to a Hub port
	 */
	private void disconnectOutgoingMediaFromHubPort() {
		hubPort.disconnect(outgoingMedia,
				asyncLog("Disconnect hubPort from outgoingMedia", "Could not disconnect hubPort from outgoingMedia"));
		outgoingMedia.disconnect(hubPort,
				asyncLog("Disconnect outgoingMedia from hubPort", "Could not disconnect ougoingMedia from hubPort"));
	}

	/**
	 * Allow to renew an WebRTCEndpoint and connect it to a hub port
	 */
	public void renewOutgoingMedia() {
		super.renewHubPort();
		releaseOutgoingMedia();
		newOutgoingMedia();
		connectOutgoingMediaToHubPort();
	}

	/**
	 * Allow to return the name of the participant
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Allow to create a new WebRTCEndpoint
	 */
	private void newOutgoingMedia() {
		this.outgoingMedia = new WebRtcEndpoint.Builder(compositePipeline).build();

		this.outgoingMedia.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

			@Override
			public void onEvent(OnIceCandidateEvent event) {

				JsonObject response = new JsonObject();
				response.addProperty("id", "iceCandidate");
				response.addProperty("userId", userId);
				response.addProperty("type", "composite");
				response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
				try {
					synchronized (session) {
						session.sendMessage(new TextMessage(response.toString()));
					}
				} catch (IOException e) {
					log.debug(e.getMessage());
				}
			}
		});
	}

	/**
	 * 
	 * @return - WebRTCEndpoint
	 */
	public WebRtcEndpoint getOutgoingWebRtcPeer() {
		return outgoingMedia;
	}

	/**
	 * Allows to set a WebRtcEndpoint
	 * 
	 * @param sharingMedia
	 *            - sharing WebRtcEndpoint
	 */
	public void setSharingMedia(WebRtcEndpoint sharingMedia) {
		this.sharingMedia = sharingMedia;
	}

	/**
	 * Allows to get a WebRtcEndpoint
	 * 
	 * @return - sharing WebRTCEndpoint
	 */
	public WebRtcEndpoint getSharingMedia() {
		return this.sharingMedia;
	}

	/**
	 * Send a JsonObject to run the javascript function receiveVideoAnswer
	 * 
	 * @param sender
	 *            - instance of a WebUser
	 * @param type
	 *            - String equals to null or composite
	 * @param sdpOffer
	 *            - SessionSpec offer from the remote User Agent
	 * @param room
	 *            - instance of the class Room
	 * @throws IOException
	 */
	public void receiveVideoFrom(WebUser sender, String type, String sdpOffer, Room room) throws IOException {
		log.info("USER {}: connecting with {} in room {}", this.name, sender.getName(), this.roomName);

		log.trace("USER {}: SdpOffer for {} is {}", this.name, sender.getName(), sdpOffer);

		WebRtcEndpoint ep = this.getEndpointForUser(sender, type, room);

		try {
			if (ep.getLocalSessionDescriptor() != null)
				return;
		} catch (Exception e) {
		}

		final String ipSdpAnswer = ep.processOffer(sdpOffer);

		log.trace("USER {}: SdpAnswer for {} is {}", this.name, sender.getName(), ipSdpAnswer);

		log.debug("gather candidates");
		ep.gatherCandidates();

		final JsonObject scParams = new JsonObject();
		scParams.addProperty("id", "receiveVideoAnswer");
		scParams.addProperty("userId", sender.getId());
		scParams.addProperty("name", sender.getName());
		scParams.addProperty("sdpAnswer", ipSdpAnswer);
		scParams.addProperty("type", type);
		this.sendMessage(scParams);
	}

	/**
	 * Run the javascript fonction ICEcandidate and the fonction
	 * OnPresenterReady
	 * 
	 * @param sender
	 *            - the user
	 * 
	 * @return the Endpoint used to receive media from a certain user
	 */
	private WebRtcEndpoint getEndpointForUser(final WebUser sender, final String type, Room room) {

		if (!type.equals("composite")) {
			if ((this.isScreensharer && this.equals(sender)) || (sender.isScreensharer)) {

				if (this.sharingMedia == null) {

					this.sharingMedia = new WebRtcEndpoint.Builder(presentationPipeline).build();

					final Participant presenter = (this.isScreensharer) ? this : sender;

					this.sharingMedia.addOnIceCandidateListener(new EventListener<OnIceCandidateEvent>() {

						@Override
						public void onEvent(OnIceCandidateEvent event) {
							JsonObject response = new JsonObject();
							response.addProperty("id", "iceCandidate");
							response.addProperty("name", presenter.getName());
							response.addProperty("userId", presenter.getId());
							response.addProperty("type", type);
							response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
							try {
								synchronized (session) {
									session.sendMessage(new TextMessage(response.toString()));
								}
							} catch (IOException e) {
								log.debug(e.getMessage());
							}
						}
					});

					if (this.isScreensharer && this.equals(sender)) {
						final JsonObject newPresenterMsg = new JsonObject();
						newPresenterMsg.addProperty("id", "presenterReady");
						newPresenterMsg.addProperty("userId", this.getId());
						newPresenterMsg.addProperty("presenter", this.getName());

						room.broadcast(newPresenterMsg);
					}
				}

				if (!this.isScreensharer)
					sender.getSharingMedia().connect(sharingMedia);

				return this.sharingMedia;

			}
		}

		return this.getOutgoingWebRtcPeer();
	}

	/**
	 * Allow to cancel the presentation by releasing the sharing WebRtcEndpoint
	 */
	public void cancelPresentation() {
		log.debug("PARTICIPANT {}: canceling presentation reception", this.getName());
		log.debug("PARTICIPANT {}: removing endpoint", this.getName());

		releaseSharingMedia();
	}

	/**
	 * Process an ICE candidate sent by the remote peer of the connection.
	 * 
	 * @param e
	 *            - remote ICE candidate
	 * @param type
	 *            - String equals to null or composite
	 */
	public void addCandidate(IceCandidate e, String type) {

		WebRtcEndpoint ep = (type.equals("composite")) ? this.getOutgoingWebRtcPeer() : sharingMedia;

		if (ep != null)
			ep.addIceCandidate(e);
	}

	/**
	 * @return - boolean to know if the screen is shared
	 */
	public boolean isScreensharer() {
		return this.isScreensharer;
	}

	/**
	 * 
	 * @param b
	 *            - boolean to know if the screen is shared
	 */
	public void isScreensharer(boolean b) {
		this.isScreensharer = b;
	}

	@Override
	public void close() throws IOException {

		log.debug("PARTICIPANT {}: Releasing resources", this.getName());
		super.releaseHubPort();

		releaseOutgoingMedia();
		releaseSharingMedia();
	}

	/**
	 * 
	 * disconnetion from the hub
	 */
	private void releaseOutgoingMedia() {
		if (outgoingMedia != null) {
			outgoingMedia.release(asyncLog("Released outgoing endpoint", "Could not release sharing endpoint"));
			outgoingMedia = null;
		}
	}

	/**
	 * 
	 * disconnetion the webrtc endpoint
	 */
	private void releaseSharingMedia() {
		if (sharingMedia != null) {
			sharingMedia.release(asyncLog("Released sharing endpoint", "Could not release sharing ep"));
			sharingMedia = null;
		}
	}

	/**
	 * 
	 * @return - a Calender
	 */
	public Calendar getLastPing() {
		return lastPing;
	}

	/**
	 * Set a Calendar
	 * 
	 * @param now
	 */
	public void setLastPing(Calendar now) {
		this.lastPing = now;
	}

	/**
	 * 
	 * 
	 * @param success
	 * @param error
	 * 
	 * @return
	 */
	public Continuation<Void> asyncLog(final String success, final String error) {
		return new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("PARTICIPANT {}: {}", WebUser.this.getName(), success);
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("PARTICIPANT {}: {}", WebUser.this.getName(), error);
			}
		};
	}
}
