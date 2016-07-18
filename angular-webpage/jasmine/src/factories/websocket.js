/**
* Describe the websocket object
* @class socket
*/
app.factory('socket', ['$window', 'variables', function($window, variables) {

    //Initialization of the Websocket with default parameters
	var uri;
	var socket = {
		readyState: 0
	};
	var messagePrepared = null;

    //Instantiate with the config.json informations
	variables.get().then(function(data) {
		uri = ($window.location.protocol == 'https:') ? data.wss_uri : data.ws_uri;
		start();
	});

    /**
    * @function start() - WebSocket starter
    * This function deals with the three state of the websocket * and is responsible of the stay-alive message parameters
    */
	function start() {
		socket = new WebSocket(uri);

		socket.onopen = function(event) {
			setInterval(function() {
				send({ id: 'stay-alive' });
			}, 30000);
		};

		socket.onerror = function(error) {
			socket.close();
		};

		socket.onclose = function(event) {
			// Try to reconnect each 1 second
			setTimeout(function() {
				start();
			}, 1000);
		};
	}
    
    /**
    * @function send() - Send a JSON message to server
    * @param String{} - message
    * @throws err - Closed before sending
    */
	function send(message) {
		var jsonMessage = JSON.stringify(message);
		console.log('Sending message: ' + jsonMessage);
		try {
			socket.send(jsonMessage);
		} catch (e) {
			console.warn('Socket was closed before sending message');
		}
	}
    
    /**
    * @function get() - Socket getter
    * @return socket - Websocket
    */
	function get() {
		return socket;
	}

    /**
    * @function getMessage() - messagePrepared getter
    * @return socket - Websocket
    */
	function getMessage() {
		return messagePrepared;
	}
    
    /**
    * @function prepareJoiningRoom() - Set prepared message to * param
    * @param String{} - message
    */
	function prepareJoiningRoom(m) {
		messagePrepared = m;
	}

    /**
    * @function roomReady() - Send the current prepared message * if it exists
    */
	function roomReady() {
		if (messagePrepared !== null)
			send(messagePrepared);
		messagePrepared = null;
	}

    /**
    * @function isOpen() - Check the socket's state and verify * it is open
    * @return Boolean - Socket open or not
    */
	function isOpen() {
		return (socket.readyState == 1);
	}

    /**
    * @function onbeforeunload() - Automatic websocket closer 
    * before leaving the webpage
    */
	$window.onbeforeunload = function() {
		socket.close();
	};

	return {
		send: send,
		get: get,
        getMessage: getMessage,
		prepareJoiningRoom: prepareJoiningRoom,
		roomReady: roomReady,
		isOpen: isOpen
	};
}]);