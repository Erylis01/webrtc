/**
* Describe the retriever of config file config.json
* @class
*/ 
app.factory('variables', ['$http', function($http) {
    
    /**
    * @function get() - config.json getter
    * @return String{} - result.data (Containe the config file field)
    */
	function get() {
		return $http.get('config.json').then(function(result) {
			return result.data;
		});
	}

	return { get: get };
}]);