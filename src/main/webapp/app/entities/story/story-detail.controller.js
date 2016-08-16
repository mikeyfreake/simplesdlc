(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('StoryDetailController', StoryDetailController);

    StoryDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Story', 'Product', 'Release', 'User', 'Sprint'];

    function StoryDetailController($scope, $rootScope, $stateParams, previousState, entity, Story, Product, Release, User, Sprint) {
        var vm = this;

        vm.story = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('simplesdlcApp:storyUpdate', function(event, result) {
            vm.story = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
