/**
* Test file for room.js controller 
*/

describe('General parameters for testing controller', function() {
   
    var mock, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
    }));
   
    
    
    describe('Unit testing for user controller', function() {
        
        var $scope, controller;
        
        beforeEach(function() {
        $scope = {
            $on: function() {}
        };
        var controller = $controller('RoomCtrl', {$scope: $scope });
        inject(function($injector) {
        constraints = $injector.get('constraints'); 
        socket = $injector.get('socket'); 
        participants = $injector.get('participants'); 
        });
        });
        
        it('Verify scope variable initialization', function() {
        expect($scope.lineAvailable).toBe(false);   
        });
    });
    
});