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
  console.log('Print');

  inject(function($injector) {
    constraints = $injector.get('constraints'); 
    variables = $injector.get('variables');
  });

});
    
   //Check the factory injection
   it("verify factory injection", function() {
     expect(constraints).not.toBe(undefined);
     expect(variables).not.toBe(undefined);
   }); 
    
});