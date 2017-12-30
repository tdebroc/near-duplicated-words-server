(function() {
    'use strict';

    angular
        .module('iaserversnorkunkingApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state',
                              'IAConnectorGame', 'IAConnectorService', 'IAConnectorSocketService'];

    function HomeController ($scope, Principal, LoginService, $state,
                            IAConnectorGame, IAConnectorService, IAConnectorSocketService) {


        $scope.findDuplicates = function() {
            var text = $("#text").val()
            console.log(text);
            IAConnectorService.findDuplicates(text)
                .then(function(response) {
                     $scope.result = response.data.textResult;
                }, function(response) {
                     console.error(response);
                });
        }

    }


})();
