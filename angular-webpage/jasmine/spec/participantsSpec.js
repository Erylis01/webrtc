var participants;
beforeEach(angular.module('app'));
beforeEach(function() {
  angular.inject(function($injector) {
    participants = $injector.get('participants');
  });
});


it("verify fs the room is empty", function() {
    expect(participants.isEmpty()).toBe(true);
});       
