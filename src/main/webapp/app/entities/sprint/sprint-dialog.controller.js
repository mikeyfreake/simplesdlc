(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('SprintDialogController', SprintDialogController);

    SprintDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Sprint', 'Release'];

    function SprintDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Sprint, Release) {
        var vm = this;

        vm.sprint = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.releases = Release.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.sprint.id !== null) {
                Sprint.update(vm.sprint, onSaveSuccess, onSaveError);
            } else {
                Sprint.save(vm.sprint, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('simplesdlcApp:sprintUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.plannedStartDate = false;
        vm.datePickerOpenStatus.plannedEndDate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
