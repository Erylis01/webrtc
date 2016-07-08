

describe("isEmpty", function() {
        it("verify is the room is empty", function() {
            inject(fuction(participants));
            expect(participants.isEmpty()).toBe(true);
        });       
});