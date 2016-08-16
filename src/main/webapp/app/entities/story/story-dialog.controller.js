(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('StoryDialogController', StoryDialogController);

    StoryDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Story', 'Product', 'Release', 'User', 'Sprint'];

    function StoryDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Story, Product, Release, User, Sprint) {
        var vm = this;

        vm.story = entity;
        vm.clear = clear;
        vm.save = save;
        vm.products = Product.query();
        vm.releases = Release.query();
        vm.users = User.query();
        vm.sprints = Sprint.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.story.id !== null) {
                Story.update(vm.story, onSaveSuccess, onSaveError);
            } else {
                Story.save(vm.story, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('simplesdlcApp:storyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
