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


it("verify fs the room is empty", function() {
    expect(participants.isEmpty()).toBe(true);
});       
