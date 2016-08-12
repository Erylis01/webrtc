/**
* Test file for room.js controller 
*/

describe('General parameters for testing controller', function() {
   
    var mock, $rootScope, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_, _$rootScope_) {
    $controller = _$controller_;
	$rootScope = _$rootScope_;
    }));
   
    
    
    describe('Unit testing for RoomCtrl', function() {
        
        var $scope, location, rootScope, controller;
        
        console = {};    
        console.error = jasmine.createSpy();   
        console.log = jasmine.createSpy();   
        console.warn = jasmine.createSpy(); 
		
        
        beforeEach(function() {
        $scope = {
            $on: function() {}
        };
        
        var controller = $controller('RoomCtrl', {$scope: $scope, rootScope: $rootScope});
            
        inject(function($injector, $location) {
        constraints = $injector.get('constraints'); 
        socket = $injector.get('socket'); 
        participants = $injector.get('participants');
		location = $location;
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
		
		//Test the presentation ability checker
		it('verify the presentation ability checker function - canPresent()', function() {
			//Case 1 : False because it can't present
			constraints.canPresent = false
			constraints.browserIsChrome = true;
			expect($scope.canPresent).toBeFalsy;
			//Case 2 : False because it isn't chrome browser
			constraints.canPresent = true
			constraints.browserIsChrome = false;
			expect($scope.canPresent).toBeFalsy;
			//Case 3 : True
			constraints.canPresent = true
			constraints.browserIsChrome = true;
			expect($scope.canPresent).toBeTruthy;
		});
        
        //Check the stopPresenting function 
        it('verify the presentation cancelling function - $scope.stopPresenting', function () {
        $scope.stopPresenting();
        expect($scope.presentation.presenterIsMe).toBeFalsy;
        expect(constraints.getType()).toBe('composite');
        expect(console.warn).toHaveBeenCalledTimes(1);
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
		
		
		//Check the invite function - Need to be completed with server answer
		it('verify the invite function - $scope.invite()', function () {
			$scope.invite('0638457456');
			expect(console.warn).toHaveBeenCalledTimes(2);
		});
        
		//Check the leaving function and scope
		it('verify the leaving function and related scope - leave() and $scope.leave()', function() {
			$scope.leave();
			expect(console.warn).toHaveBeenCalledTimes(3);
			expect(constraints.getType()).toBe('composite');
			expect(participants.isEmpty()).toBeTruthy;
			expect(location.path()).toBe('/');
		});
		
		//Check the $scope.record
		it('verify the recording scope element - $scope.record', function() {
			//We have to add a participant for the change function (construct socket message)
			participants.add("1515184",'Jean');
			expect($scope.record.recording).toBeFalsy;
			expect($scope.record.text).toBe('RECORD');
			$scope.record.change(); //Don't forget to count call to socket.send
			expect($scope.record.recording).toBeTruthy;
			expect($scope.record.text).toBe('STOP_RECORD');
		});
		
		//Check both recordJS and stopRecordJS()
		it('verify the ability to ask to server for a recording -recordJS() and stopRecordJS()', function() {
			participants.add("1515184",'Jean');
			$scope.record.change();
			expect(console.log).toHaveBeenCalledWith('Start record');
			expect(console.warn).toHaveBeenCalledTimes(5);
			$scope.record.change();
			expect(console.log).toHaveBeenCalledWith('End record');
			expect(console.warn).toHaveBeenCalledTimes(6);
		});
		
		//CHeck the resolution scope element (4 cases)
		it('verify the resolution setting scope element', function() {
			//Case 1 : English setting with isAuto
			$rootScope.langKey = 'en';
			$scope.setResolution(240,120,true);
			expect(console.log).toHaveBeenCalledWith('Resolution set to 160 * 120');
			expect(console.log).toHaveBeenCalledWith('Auto_EN');
			//Case 2 : French setting with isAuto
			$rootScope.langKey = 'fr';
			$scope.setResolution(240,120,true);
			expect(console.log).toHaveBeenCalledWith('Resolution set to 160 * 120');
			expect(console.log).toHaveBeenCalledWith('Auto_FR');
			//Case 3 : French setting without isAuto
			$scope.setResolution(240,120,false);
			expect(console.log).toHaveBeenCalledWith('Resolution set to 240 * 120');
			expect(console.log).toHaveBeenCalledWith('Not_Auto_FR');
			//Case 4 : Engish setting without isAuto
			$rootScope.langKey = 'en';
			$scope.setResolution(240,120,false);
			expect(console.log).toHaveBeenCalledWith('Resolution set to 240 * 120');
			expect(console.log).toHaveBeenCalledWith('Not_Auto_EN');
		});
		
		//Check the constraints renewel function and associated scopes - renewCOnstraints(compositeOptions)
		it('verify the constraints renewal function and associated $scope- renewCOnstraint(), $scope.allTracks(), $scope.watchOnly(), $scope.microOnly()', function() {
		//participants.add("1515184",'Jean');
		//$scope.allTracks();
		//expect(console.warn).toHaveBeenCalledTimes(7);
		//expect(console.log).toHaveBeenCalledWith('normal');
		//$scope.watchOnly();
		//expect(console.warn).toHaveBeenCalledTimes(8);
		//expect(console.log).toHaveBeenCalledWith('watchOnly');
		//$scope.microOnly();
		//expect(console.warn).toHaveBeenCalledTimes(9);
		//expect(console.log).toHaveBeenCalledWith('audioOnly');
		expect('Test').toBe('Written but not functionnal - Need to find how to correct the error due to sendStream that could not work offline');
		});
		
		/**
		* After the last test, every function called exclusively by receiving a * message couldn't be directly test while we havn't find a way to 
		* direcly call controller function
		*/
		
		//Check the on presenter ready function 
		//it('verify the onPresenterReady function', function(){
		//this.onPresenterReady('test');	
		//});
		
    });   
	
});