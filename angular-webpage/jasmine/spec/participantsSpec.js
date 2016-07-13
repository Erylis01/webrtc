var participants;

beforeEach(function() {
  inject(function($injector) {
    participants = $injector.get('participants');
  });
});


it("verify fs the room is empty", function() {
    expect(participants.isEmpty()).toBe(true);
});       
