/**
* Test file for room.js controller 
*/

describe('General parameters for testing controller', function() {
   
    var mock, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
    }));
   
    
    
    describe('Unit testing for RoomCtrl', function() {
        
        var $scope, $rootScope, controller;
        
        console = {};    
        console.error = jasmine.createSpy();   
        console.log = jasmine.createSpy();   
        console.warn = jasmine.createSpy(); 
        
        beforeEach(function() {
        $scope = {
            $on: function() {}
        };
        
        var controller = $controller('RoomCtrl', {$scope: $scope, $rootScope: $rootScope});
            
        inject(function($injector) {
        constraints = $injector.get('constraints'); 
        socket = $injector.get('socket'); 
        participants = $injector.get('participants');
        });  
            
        });

        
        it('Verify scope variable initialization', function() {
        //Line variable
        expect($scope.lineAvailable).toBe(true); 
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
        
        //Check the Output resolution (i.e. size container) setter
        it('verify the Output resolution setter - setOutputVideoResolution()', function () {
        $scope.setOutputVideoResolution('test');
        expect(console.log).toHaveBeenCalledWith('640px'); 
        $scope.setOutputVideoResolution('normal');
        expect(console.log).toHaveBeenCalledWith('60%');
        $scope.setOutputVideoResolution('cinema');
        expect(console.log).toHaveBeenCalledWith('90%');
        });
        
        //Should be mocked to verify correspondance table in the onMessage function
        it('verify the relation between message received and function ', function() {
        expect('Test').toBe('Not implemented yet, need to configure mock for simulate server message');
        });
        
        //Verification of the location changing event
        it('verify the location change event', function() {
        expect('Test').toBe('Not implemented yet, need to find how to spy $on event');    
        });
        
        //Check the stopPresenting function 
        it('verify the presentation cancelling function - $scope.stopPresenting', function () {
        $scope.stopPresenting();
        expect($scope.presentation.presenterIsMe).toBeFalsy;
        expect(constraints.getType()).toBe('composite');
        expect(console.warn).toHaveBeenCalledWith('Socket was closed before sending message'); 
        });
        
        //Check the sharing function 
        it('verify the sharing function - $scope.share()', function() {
            // Case 1 : Presentation already started
            $scope.presentation.active = true;
			$scope.presentation.presenterIsMe = false;
            $scope.share('composite');
            expect(console.log).toHaveBeenCalledWith('A presentation is already running');
            // Case 2 : No presentation active but browser is not chrome
            constraints.browserIsChrome = false;
            constraints.canPresent = false;
            $scope.presentation.active = false;
            $scope.share('composite');
            expect(console.log).toHaveBeenCalledWith('Your browser does not support screensharing');
            //Case 3 : No presentation active, chrome browser but extension not installed
            constraints.browserIsChrome = true;
            constraints.canPresent = true;
			constraints.setType('composite');
            constraints.chromeExtensionInstalled = false;
			$scope.share('composite');
            expect(console.log).toHaveBeenCalledWith('Extension need to be installed');
			//Case 4 : Everything is right for presentation
			constraints.chromeExtensionInstalled = true;
			$scope.share('composite');
			expect($scope.presenterIsMe).toBeTruthy;
        });
        
    });
    
});