/**
* Test file for room.js controller 
*/

describe('General parameters for testing controller', function() {
   
    var mock, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_) {
    $controller = _$controller_
    }));
   
    
    
    describe('Unit testing for user controller', function() {
        
        var $scope, controller;
        
        beforeEach(function() {
        $scope = {
            $on: function() {}
        };
        
        var controller = $controller('RoomCtrl', {$scope: $scope});
            
        inject(function($injector) {
        constraints = $injector.get('constraints'); 
        socket = $injector.get('socket'); 
        participants = $injector.get('participants');
        });
            
        });
        
        it('Verify scope variable initialization', function() {
        //Line variable
        expect($scope.lineAvailable).toBe(false); 
        expect($scope.lineExtension).toBe('');
        //Presentation variable    
        expect($scope.presentation.active).toBeFalsy;
        expect($scope.presentation.presenterIsMe).toBeFalsy; 
        $scope.presentation.disabled.all;
        expect($scope.presentation.disabled.general).toBeTruthy;
        expect($scope.presentation.disabled.screen).toBeTruthy;
        expect($scope.presentation.disabled.window).toBeTruthy;
        $scope.presentation.disabled.none;
        expect($scope.presentation.disabled.general).toBeFalsy;
        expect($scope.presentation.disabled.screen).toBeFalsy;
        expect($scope.presentation.disabled.window).toBeFalsy;
        //Participant variable    
        expect($scope.participantNames).toBeDefined;        
        });
    });
    
});