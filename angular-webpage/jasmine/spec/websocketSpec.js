/**
* Test file for websocket.js factory 
*
* In charge of the server socket - Miss ever case when the 
* socket is open
*/

describe('Unit testing for websocket factory', function() {
   
var mock, websocket, variables;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()}; 

  console = {};    
  console.error = jasmine.createSpy();   
  console.log = jasmine.createSpy();   
  console.warn = jasmine.createSpy();   
    
    
  inject(function($injector) {
    websocket = $injector.get('socket'); 
    variables = $injector.get('variables');
  });
    
});

    
   //Check the factory injection
   it("verify factory injection", function() {
     expect(websocket).not.toBe(undefined);
     expect(variables).not.toBe(undefined);
   }); 

  //Test the function send()
    it('verify the sending ability - send(message)', function() {
    websocket.send(JSON.stringify({'A':'B'}));
    expect(console.warn).toHaveBeenCalledWith('Socket was closed before sending message'); 
    });
    
  //Test the function get()  
  it("verify the socket getter - get()", function() {
      expect(websocket.get().readyState).not.toBe(undefined);
  });
    
  //Test the function prepareJoiningRoom()  
  it("verify the ability to prepare a room entering - prepareJoiningRoom(m)", function() {
      var m = JSON.stringify({'A':'B'});
      websocket.prepareJoiningRoom(m);
      expect(websocket.getMessage()).toBe(m);
  });
    
  //Test the function roomReady()  
  it("verify the ability to signal that room is reasdy - roomReady())", function() {
      expect(websocket.getMessage()).toBe(null);
      var m = JSON.stringify({'A':'B'});
      websocket.prepareJoiningRoom(m);
      websocket.roomReady();
      expect(console.warn).toHaveBeenCalledWith('Socket was closed before sending message'); 
  }); 
    
  //Test the function isOpen()
  it("verify the open checker - isOpen()", function() {
      expect(websocket.isOpen()).toBeFalsy;
  });
});
