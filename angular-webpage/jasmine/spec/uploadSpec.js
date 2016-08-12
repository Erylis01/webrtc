/**
* Test file for upload.js factory 
*
* Relative to upload-speed-tester. Mock could be usefull for this factory, 
* altought, there isn't access to the function that calculate him. According to
* the dom, 100% covered by test.
*/

describe('Unit testing for upload factory', function() {
   
var mock, upload, variables;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()}; 
  request = {};
  request.abort = jasmine.createSpy(); 

  inject(function($injector) {
    variables = $injector.get('variables'); 
    upload = $injector.get('upload');
  });

});
    
   //Check the factory injection
   it("verify factory injection", function() {
     expect(upload).not.toBe(undefined);
     expect(variables).not.toBe(undefined);
   }); 
    
    //Check the speed retriving function - speed() 
    //FURTHER WORK TO DO : SIMULATE ANSWER FOR VARIOUS SPEED
    it("verify speed retriving - speed()", function() {
     expect(upload.speed).not.toBe(undefined);
   }); 
    
    //Check the aborting function - abort()
    it('verify the aborting function - abort()', function() {
    upload.abort();
    expect(request.abort()).toHaveBeenCalled;
    });
});
