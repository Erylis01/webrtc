var app = angular.module('app', ['ngRoute', 'ng.deviceDetector', 'lumx','pascalprecht.translate']);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider
		.when('/', {
			templateUrl: 'views/login.html',
			controller: 'UserCtrl'
		})
		.when('/rooms/:roomName', {
			templateUrl: 'views/room.html',
			controller: 'RoomCtrl'
		})
		.otherwise({
			redirectTo: '/'
		});
}]);


// Translation bloc 
app.config(['$translateProvider',function($translateProvider) {
        $translateProvider.translations('en', {
            'WARNING_BROWSER':'Your browser may not support WebRTC yet. Please use Mozilla Firefox, Google Chrome or Opera instead.',
            'NAME':'Name',
            'ROOM':'Room',
            'CONFIRMATION':'Join !',
            'WARNING_NAME':'Name can\'t be empty',
            'WARNING_ROOM':'Room can\'t be empty',
            'SHOW_SETTINGS':'Show advanced stream settings',
            'HIDE_SETTINGS':'Hide advanced stream settings',
            'MICRO_ONLY':'Microphone only',
            'END_USER_MEDIA':'Watch only',
            BUTTON_TEXT_EN: 'english',
            BUTTON_TEXT_FR: 'français'              
        });    
        
    
        $translateProvider.translations('fr', {
            'WARNING_BROWSER':'Votre navigateur est potentiellement incompatible avec WebRTC. Veuillez utiliser Mozilla Firefox, Google Chrome ou Opera à la place.',
            'NAME':'Nom',
            'ROOM':'Salle',
            'WARNING_NAME':'Veuillez renseigner votre nom',
            'WARNING_ROOM':'Veuillez renseigner la salle',
            'SHOW_SETTINGS':'Afficher les réglages avancés',
            'HIDE_SETTINGS':'Cacher les réglages avancés',
            'MICRO_ONLY':'Microphone seulement',
            'END_USER_MEDIA':'Desactiver microphone et caméra',
            'CONFIRMATION':'Entrez !',
            BUTTON_TEXT_EN: 'english',
            BUTTON_TEXT_FR: 'français'
        });  
    
    
        $translateProvider.determinePreferredLanguage();
            }]);


// Injections
app.controller('UserCtrl', ['$scope', '$location', 'socket', 'constraints', 'LxNotificationService', 'participants', UserCtrl]);
app.controller('RoomCtrl', ['$scope', '$location', '$window', '$routeParams', '$timeout', 'socket', 'constraints', 'LxNotificationService', 'LxProgressService', 'participants', RoomCtrl]);
app.controller('TranslateController', function($translate, $scope) {
  $scope.changeLanguage = function (langKey) {
    $translate.use(langKey);
  };
});
