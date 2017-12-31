(function() {
    'use strict';

    angular
        .module('iaserversnorkunkingApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state',
                              'IAConnectorGame', 'IAConnectorService', 'IAConnectorSocketService',
                              "$sce"];

    function HomeController ($scope, Principal, LoginService, $state,
                            IAConnectorGame, IAConnectorService, IAConnectorSocketService, $sce) {

        $("#text").val("word test word\ntes\ntesp tes\ntest plop\n plop ")
        $scope.text = "word test word\ntes\ntesp tes\ntest plop\n plop "
        $scope.minWordLength = 3;
        $scope.numberCharToKeepInTheWord = 5;
        $scope.numberOfWordDistance = 50;


        $scope.findDuplicates = function() {
            var text = $("#text").val()
            $scope.minWordLength = parseInt($("#minWordLength").val())
            $scope.numberCharToKeepInTheWord = parseInt($("#numberCharToKeepInTheWord").val())
            $scope.numberOfWordDistance = parseInt($("#numberOfWordDistance").val())

            IAConnectorService.findDuplicates(text, $scope.minWordLength, $scope.numberCharToKeepInTheWord, $scope.numberOfWordDistance)
                .then(function(response) {
                     $scope.result = $sce.trustAsHtml(response.data.textResult);
                     $scope.numberDuplicated = response.data.numberDuplicated;
                     window.scrollTo(0,0);
                }, function(response) {
                     console.error(response);
                });
        }

        setTimeout(function() {
                $scope.$apply();
                $scope.findDuplicates();
            }, 100
        )


    }


})();

