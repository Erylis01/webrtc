/**
* Test file for variables.js factory 
*
* There is only one getter but it's the config.json one, so it's 
* important - Couldn't check inside the file because it's offline testing. 
* 100% of the DOM were covered, but precision had to be improved. 50% of total 
* factory covered so.
*/

describe('Unit testing for variables factory', function() {
   
var mock, variables;

beforeEach(angular.mock.module('app'));

beforeEach(function() {
    
  mock = {alert: jasmine.createSpy()}; 
 

  inject(function($injector) {
    variables = $injector.get('variables'); 
  });

});
    //Check the factory injection
   it("verify factory injection", function() {
     expect(variables).not.toBe(undefined);
   });        
           
    //Check the function get() - WIP - Found how to acess data
    it("verify if the config.json was retrieved", function() {
        var result = variables.get()
        expect(result).not.toBe(undefined);
    });
    
});
