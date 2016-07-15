/**
* Test file for variables.js factory 
*
* There is only one getter but it's the config.json one, so it's * important
*/

describe('Unit testing for variables factory', function() {
   
var mock, variables;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()};
    
  console = {};    
  console.error = jasmine.createSpy();   
  console.log = jasmine.createSpy();   
  console.warn = jasmine.createSpy();      
 

  inject(function($injector) {
    variables = $injector.get('variables'); 
  });

}
           
   it("verify variables factory injection", function() {
     expect(variables).not.toBe(undefined);
   });        
           
    it("verify if the config.json was retrieved", function() {
    
    
    })
});
