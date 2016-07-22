if (!String.prototype.repeat) {
	String.prototype.repeat = function(count) {
		'use strict';
		if (this === null) {
			throw new TypeError('can\'t convert ' + this + ' to object');
		}
		var str = '' + this;
		count = +count;
		if (count != count) {
			count = 0;
		}
		if (count < 0) {
			throw new RangeError('repeat count must be non-negative');
		}
		if (count == Infinity) {
			throw new RangeError('repeat count must be less than infinity');
		}
		count = Math.floor(count);
		if (str.length === 0 || count === 0) {
			return '';
		}
		// Ensuring count is a 31-bit integer allows us to heavily optimize the
		// main part. But anyway, most current (August 2014) browsers can't handle
		// strings 1 << 28 chars or longer, so:
		if (str.length * count >= 1 << 28) {
			throw new RangeError('repeat count must not overflow maximum string size');
		}
		var rpt = '';
		for (;;) {
			if ((count & 1) == 1) {
				rpt += str;
			}
			count >>>= 1;
			if (count === 0) {
				break;
			}
			str += str;
		}
		return rpt;
	};
}

/**
* Describe the class that make the links with upload speed tester
* @class - upload
*/
app.factory('upload', ['$http', 'deviceDetector', 'variables', function($http, device, variables) {
    
    //Initialization of parameters
	var url = null;
	var request = null;

	var speed = 0;

    //Main process of the tester
	variables.get().then(function(data) {

		var bytes = 209715;
		var iterations = 3;
		var content = "0".repeat(bytes);

		var startTime = null;
		var endTime = null;

		url = data.upload_speed_tester_uri;
        
        //Retrieve the date before sending data
		function beforeSend() {
			startTime = Date.now();
		}
        
        //Retrieve the date after sending data
        //Calculate speed
		function afterSend(i) {
			endTime = Date.now();
			speed = ((i - 1) * speed + endTime - startTime) / i;

			if (i < iterations && speed >= 2 && device.isDesktop())
				speedtest(i);
		}
        
        //Make the global test with the two last function
		function speedtest(i) {
			request = $.ajax({
				method: 'POST',
				url: url,
				data: content,
				beforeSend: beforeSend,
				success: function() {
					afterSend(i + 1);
				}
			});

			request = null;
		}

		if (url !== null)
			speedtest(0);

	});
    
    /**
    * @function abort() - End the test
    */
	function abort() {
		if (request !== null)
			request.abort();
	}

	return {
		speed: function() {
			return speed;
		},
		abort: abort
	};
}]);