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
            createGame : function (oxygenFactor, caveCount, caveWidth) {
                return $http.get('/api/iaconnector/game?oxygenFactor='
                        + oxygenFactor + '&caveCount=' + caveCount
                        +  '&caveWidth=' + caveWidth);
            },
            getGames : function (callback) {
                        $http.get('/api/iaconnector/games').then(callback);
            },
            getGames : function (callback) {
                $http.get('/api/iaconnector/games').then(callback);
            },
            addPlayer : function(idGame, playerName, callback) {
                $http.get('/api/iaconnector/addPlayer?idGame=' + idGame + '&playerName=' + playerName)
                     .then(callback);
            },
            startGame : function (idGame) {
                return $http.get('/api/iaconnector/startGame?idGame=' + idGame);
            },
            sendMove : function (playerUUID, move) {
                 return $http.get('/api/iaconnector/sendMove?playerUUID=' + playerUUID
                            + "&move=" + move);
            },
            init : function () {
                  return $http.get('/api/iaconnector/init');
            }
        }
    }
})();
