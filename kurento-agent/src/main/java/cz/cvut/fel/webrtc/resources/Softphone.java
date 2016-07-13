package cz.cvut.fel.webrtc.resources;

import org.kurento.client.Continuation;
import org.kurento.client.Hub;
import org.kurento.client.MediaPipeline;
import org.kurento.client.RtpEndpoint;
import org.kurento.client.internal.server.KurentoServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class Softphone extends Participant {

	private static final Logger log = LoggerFactory.getLogger(Softphone.class);

	private final RtpEndpoint rtpEndpoint;

	/**
	 * Constructor of the class Softphone
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
	public Softphone(String id, String roomName, WebSocketSession session, MediaPipeline compositePipeline,
			MediaPipeline presentationPipeline, Hub hub) {
		super(id, roomName, session, compositePipeline, presentationPipeline, hub);

		rtpEndpoint = new RtpEndpoint.Builder(compositePipeline).build();

		rtpEndpoint.connect(hubPort);
		hubPort.connect(rtpEndpoint);
	}

	/**
	 * Return the chosen configuration from the ones stated in the SDP offer
	 * 
	 * @param sdpOffer
	 *            - SessionSpec offer from the remote User Agent
	 * 
	 * @return - SessionSpec answer from the remote User Agent
	 */
	public String getSdpAnswer(String sdpOffer) {
		final String sdpAnswer = rtpEndpoint.processOffer(sdpOffer);
		log.trace("USER {}: SdpAnswer for rtp is {}", this.name, sdpAnswer);
		return sdpAnswer;
	}

	/**
	 * @return - Endpoint that provides bidirectional content delivery
	 *         capabilities
	 */
	public RtpEndpoint getRtpEndpoint() {
		return rtpEndpoint;
	}

	/**
	 * Allows to return the last agreed SessionSpec or to generate SDP offer
	 * with media capabilities of the Endpoint.
	 * 
	 * @return - The last agreed SessionSpec
	 */
	public String getGeneratedOffer() {

		String offer = null;

		try {
			offer = rtpEndpoint.getLocalSessionDescriptor();
		} catch (KurentoServerException e) {
		}

		if (offer == null)
			offer = rtpEndpoint.generateOffer();

		return offer;
	}

	@Override
	public void close() throws IOException {
		log.debug("PARTICIPANT {}: Releasing resources", this.getName());
		super.releaseHubPort();

		rtpEndpoint.release(new Continuation<Void>() {

			@Override
			public void onSuccess(Void result) throws Exception {
				log.trace("PARTICIPANT {}: Released outgoing EP", Softphone.this.getName());
			}

			@Override
			public void onError(Throwable cause) throws Exception {
				log.warn("USER {}: Could not release outgoing EP", Softphone.this.getName());
			}
		});
	}

}
