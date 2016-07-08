package cz.cvut.fel.webrtc.db;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

import cz.cvut.fel.webrtc.resources.WebUser;

public class WebRegistryTest {

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
	public void testRegister() {
		WebRegistry wr = new WebRegistry();
		WebUser user = Mockito.mock(WebUser.class);
		WebSocketSession session= Mockito.mock(WebSocketSession.class);
		System.out.println(user.getSession());
		//Mockito.when(user.getSession()).thenReturn("userID1");
		//String result = user.getSession().getId();
		//assertTrue(user.getSession().getId().equals(result));
		//ConcurrentHashMap<String, WebUser> users = new ConcurrentHashMap<>();
		//wr.register(user);
		//assertEquals("Les valeurs sont identiques",users.get("userID1"),user);
	}


	@Test
	public void testGetBySession() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveBySession() {
		fail("Not yet implemented");
	}

}
