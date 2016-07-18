/**
* Test file for room.js controller 
*/

describe('Unit testing for room controller', function() {
   
    var mock, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
    }));
   
    
    
    describe('$scope.lineAvailable', function() {
        
        var $scope, controller;
        
        beforeEach(function() {
        $scope = {};
        var controller = $controller('RoomCtrl', {$scope: $scope }); 
        });
        
        it('Verify lineAvailable scope initialization', function() {
        expect($scope.lineAvailable).toBe(false);
        });
    });
    
});