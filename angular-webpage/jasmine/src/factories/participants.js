/**
* Describe the object : attributes and
* methods of the partipant object.
* @class
*/
app.factory('participants', ['socket', function(socket) {

	var participants = {};
	var id = null;
	var name = null;

	function add(userId, n) {
        
        /**
        * Model for a participant and related function to build it
        * @constructs
        * @typedef {String} UserID - unique identifier for the user
        * @typedef {String} Name - Name chossen by the used
        * @typedef {function{}} rtcPeer - Set the function composite and presentation to null
        * @typedef {function} offer - Function that allow user to send its signal
        * @typedef {function} offerToReceive - Describe composite and presentation in this case
        * @typedef {function} iceCanddate - Function that allow user to send its ICE request
        * @typedef {function} onIceCandidate - Describe composite and presentation in this case
        * @typedef {function} disposeType - Dispose the rtcPeer status
        * @typedef {function} dispose - Dispose the current compositie and presentation function
        * @typedef {function} getIceCandidate - Retrieve the ice composite and presentation function
        */ 
		var participant = {
			userId: userId,
			name: n,
			rtcPeer: {
				presentation: null,
				composite: null
			},

			offer: function(type, error, offerSdp, wp) {
				if (error)
					return console.error("sdp offer error");

				console.log('Invoking SDP offer callback function');

				var msg = {
					id: "receiveVideoFrom",
					userId: userId,
					sender: n,
					sdpOffer: offerSdp,
					type: type
				};

				socket.send(msg);
			},

			offerToReceive: {
				composite: function(error, offerSdp, wp) {
					this.offer('composite', error, offerSdp, wp);
				},
				presentation: function(error, offerSdp, wp) {
					this.offer('presentation', error, offerSdp, wp);
				}
			},

			iceCandidate: function(type, candidate, wp) {
				console.log("Local candidate" + JSON.stringify(candidate));

				var message = {
					id: 'onIceCandidate',
					candidate: candidate,
					type: type
				};

				socket.send(message);
			},

			onIceCandidate: {
				composite: function(candidate, wp) {
					this.iceCandidate('composite', candidate, wp);
				},

				presentation: function(candidate, wp) {
					this.iceCandidate('presentation', candidate, wp);
				}
			},

			disposeType: function(type) {
				if (this.rtcPeer[type])
					this.rtcPeer[type].dispose();
			},

			dispose: function() {
				console.log('Disposing participant ' + this.name);

				this.disposeType('presentation');
				this.disposeType('composite');
			},

			getIceCandidate: function(type) {

				if (type != 'composite')
					return this.onIceCandidatePresentation;
				return this.onIceCandidateComposite;
			}
		};

		Object.defineProperty(participant.rtcPeer, 'presentation', {
			writable: true
		});

		Object.defineProperty(participant.rtcPeer, 'composite', {
			writable: true
		});

		participants[userId] = participant;

		if (name === null) {
			id = userId;
			name = n;
		}
	}
    
    /**
    * @function get() - userId getter
    * @param String - userId
    * @return String - userId
    */
	function get(userId) {
		return participants[userId];
	}
    /**
    * @function remove() - userId removal
    * @param String - userId
    */
	function remove(userId) {
		delete participants[userId];
	}

    /**
    * @function me() - Retrieve the object that represente the paticipant who invoke it
    * @return Particpant - participant
    */
	function me() {
		return participants[id];
	}
    
    /**
    * @function isEmpty() - Check if the participants tab is empty
    * @return booleam - isEmpty
    */
	function isEmpty() {
		return _.isEmpty(participants);
	}

    /**
    * @function clear() - Delete every participans of the room 
    */
	function clear() {
		for (var key in participants) {
			if (participants[key] !== undefined)
				participants[key].dispose();

			delete participants[key];
		}

		name = null;
		id = null;
	}

	return {
		add: add,
		clear: clear,
		get: get,
		isEmpty: isEmpty,
		me: me,
		remove: remove
	};
}]);