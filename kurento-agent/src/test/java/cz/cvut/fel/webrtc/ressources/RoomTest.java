package cz.cvut.fel.webrtc.ressources;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import com.google.gson.JsonObject;

import cz.cvut.fel.webrtc.resources.Participant;
import cz.cvut.fel.webrtc.resources.Room;
import cz.cvut.fel.webrtc.resources.WebUser;

public class RoomTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testRoomString() {
		Room room = new Room("roomTest");

		// Test that the room is correctly created
		Assert.assertTrue(room !=null);
		Assert.assertTrue(room.getName().equals("roomTest"));
		Assert.assertTrue(!(room.getCallId().isEmpty()));
	}

	@Test
	public void testRoomStringKurentoClient() {
		fail("Not yet implemented");
	}

	@Test
	public void testJoin() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		Room room = new Room("roomtest");
		Participant pMocked = Mockito.mock(Participant.class);

		for (int i = 0; i < 5; i++) {
			Mockito.when(pMocked.getId()).thenReturn("pMocked" + i);
			room.add(pMocked);

			// Test that the size of the list increase and the values are
			// correct
			Assert.assertEquals(i + 1, room.getParticipants().size());
			Assert.assertTrue(room.getParticipant("pMocked" + i).equals(pMocked));
		}
	}

	@Test
	public void testLeaveParticipant() throws IOException {
		Participant pMocked = Mockito.mock(Participant.class);
		Room room = new Room ("roomTest");
		room=Mockito.spy(room);

		//Stubbing :
		Mockito.doNothing().when(room).removeParticipant(pMocked);
		Mockito.doNothing().when(pMocked).close();
		
		//Test if the different method are called
		for (int i=1;i<6;i++){
		room.leave(pMocked);
		Mockito.verify(room,Mockito.times(i)).removeParticipant(pMocked);
		Mockito.verify(pMocked,Mockito.times(i)).close();
		}
	}

	@Test
	public void testLeaveString() throws IOException {
		Participant pMocked = Mockito.mock(Participant.class);
		Room room = new Room ("roomTest");
		room=Mockito.spy(room);
		
		//Stubbing :
		Mockito.when(room.getParticipantbyId("pMocked")).thenReturn(pMocked);
		Mockito.doNothing().when(room).leave(pMocked);
		
		//Test that leave user is called
		for (int i=1 ; i<6; i++){
			room.leave("pMocked");
			Mockito.verify(room,Mockito.times(i)).leave(pMocked);
		}
	}

	@Test
	public void testJoinRoom() throws Exception {
		fail("Not yet implemented");
	}

	@Test
	public void testBroadcast() throws IOException {
		Participant pMocked = Mockito.mock(Participant.class);
		Participant pMocked2 = Mockito.mock(Participant.class);
		WebUser wuMocked = Mockito.mock(WebUser.class);
		WebUser wuMocked2 = Mockito.mock(WebUser.class);
		JsonObject message = new JsonObject();
		Room room = new Room ("roomTest");
		room=Mockito.spy(room);
		
		// Stubbing
		Mockito.doNothing().when(pMocked).sendMessage(message);
		Mockito.when(pMocked.getId()).thenReturn("pMocked");
		
		Mockito.doNothing().when(pMocked2).sendMessage(message);
		Mockito.when(pMocked2.getId()).thenReturn("pMocked2");
		
		Mockito.doNothing().when(wuMocked).sendMessage(message);
		Mockito.when(wuMocked.getId()).thenReturn("wuMocked");
		
		Mockito.doNothing().when(wuMocked2).sendMessage(message);
		Mockito.when(wuMocked2.getId()).thenReturn("wuMocked2");
		
		Mockito.doCallRealMethod().when(room).getParticipants();
					
		//test if the !(participant=Webuser) and  (participant=exeption)
		room.add(pMocked);
		room.broadcast(message, pMocked);
		Mockito.verify(pMocked,Mockito.never()).sendMessage(message);
		
		//Test if the (participant=Webuser) and  !(participant=exeption)
		room.broadcast(message,wuMocked);
		Mockito.verify(wuMocked,Mockito.never()).sendMessage(message);
		
		//test if the !(participant=Webuser) and  !(participant=exeption)
		room.broadcast(message, pMocked2);
		Mockito.verify(pMocked2,Mockito.never()).sendMessage(message);
		
		//Test if the (participant=Webuser) and  (participant=exeption)
		room.add(wuMocked2);
		room.broadcast(message,wuMocked2);
		Mockito.verify(wuMocked2,Mockito.times(1)).sendMessage(message);
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
	public void testGetParticipants() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetParticipant() {
		fail("Not yet implemented");
	}

	@Test
	public void testClose() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCompositePipeline() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetPresentationPipeline() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetScreensharer() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasScreensharer() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCSeq() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCSeq() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetLine() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetCallId() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetLine() {
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
