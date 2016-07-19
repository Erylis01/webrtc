/**
* Test file for participants.js factory 
*
* We consider that getter is fully functionnal. The webSocket is used with angular 
* mocks.js. This testfile could be run with grunt without setting up application.
*/

describe('Unit testing for participants factory', function() {
   
var mock, participants, socket;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()};
    
  console = {};    
  console.error = jasmine.createSpy();   
  console.log = jasmine.createSpy();   
  console.warn = jasmine.createSpy();      
    
  inject(function($injector) {
    participants = $injector.get('participants');
    socket = $injector.get('socket');  
  });
    
});

it("verify factory injection", function() {
expect(participants).not.toBe(undefined);
expect(socket).not.toBe(undefined);
}); 
    
it("verify the object participant",function() {
    //Call the add function and check basic property
    participants.add("145RBC","Jean");
    expect(participants.get("145RBC").name).not.toBe(undefined);
    expect(participants.get("145RBC").name).toBe("Jean");
    
    //Verify the offer method
    participants.get("145RBC").offer("composite", true, "A", "B");
    expect(console.error).toHaveBeenCalledWith('sdp offer error'); 
    participants.get("145RBC").offer("composite", false, "A", "B");
    expect(console.warn).toHaveBeenCalledWith('Socket was closed before sending message'); 
    
    //No test for offerToReceive beacause it's only call offer
    
    //Verify the iceCandidate
    participants.get("145RBC").iceCandidate("composite","A","B");
    expect(console.warn).toHaveBeenCalledWith('Socket was closed before sending message'); 
    ('Socket was closed before sending message'); 
    //No test for onIce Candidate because it's only call iceCandidate
    
    //No test or the remaiining method because they only call native dispose function (see clear test behind to be convaince)
});

//Test for Remove() 
it("verify the participant remover - remove()", function() {
    participants.add("145RBC","Jean");
    expect(participants.get("145RBC")).not.toBe(undefined);
    participants.remove("145RBC");
    expect(participants.get("145RBC")).toBe(undefined);
});   
    
//Test for me()
it("verify the auto-retriever function - me()", function() {
    participants.add("145RBC","Jean");
    expect(participants.get("145RBC")).not.toBe(undefined);
    expect(participants.me().name).toBe("Jean");
    expect(participants.me().name).not.toBe("Henry");
});
    
//Test for isEmpty()     
it("verify the emptyness checking - isEmpty()", function() {
    expect(participants.isEmpty()).toBe(true);
});

//Test for clear()
it("verify the clearing of participants - clear()", function() {    
    participants.add("145RBC","Jean");
    expect(participants.get("145RBC")).not.toBe(undefined);
    participants.add("145RBD","Jeannot");
    expect(participants.get("145RBD")).not.toBe(undefined);
    participants.add("145RBE","Jean-Kevin");
    expect(participants.get("145RBE")).not.toBe(undefined);
    participants.clear();
    expect(participants.isEmpty()).toBe(true);
});
    
    
});