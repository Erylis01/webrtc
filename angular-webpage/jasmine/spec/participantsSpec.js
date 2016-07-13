var mock, participants;

beforeEach(angular.mock.module('app',['ngRoute', 'ng.deviceDetector', 'lumx','pascalprecht.translate']));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()};
    
  angular.mock.module(function($provide) {
    $provide.value('socket', mock);
  },['$window', 'variables']);
    
  inject(function($injector) {
    participants = $injector.get('participants');
  });
    
});


it("verify fs the room is empty", function() {
    expect(participants.isEmpty()).toBe(true);
});       
