/**
* Test file for constraints.js factory 
*
* In charge of the user media. 
*/

describe('Unit testing for constraints factory', function() {
   
var mock, constraints, variables;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()};   

  inject(function($injector) {
    constraints = $injector.get('constraints'); 
    variables = $injector.get('variables');
    upload = $injector.get('upload');
  });

});
    
//Check the factory injection
it("verify factory injection", function() {
expect(constraints).not.toBe(undefined);
expect(variables).not.toBe(undefined);
expect(upload).not.toBe(undefined);
}); 

//Check the constrain getter function get()
it("verify the constraint getter function - get()", function() {
var constraintToTest = constraints.get();
expect(constraintToTest.audio).toBe(true);
expect(constraintToTest.video.width.min).toBe(160);
expect(constraintToTest.video.height.min).toBe(120);
});
    
//Check the resolution setter function 
it('verify the resolution setter function - setRes()', function () {
//First test to check the setting ability
constraints.setRes(160,120,false);
var constraintToTest = constraints.get();
expect(constraintToTest.video.width.ideal).toBe(160);
expect(constraintToTest.video.height.ideal).toBe(120);
//Second to verify that we can change it
constraints.setRes(320,240,false);
constraintToTest = constraints.get();
expect(constraintToTest.video.width.ideal).toBe(320);
expect(constraintToTest.video.height.ideal).toBe(240);
//Check the first case of auto-adjusment
constraints.setRes(160,120,true);
constraintToTest = constraints.get();
expect(constraintToTest.video.width.ideal).toBe(160);
expect(constraintToTest.video.height.ideal).toBe(120);
//Check the second case of auto-adjusment
upload.speed =  function () { return 0.6 };
constraints.setRes(160,120,true);
constraintToTest = constraints.get();
expect(constraintToTest.video.width.ideal).toBe(320);
expect(constraintToTest.video.height.ideal).toBe(240);
//Check the third case of auto-adjusment
upload.speed =  function () { return 0.8 };
constraints.setRes(160,120,true);
constraintToTest = constraints.get();
expect(constraintToTest.video.width.ideal).toBe(640);
expect(constraintToTest.video.height.ideal).toBe(480); 
});
    
});

