package cz.cvut.fel.webrtc.db;


import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.web.socket.WebSocketSession;

import cz.cvut.fel.webrtc.resources.WebUser;


/**
 * This class allows to test every methods of WebRegistry
 * 
 * @author Pierre Coquerel
 *
 */
public class WebRegistryTest {
	

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
	
	/**
	 *  Test of the method {@link cz.cvut.fel.webrtc.db.WebRegistry#getAll()}
	 */
	@Test
	public void getAll() {
		// Mock of the different class
		WebUser userMocked = Mockito.mock(WebUser.class);
		WebUser userMocked2 = Mockito.mock(WebUser.class);
		WebSocketSession sessionMocked = Mockito.mock(WebSocketSession.class);
		WebSocketSession sessionMocked2 = Mockito.mock(WebSocketSession.class);
		ConcurrentHashMap<String, WebUser> usersSpy = Mockito.spy(ConcurrentHashMap.class);
		WebRegistry wr = new WebRegistry(usersSpy);
		
		// Stubbing
		Mockito.when(userMocked.getSession()).thenReturn(sessionMocked);
		Mockito.when(sessionMocked.getId()).thenReturn("IdSession");
		Mockito.when(userMocked2.getSession()).thenReturn(sessionMocked2);
		Mockito.when(sessionMocked2.getId()).thenReturn("IdSession2");
		usersSpy.put("IdSession", userMocked);
		usersSpy.put("IdSession2", userMocked2);
		
		//Test if the size of the collection of user returned is correct
		Collection<WebUser> usersTest =wr.getAll();
		Assert.assertEquals(2,usersTest.size());
		
		//Test if the values of the collection of user returned is correct
		Assert.assertEquals(true, userMocked.equals(usersTest.toArray()[1]));
		Assert.assertEquals(true, userMocked2.equals(usersTest.toArray()[0]));
		
	}
}
