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
        
  console = {};    
  console.error = jasmine.createSpy();   
  console.log = jasmine.createSpy();   
  console.warn = jasmine.createSpy();
    
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
upload.speed =  function () { return 0.6 }
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

//Check the composite options setter function - setCompositeOption(opt)
it('verify the composite options setter - setCompositeOption(opt)', function () {
 constraints.setCompositeOptions('normal');
 expect(console.log).toHaveBeenCalledWith('normal');
 constraints.setCompositeOptions('audioOnly');
 expect(console.log).toHaveBeenCalledWith('audioOnly');
 constraints.setCompositeOptions('watchOnly');
 expect(console.log).toHaveBeenCalledWith('watchOnly');
 constraints.setCompositeOptions('notPredicted');
 expect(console.log).not.toHaveBeenCalled;
});

//Check the type getter - getType()
it('verify the type getter - getType()', function() {
expect(constraints.getType()).toBe('composite');    
});  
    
//Check the type setter - setType()
it('verify the setter - setType()', function() {
constraints.setType('random');
expect(constraints.getType()).toBe('random');
});

//Check the chrome extension detecter for screensharing - chromeExtensionDetected()
it('verify the chrome extension detecter for screensharing - chromeExtensionDetected()', function () {
constraints.chromeExtensionDetected();
expect(console.log).toHaveBeenCalledWith(true);
});
    
//Check the chrome extension installed getter - isChromeExtensionInstalled()
it('verify the chrome extension installed getter - isChromeExtensionInstalled()', function() {
expect(constraints.isChromeExtensionInstalled()).toBeFalsy;                                    
constraints.chromeExtensionDetected();
expect(constraints.isChromeExtensionInstalled()).toBeTruthy;
});

//Check the warning getter - getWarning()
it('verify the warning getter', function() {
expect(constraints.getWarning()).toBeNull;   
});

//Check the warning setter - setWarning(w)
it('verify the warning setter - setWarning(w)', function () {
constraints.setWarning('error');
expect(constraints.getWarning()).toBe('error');
});

//Check the ID setter - setId(id)
it('verify the id setter for chrome', function() {
constraints.setId('145872');
expect(console.log).toHaveBeenCalledWith('145872');
});
    
//Check all the public variable declared in the DOM
it('verify the accesibility of the variable declared in the DOM', function() {
expect(constraints.browser).toBeDefined;
expect(constraints.browserIsChrome).toBeFalsy;
expect(constraints.browserIsFirefox).toBeFalsy;
expect(constraints.canPresent).toBeFalsy;
});
    
});

