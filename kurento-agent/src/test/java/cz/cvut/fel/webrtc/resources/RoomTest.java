package cz.cvut.fel.webrtc.resources;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;

import org.junit.Test;
import org.kurento.client.Composite;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.google.gson.JsonObject;

import org.junit.Assert;


public class RoomTest {
	

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testRoomString() {
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room r = new Room("roomtest",participantSpy);
		
		//Test that the roomName is correct
		Assert.assertTrue(r.getName().equals("roomtest"));
		
		//Test that the list of the participants is correct
		Assert.assertTrue(r.getParticipants().equals(participantSpy));
	}

	@Test
	public void testRoomStringKurentoClient() throws Exception {
		
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		KurentoClient kurentoMocked = Mockito.mock(KurentoClient.class);
		Composite composite=Mockito.mock(Composite.class);
		MediaPipeline compositeMocked = Mockito.mock(MediaPipeline.class);
		MediaPipeline presentationMocked = Mockito.mock(MediaPipeline.class);
		
		//Stubbing
		Mockito.when(kurentoMocked.createMediaPipeline()).thenReturn(compositeMocked, presentationMocked);

		//Test that the roomName is correct
		Room r = new Room ("roomTest",kurentoMocked,participantSpy);
		Assert.assertTrue(r.getName().equals("roomtest"));
		
		//Test that the composite pipeline is correct
		Assert.assertTrue(r.getCompositePipeline().equals(compositeMocked));
		Assert.assertTrue(r.getPresentationPipeline().equals(presentationMocked));
	}

	@Test
	public void testJoin() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room rtest = new Room ("roomTest",participantSpy);
		Participant pMocked = Mockito.mock(Participant.class);
		
		//Test size of the list participant and the value
		for (int i=1; i<5; i++){
			Mockito.when(pMocked.getId()).thenReturn("pMocked"+i);
			rtest.add(pMocked);
			Assert.assertEquals(i,participantSpy.size());
			Assert.assertTrue(participantSpy.containsKey("pMocked"+i));
		}
		
		//Test if the element is null that it does nothing
		participantSpy.clear();
		rtest.add(null);
		Assert.assertEquals(0,participantSpy.size());
		Assert.assertTrue(participantSpy.isEmpty());

	}

	@Test
	public void testLeaveParticipant() throws IOException {
		// Initialization of the test
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room rtest = new Room ("roomtest",participantSpy);
		rtest = Mockito.spy(rtest);
		Participant pMocked = Mockito.mock(Participant.class);
		WebUser wuMocked = Mockito.mock(WebUser.class);
		
		
		//Stubbing
		Mockito.doNothing().when(rtest).removeParticipant(pMocked);
		Mockito.doNothing().when(pMocked).close();
		Mockito.when(rtest.getScreensharer()).thenReturn(wuMocked);
		
		//Test that the method removeParticipant() and close() are called
		for (int i=1; i<5; i++){
			participantSpy.put("pMocked"+i, pMocked);
			rtest.leave(pMocked);
			Mockito.verify(rtest,Mockito.times(i)).removeParticipant(pMocked);
			Mockito.verify(pMocked,Mockito.times(i)).close();
		}
	}

	@Test
	public void testLeaveString() throws IOException {
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room rtest = new Room ("roomtest",participantSpy);
		rtest = Mockito.spy(rtest);
		Participant pMocked = Mockito.mock(Participant.class);
		
		//Test if that the method leave(Participant) is called 
		for (int i=1; i<5;i++){
			Mockito.when(pMocked.getId()).thenReturn("pMocked"+i);
			Mockito.doNothing().when(rtest).leave(pMocked);
			participantSpy.put("pMocked"+i, pMocked);
			rtest.leave("pMocked"+i);
			Mockito.verify(rtest,Mockito.times(i)).leave(pMocked);	
		}
	}

	@Test
	public void testJoinRoom() {
		fail("Not yet implemented");
		
	}

	@Test
	public void testBroadcast() {
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room rtest = new Room ("roomtest",participantSpy);
		Participant pMocked = Mockito.mock(Participant.class);
		JsonObject messageMocked = Mockito.mock(JsonObject.class);
		
		for (int i=1; i<6;i++){
			participantSpy.put("pMocked"+i,pMocked);
		}
		rtest.broadcast(messageMocked, pMocked);
	}

	@Test
	public void testCancelPresentation() {
		fail("Not yet implemented");
	}

	@Test
	public void testSendInformation() {
		fail("Not yet implemented");
	}

	@Test
	public void testClose() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasScreensharer() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsClosing() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetClosing() {
		fail("Not yet implemented");
	}

	@Test
	public void testSize() {
		fail("Not yet implemented");
	}

	@Test
	public void testRecord() {
		fail("Not yet implemented");
	}

	@Test
	public void testStopRecord() {
		fail("Not yet implemented");
	}

}
