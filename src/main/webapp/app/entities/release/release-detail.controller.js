(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('ReleaseDetailController', ReleaseDetailController);

    ReleaseDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Release', 'User'];

    function ReleaseDetailController($scope, $rootScope, $stateParams, previousState, entity, Release, User) {
        var vm = this;

        vm.release = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('simplesdlcApp:releaseUpdate', function(event, result) {
            vm.release = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
