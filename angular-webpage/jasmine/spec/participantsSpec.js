/**
* Test file for participants.js factory 
*
* We consider that getter is fully functionnal. The webSocket is used with angular 
* mocks.js. This testfile could be run with grunt without setting up application.
*/

describe('Unit testing for participants factory', function() {
   
var mock, participants;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()};
    
  angular.mock.module(function($provide) {
    $provide.value('socket', mock);
  });
    
  inject(function($injector) {
    participants = $injector.get('participants');
  });
    
});

it("verify participants factory injection", function() {
expect(participants).not.toBe(undefined);
}); 
 
it("verify the object participant",function() {
    //Call the add function and check basic property
    participants.add("145RBC","Jean");
    expect(participants.get("145RBC").name).not.toBe(undefined); 
    expect(participants.get("145RBC").name).toBe("Jean");
});

//Test for Remove() 
it("verify the participant remover - remove()", function() {
    participants.add("145RBC","Jean");
    expect(participants.get("145RBC")).not.toBe(undefined);
    participants.remove("145RBC");
    expect(participants.get("145RBC")).toBe(undefined);
});    
    
//Test for isEmpty()     
it("verify the emptyness checking - isEmpty()", function() {
    expect(participants.isEmpty()).toBe(true);
});
    
    
});