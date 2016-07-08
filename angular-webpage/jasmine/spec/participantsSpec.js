

describe("get", function() {
        it("retrieve an user", function(){
            pariticipants.add(14587,"Jean")
            expect(get(14587).name).toEqual("Jean");
        });       
});