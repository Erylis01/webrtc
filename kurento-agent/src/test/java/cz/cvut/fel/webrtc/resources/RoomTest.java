package cz.cvut.fel.webrtc.resources;

import static org.junit.Assert.fail;

import java.util.concurrent.ConcurrentMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kurento.client.Composite;
import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.junit.Assert;


public class RoomTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRoomString() {
		ConcurrentMap<String, Participant> participantSpy = Mockito.spy(ConcurrentMap.class);
		Room r = new Room("roomtest",participantSpy);
		
		//Test that the roomName is correct
		Assert.assertTrue(r.getName().equals("roomtest"));
		
		//Test that the list of the participants is correct
		Assert.assertTrue(r.getParticipants().equals(participantSpy));
	}

	@Test
	public void testRoomStringKurentoClient() throws Exception {
		ConcurrentMap<String, Participant> participantSpy = Mockito.spy(ConcurrentMap.class);
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
		ConcurrentMap<String, Participant> participantSpy = Mockito.spy(ConcurrentMap.class);
		Room r = new Room ("roomtest",participantSpy);
		Participant pMocked = Mockito.mock(Participant.class);
		Participant pbMocked = Mockito.mock(Participant.class);
		
		//Stubbing
		Mockito.when(pMocked.getId()).thenReturn("participanttest");
		Mockito.when(pbMocked.getId()).thenReturn("participanttest2");
		
		//Test if the room size and the participant of is correct 
		r.add(pMocked);
		System.out.println(r.getParticipants().size());
		Assert.assertEquals(1,r.getParticipants().size());
		Assert.assertTrue(r.getParticipants().containsValue(pMocked));
		
		r.add(pbMocked);
		Assert.assertEquals(2,r.getParticipants());
		Assert.assertTrue(r.getParticipants().containsValue(pbMocked)&& r.getParticipants().containsValue(pMocked));
		
	}

	@Test
	public void testLeaveParticipant() {
		fail("Not yet implemented");
	}

	@Test
	public void testLeaveString() {
		Participant pMocked = Mockito.mock(Participant.class);
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
