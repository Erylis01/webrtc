package cz.cvut.fel.webrtc.db;

import static org.junit.Assert.*;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.socket.WebSocketSession;

import cz.cvut.fel.webrtc.resources.WebUser;

/**
 * This class allows to test every methods of WebRegistry
 * 
 * @author Pierre Coquerel
 *
 */
public class WebRegistryTest {
	

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		
	}


	/**
	 *  Test of the method {@link cz.cvut.fel.webrtc.db.WebRegistry#Register(WebUser)}
	 */
	@Test
	public void testRegister() {
		
		// Initialization of the test 
		WebUser userMocked = Mockito.mock(WebUser.class);
		WebSocketSession sessionMocked = Mockito.mock(WebSocketSession.class);
		ConcurrentHashMap<String, WebUser> usersSpy = Mockito.spy(ConcurrentHashMap.class);
		WebRegistry wr = new WebRegistry(usersSpy);
		
		//stubbing
		Mockito.when(userMocked.getSession()).thenReturn(sessionMocked);
		Mockito.when(sessionMocked.getId()).thenReturn("IdSession");
		
		// Test if the size is correct :
		wr.register(userMocked);
		Assert.assertEquals("The size of the list is correct",1, usersSpy.size());
		
		// Test si la valeur est bonne :
		Assert.assertEquals(true, usersSpy.get("IdSession").equals(userMocked));
	}

	/**
	 *  Test of the method {@link cz.cvut.fel.webrtc.db.WebRegistry#GetBySession(WebSocketSession)}
	 */
	@Test
	public void testGetBySession() {
		
		// Mock of the different class
		WebUser userMocked = Mockito.mock(WebUser.class);
		WebSocketSession sessionMocked = Mockito.mock(WebSocketSession.class);
		ConcurrentHashMap<String, WebUser> usersSpy = Mockito.spy(ConcurrentHashMap.class);
		WebRegistry wr = new WebRegistry(usersSpy);
		
		// Stubbing
		Mockito.when(userMocked.getSession()).thenReturn(sessionMocked);
		Mockito.when(sessionMocked.getId()).thenReturn("IdSession");
		usersSpy.put("IdSession", userMocked);
		
		
		// Test if the value returned is correct 
		Assert.assertEquals(true, wr.getBySession(sessionMocked).equals(userMocked));
		
	}
	
	/**
	 *  Test of the method {@link cz.cvut.fel.webrtc.db.WebRegistry#RemoveBySession(WebSocketSession)}
	 */
	@Test
	public void testRemoveBySession() {
		
		// Mock of the different class
		WebUser userMocked = Mockito.mock(WebUser.class);
		WebSocketSession sessionMocked = Mockito.mock(WebSocketSession.class);
		ConcurrentHashMap<String, WebUser> usersSpy = Mockito.spy(ConcurrentHashMap.class);
		WebRegistry wr = new WebRegistry(usersSpy);

		// Stubbing
		Mockito.when(userMocked.getSession()).thenReturn(sessionMocked);
		Mockito.when(sessionMocked.getId()).thenReturn("IdSession");
		usersSpy.put("IdSession", userMocked);
		
		// Test if the size of the List is correct
		WebUser removedUser =wr.removeBySession(sessionMocked);
		Assert.assertEquals("The size of the list is correct",0, usersSpy.size());
		
		// Test if the value of the removed user is good
		Assert.assertEquals(true,removedUser.equals(userMocked));
		
	}
}
