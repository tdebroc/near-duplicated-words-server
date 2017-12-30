(function() {

    angular
        .module('iaserversnorkunkingApp')
        .factory('IAConnectorService', IAConnectorService);

                          IAConnectorService.$inject = ['$http'];
                          function IAConnectorService($http) {

                              function request(url, callbackSuccess, callbackError, method) {
                                  $http({
                                    method: method,
                                    url: url
            }).then(callback, callbackError);
        }
        return {
            findDuplicates : function (text) {
                return $http.post('/api/iaconnector/findDuplicates', text);
            }
        }
    }
})();
