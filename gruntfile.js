module.exports = function(grunt) {

       'use strict';
       // Project configuration.
       grunt.initConfig({
               jasmine : {
		       src : ['bower_components/jquery/dist/jquery.js',
		       'bower_components/angular/angular.js',
		       'bower_components/velocity/velocity.js',
		       'bower_components/moment/moment.js',
		       'bower_components/lumx/dist/lumx.js',
		       'bower_components/angular-route/angular-route.js',
		       'bower_components/underscore/underscore.js',
		       'bower_components/re-tree/re-tree.js',
		       'bower_components/ng-device-detector/ng-device-detector.js',
		       'bower_components/webrtc-adapter/adapter.js',
		       'bower_components/angular-translate/angular-translate.js',
		       'bower_components/angular-mocks/angular-mocks.js',
		       'angular-webpage/js/kurento-utils.min.js',
                       'angular-webpage/jasmine/src/controllers/room.js',
		       'angular-webpage/jasmine/src/controllers/user.js',
		       'angular-webpage/jasmine/src/app.js',
		       'angular-webpage/jasmine/src/factories/*.js'],

                       options : {
                               specs : ['angular-webpage/jasmine/spec/participantsSpec.js', 
'angular-webpage/jasmine/spec/variablesSpec.js', 
'angular-webpage/jasmine/spec/uploadSpec.js',
'angular-webpage/jasmine/spec/constraintsSpec.js',
'angular-webpage/jasmine/spec/websocketSpec.js',
'angular-webpage/jasmine/spec/roomSpec.js',
'angular-webpage/jasmine/spec/userSpec.js'
],
                       }
               }
       });
       grunt.loadNpmTasks('grunt-contrib-jasmine');
};
