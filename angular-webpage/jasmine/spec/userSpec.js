/**
* Test file for user.js controller 
*/

describe('Unit testing for user controller', function() {
   
    var mock, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
    }));
  
    
    describe('$scope.color', function() {
        
        var $scope, controller;
        
        beforeEach(function() {
        $scope = {};    
        var controller = $controller('UserCtrl', {$scope: $scope})
        });
        
        it('Verify color scope initialization', function() {
        expect($scope.color).toBe('blue-grey');
        });
    });
    
});