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
            'CONFIRMATION':'Join !',
            BUTTON_TEXT_EN: 'english',
            BUTTON_TEXT_FR: 'français'              
        });    
        
    
        $translateProvider.translations('fr', {
            'WARNING_BROWSER':'Votre navigateur est potentiellement incompatible avec WebRTC. Veuillez utiliser Mozilla Firefox, Google Chrome ou Opera à la place.',
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
