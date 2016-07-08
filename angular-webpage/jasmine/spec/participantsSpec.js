

describe("isEmpty", function() {
        it("verify is the room is empty", function(participants){
            expect(participants.isEmpty()).toBe(true);
        });       
});