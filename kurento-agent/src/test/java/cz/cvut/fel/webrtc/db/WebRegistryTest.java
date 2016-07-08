package cz.cvut.fel.webrtc.db;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
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
		ConcurrentHashMap<String, WebUser> users = new ConcurrentHashMap<>();
		users.put("sessionId", user);
		user.setSession(session);
		Mockito.when(user.getSession()).thenReturn(session);
		Mockito.when(session.getId()).thenReturn("sessionId");
		wr.register(user);
		assertTrue("Les valeurs sont identiques",wr.getUser("sessionId").equals(users.get("sessionId")));
	}


	@Test
	public void testGetBySession() {
		WebRegistry wr = new WebRegistry();
		WebUser user = Mockito.mock(WebUser.class);
		WebSocketSession session= Mockito.mock(WebSocketSession.class);
		Mockito.when(user.getSession()).thenReturn(session);
		Mockito.when(session.getId()).thenReturn("sessionId");
		wr.register(user);
		assertTrue("Les valeurs sont identiques", wr.getBySession(session).equals(user));
	}

	@Test
	public void testRemoveBySession() {
		WebRegistry wr = new WebRegistry();
		WebUser usera = Mockito.mock(WebUser.class);
		WebSocketSession sessiona= Mockito.mock(WebSocketSession.class);
		Mockito.when(usera.getSession()).thenReturn(sessiona);
		Mockito.when(sessiona.getId()).thenReturn("sessiona_Id");
		WebUser userb = Mockito.mock(WebUser.class);
		WebSocketSession sessionb= Mockito.mock(WebSocketSession.class);
		Mockito.when(userb.getSession()).thenReturn(sessionb);
		Mockito.when(sessionb.getId()).thenReturn("sessionb_Id");
		wr.register(usera);
		wr.register(userb);
		wr.removeBySession(sessiona);
		
		// Test taille de la liste :
		assertEquals(1,wr.getAll().size());
		wr.register(usera);
		assertEquals(2,wr.getAll().size());
		
		// Test de la présence de l'élément :

	}

}
