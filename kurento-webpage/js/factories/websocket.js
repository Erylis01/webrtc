app.factory('socket', ['$window', function($window) {

	var socket = new WebSocket('wss://webrtc.ml/groupcall');

	var send = function(message) {
		var jsonMessage = JSON.stringify(message);
		console.log('Sending message: ' + jsonMessage);
		socket.send(jsonMessage);
	};

	var get = function() {
		return socket;
	};

	var isOpen = function() {
		return (socket.readyState == 1);
	};

	$window.onbeforeunload = function() {
		send({ id: 'leaveRoom' });
		socket.close();
	};

	return {
		send: send,
		get: get,
		isOpen: isOpen
	};
}]);