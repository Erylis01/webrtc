var participants;
beforeEach(angular.module('/src/app.js'));
beforeEach(function() {
  angular.inject(function($injector) {
    participants = $injector.get('participants');
  });
});


it("verify fs the room is empty", function() {
    expect(participants.isEmpty()).toBe(true);
});       
