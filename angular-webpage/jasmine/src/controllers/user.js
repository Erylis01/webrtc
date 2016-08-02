/**
* Controller related to user's functionnality
* @constructor
* @param {function} $scope - Enable to focus a component. See Angular doc for further informations
* @param {string} $location - Set the current path (to complete)
* @param {socket} socket - see socket.js
* @param {constraints} sonstraints - see constraints.js
* @param {object} notifications - Object containing required functionnalities to use notifications
* @param {Participant{}} - Dictionnary of the current participant
*/
function UserCtrl($scope, $rootScope, $location, socket, constraints, notifications, participants) {

    //Set the field to null value
	$scope.participant = {
		name: '',
		room: '',
		compositeOptions: 'normal'
	};
    
    //Button coloration
	$scope.color = 'blue-grey';
    
	$scope.checked = {
		name: true,
		room: true
	};

    
    //Check if it's necessary to print compatibility warning
	$scope.isIncompatible = function() {
		var browser = constraints.browser;

		if (browser == 'safari' || browser == 'ie' || browser == 'ms-edge')
			return true;

		return false;
	};

    //Launch process for user entry when click on Join button
	$scope.join = function(participant) {
        
        //Roome isn't existing : create both
		if (_.isEmpty(participant.name) || _.isEmpty(participant.room)) {

			$scope.checked.name = !_.isEmpty(participant.name);
			$scope.checked.room = !_.isEmpty(participant.room);

			$scope.color = 'red';

		} else {
            // Room is existing, try to contact other participants
			if (socket.isOpen()) {

				var userId = createGuid();
				participants.add(userId, participant.name);

				socket.prepareJoiningRoom({
					id: 'joinRoom',
					userId: userId,
					name: participant.name,
					room: participant.room,
					mediaSource: 'composite'
				});

				constraints.setCompositeOptions(participant.compositeOptions);

				$location.path("/rooms/" + participant.room);
            
			} else {
                //WebSocket fail - server unreacheable
                if ($rootScope.langKey === 'en') {
				    var warning = {
					   title: 'Websocket Error',
					   content: 'Unable to connect to the server. Please try later.'
				    };
                } else if ($rootScope.langKey === 'fr') {
                    var warning = {
					   title: 'Erreur Websocket',
					   content: 'Impossible de se connecter au serveur. Merci d\'essayer plus tard.'
				    };
                };

				notifications.alert(warning.title, warning.content, 'Ok', function(answer) {
					// This should be handled by lumx (it must be a bug)
					// May be removed in the future
					$('.dialog-filter').remove();
					$('.dialog').remove();
				});

			}
		}
    };    

    /**
    * This bloc is not yet implemented
    */
	if (constraints.getWarning()) {

		var warning = {
			title: 'Username already taken',
			content: 'Please choose another username.'
		};

		notifications.alert(warning.title, warning.content, 'Ok', function(answer) {
			// This should be handled by lumx (it must be a bug)
			// May be removed in the future
			$('.dialog-filter').remove();
			$('.dialog').remove();
		});

		constraints.setWarning(null);

	}
    /**
    *End of the previous bloc
    */


/**
* @function - setResolution() : change user resolution according param
* @param number - width : width of the sending video
* @param number - height : height of the sending video
* @param boolean - isAuto : tru if the user wants an auto setting
*/
$scope.setResolution = function (width, height, isAuto) {

        constraints.setRes(width, height, isAuto);
        if (isAuto) {
            if ($rootScope.langKey === 'en') {
            notifications.notify('Resolution auto adjustment', 'account-plus')
            } else if ($rootScope.langKey === 'fr') {
            notifications.notify('Ajustement automatique de la résolution', 'account-plus')    
            };  
            console.log('Resolution auto adjustment');
        } else {
            if ($rootScope.langKey === 'en') {
            notifications.notify('Resolution set to : '+width+' * '+height, 'account-plus');  
            } else if ($rootScope.langKey === 'fr') {
            notifications.notify('Resolution choisie : '+width+' * '+height, 'account-plus');      
            };
            console.log('Resolution set to : '+width+' * '+height);
        }
    };

}


/**
* @funtion createGuid() - Create an id for the user
* @return String - Id
*/
function createGuid() {
	return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
		var r = Math.random()*16|0, v = c === 'x' ? r : (r&0x3|0x8);
		return v.toString(16);
	});
}