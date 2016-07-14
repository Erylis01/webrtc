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


it("verify the emptyness checking", function() {
    expect(participants.isEmpty()).toBe(true);
});       
});