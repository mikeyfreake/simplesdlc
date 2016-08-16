(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('SprintDetailController', SprintDetailController);

    SprintDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Sprint', 'Release'];

    function SprintDetailController($scope, $rootScope, $stateParams, previousState, entity, Sprint, Release) {
        var vm = this;

        vm.sprint = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('simplesdlcApp:sprintUpdate', function(event, result) {
            vm.sprint = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
