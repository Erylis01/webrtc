var app = angular.module('app', ['ngRoute', 'ng.deviceDetector', 'lumx','pascalprecht.translate']);

app.config(['$routeProvider', function($routeProvider) {
	$routeProvider
		.when('/', {
			templateUrl: '/views/login.html',
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
            'STREAM_SETTING':'Stream settings',
            'FUNCTIONNALITIES':'Func.',
            'DOWNLOAD':'Download',
            'VOLUME':'Conference volume',
            'LEAVE':'Leave room',
            'ONLINE_USERS':'Online users',
            'PRESENTATION':'Give a presentation',
            'SCREENSHARE':'Screenshare',
            'WINDOW_SHARE':'Window share',
            'RECORD':'Record',
            'STOP_RECORD':'Stop record',
            'MUTE':'Mute',
            'UNMUTE':'Unmute',
            'BUTTON_TEXT_EN': 'English',
            'BUTTON_TEXT_FR': 'French'              
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
            'END_USER_MEDIA':'Regarder seulement',
            'CONFIRMATION':'Entrez !',
            'STREAM_SETTING':'Réglages',
            'FUNCTIONNALITIES':'Options',
            'DOWNLOAD':'Télécharger',
            'VOLUME':'Audio',
            'LEAVE':'Quitter la salle',
            'ONLINE_USERS':'Utilisateurs en ligne',
            'PRESENTATION':'Faire une présentation',
            'SCREENSHARE':'Partage d\écran',
            'WINDOW_SHARE':'Partage de fenêtre',
            'RECORD':'Enregistrer',
            'STOP_RECORD':'Arrêter l\'enregistrement',
            'MUTE':'Silencieux',
            'UNMUTE':'Sonore',
            'BUTTON_TEXT_EN': 'Anglais',
            'BUTTON_TEXT_FR': 'Français'
        });  
    
    
        $translateProvider.preferredLanguage('en');
            }]);


// Injections
app.controller('UserCtrl', ['$scope', '$location', 'socket', 'constraints', 'LxNotificationService', 'participants', UserCtrl]);
app.controller('RoomCtrl', ['$scope', '$location', '$window', '$routeParams', '$timeout', 'socket', 'constraints', 'LxNotificationService', 'LxProgressService', 'participants', RoomCtrl]);
app.controller('TranslateController', function($translate, $scope) {
  $scope.changeLanguage = function (langKey) {
    $translate.use(langKey);
  };
});
