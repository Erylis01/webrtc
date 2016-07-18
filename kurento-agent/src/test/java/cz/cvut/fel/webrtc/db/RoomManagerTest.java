package cz.cvut.fel.webrtc.db;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import org.kurento.client.KurentoClient;
import org.kurento.client.MediaPipeline;

import cz.cvut.fel.webrtc.resources.Room;


/**
 * This class allows to test every methods of RoomManager
 * 
 * @author Pierre Coquerel
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({RoomManager.class })
public class RoomManagerTest {

	private Room roomMocked ;
	private ConcurrentHashMap<String, Room> roomsSpy;
	private RoomManager rm;
	
	/**
	 * Set up all the test
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.roomMocked = PowerMockito.mock(Room.class);
		this.roomsSpy = Mockito.spy(ConcurrentHashMap.class);
		this.rm = new RoomManager(roomsSpy);
	}
	
	
	/**
	 * Test of the method {@link cz.cvut.fel.webrtc.db.RoomManager#getRoom(String roomName}
	 */
	@Test
	public void testGetRoomString() throws Exception {
		
		// Stubbing		
		PowerMockito.whenNew(Room.class).withAnyArguments().thenReturn(roomMocked);
		//PowerMockito.whenNew(Room.class).withArguments("roomName",kurento).thenReturn(roomMocked);
		
		// Test that it's returning the room if the room is in the list rooms
		roomsSpy.put("roomName", roomMocked);
		Assert.assertEquals(true, rm.getRoom("roomName")==roomMocked);
		roomsSpy.remove("roomName");
		
		// Test if the room name is not null, returned the room, add it to the list of rooms
		Assert.assertEquals(true,rm.getRoom("roomName")==roomMocked);
		Assert.assertEquals(1, roomsSpy.size());
	}
	

	@Test
	public void testGetRoomStringBoolean() throws Exception {
		
		//Stubbing
		PowerMockito.whenNew(Room.class).withAnyArguments().thenReturn(roomMocked);
		
		// Test that it's returning the room if the room is in the list rooms
		roomsSpy.put("roomName", roomMocked);
		Assert.assertEquals(true, rm.getRoom("roomName", false)==roomMocked);
		roomsSpy.remove("roomName");
		
		// Test if the room name is not null and the boolean true that it's returning the room, adding it to the list of rooms
		Assert.assertEquals(true,rm.getRoom("roomName", true)==roomMocked);
		Assert.assertEquals(1, roomsSpy.size());
	}

	@Test
	public void testRemoveRoom() {
		
		MediaPipeline presentationMocked = Mockito.mock(MediaPipeline.class);
		MediaPipeline compositeMocked = Mockito.mock(MediaPipeline.class);
		
		//Stubbing
		Mockito.when(roomMocked.getName()).thenReturn("roomName");
		Mockito.when(roomMocked.getCompositePipeline()).thenReturn(compositeMocked);
		Mockito.when(roomMocked.getPresentationPipeline()).thenReturn(presentationMocked);
		
		//Test that it's removing the room of the rooms list
		roomsSpy.put("roomName", roomMocked);
		Assert.assertEquals(true,rm.removeRoom(roomMocked)==roomMocked);
		Assert.assertEquals(0, roomsSpy.size());
	}

}
