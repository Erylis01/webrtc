/**
* Test file for upload.js factory 
*
* Relative to upload-speed-tester
*/

describe('Unit testing for upload factory', function() {
   
var mock, upload, variables;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()}; 
  console.log('Print');

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
    
    // To be completed
    it("verify speed retriving", function() {
        console.log(upload.speed);
     expect(upload.speed).not.toBe(undefined);
   }); 
});
