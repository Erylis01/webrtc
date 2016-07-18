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
		Participant pMocked2 = Mockito.mock(Participant.class);
		
		//Stubbing
		Mockito.when(pMocked.getId()).thenReturn("pMocked");
		Mockito.when(pMocked2.getId()).thenReturn("pMocked2");
		
		//Test size of the list participant and the value
		rtest.add(pMocked);
		Assert.assertEquals(1,participantSpy.size());
		Assert.assertTrue(participantSpy.contains(pMocked));
		
		//Test size of the list participant and the value
		rtest.add(pMocked2);
		Assert.assertEquals(2,participantSpy.size());
		Assert.assertTrue(participantSpy.contains(pMocked2)&& participantSpy.contains(pMocked));
		
		//Test if the element is null that it does nothing
		rtest.add(null);
		Assert.assertEquals(2,participantSpy.size());
		Assert.assertTrue(participantSpy.contains(pMocked2)&& participantSpy.contains(pMocked));

	}

	@Test
	public void testLeaveParticipant() throws IOException {
		// Initialization of the test
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room rtest = new Room ("roomtest",participantSpy);
		rtest = Mockito.spy(rtest);
		Participant pMocked = Mockito.mock(Participant.class);
		WebUser wuMocked = Mockito.mock(WebUser.class);
		participantSpy.put("pMocked", pMocked);
		
		//Stubbing
		Mockito.doNothing().when(rtest).removeParticipant(pMocked);
		Mockito.doNothing().when(pMocked).close();
		
		//Test that the method removeParticipant() and close() are called
		rtest.leave(pMocked);
		Mockito.verify(rtest).removeParticipant(pMocked);
		Mockito.verify(pMocked).close();
		
		//Test if the user is the screesharer 
		Mockito.when(rtest.getScreensharer()).thenReturn(wuMocked);
		rtest.leave(pMocked);
		System.out.println(pMocked.equals(wuMocked));
		System.out.println(rtest.getScreensharer());
		//Mockito.verify(rtest).removeParticipant(pMocked);
		//Mockito.verify(pMocked).close();
		Assert.assertTrue(rtest.getScreensharer().equals(null));
	}

	@Test
	public void testLeaveString() throws IOException {
		ConcurrentHashMap<String, Participant> participantSpy = Mockito.spy(ConcurrentHashMap.class);
		Room rtest = new Room ("roomtest",participantSpy);
		rtest = Mockito.spy(rtest);
		Participant pMocked = Mockito.mock(Participant.class);
		participantSpy.put("pMocked", pMocked);
		
		// Stubbing
		Mockito.when(pMocked.getId()).thenReturn("pMocked",(String)null);
		Mockito.doNothing().when(rtest).leave(pMocked);
		
		//Test if that the method leave(Participant) is called 
		rtest.leave("pMocked");
		Mockito.verify(rtest).leave(pMocked);
				
	}

	@Test
	public void testJoinRoom() {
		fail("Not yet implemented");
	}

	@Test
	public void testBroadcast() {
		fail("Not yet implemented");
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
