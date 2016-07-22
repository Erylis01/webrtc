/**
* Test file for user.js controller 
*/

describe('General parameters for testing controller', function() {
   
    var mock, $controller;
   
    beforeEach(angular.mock.module('app'));
    
    beforeEach(inject(function(_$controller_) {
    $controller = _$controller_;
    }));
  
    
    describe('Unit testing for UserCtrl', function() {
        
        var $scope, controller;
        
        console = {};    
        console.error = jasmine.createSpy();   
        console.log = jasmine.createSpy();   
        console.warn = jasmine.createSpy();   
        
        beforeEach(function() {
        $scope = {};    
        var controller = $controller('UserCtrl', {$scope: $scope})
        inject(function($injector) {
        constraints = $injector.get('constraints'); 
        socket = $injector.get('socket'); 
        participants = $injector.get('participants'); 
        });
        });
        
        it('Verify scope variable initialization', function() {
        expect($scope.color).toBe('blue-grey');
        expect($scope.participant.name).toBe('');
        expect($scope.participant.room).toBe('');  
    expect($scope.participant.compositeOptions).toBe('normal');
        expect($scope.checked.name).toBe(true);  
        expect($scope.checked.room).toBe(true);  
        });
        
        it('verify the browser incompatibility detection - $scope.isIncompatible()', function() {
        //Every test will be tested by forced constraint browser
        constraints.browser = 'safari';  
        expect($scope.isIncompatible()).toBe(true);
        constraints.browser = 'ie';  
        expect($scope.isIncompatible()).toBe(true);
        constraints.browser = 'ms-edge';  
        expect($scope.isIncompatible()).toBe(true);
        constraints.browser = 'Other';  
        expect($scope.isIncompatible()).toBe(false);         
        });
        
        it('verify the resolution setter', function() {
           $scope.setResolution(140,140,false);
           expect(console.log).toHaveBeenCalledWith('Resolution set to : '+140+' * '+140);
           $scope.setResolution(140,140,true); 
           expect(console.log).toHaveBeenCalledWith('Resolution auto adjustment'); 
        });
        
        it('verify the joining process - $scope.join()', function() {
        expect('Tests').toBe('Not implemented yet'); 
        });
        
        it('verify the creation of used Id - $scope.createGuid()', function() {
        expect(createGuid().length).toBe(36);
        expect(createGuid().length).not.toBe('xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx');
        })
    });
    
});