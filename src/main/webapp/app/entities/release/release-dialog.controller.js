(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('ReleaseDialogController', ReleaseDialogController);

    ReleaseDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Release', 'User', 'Product'];

    function ReleaseDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Release, User, Product) {
        var vm = this;

        vm.release = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();
        vm.products = Product.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.release.id !== null) {
                Release.update(vm.release, onSaveSuccess, onSaveError);
            } else {
                Release.save(vm.release, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('simplesdlcApp:releaseUpdate', result);
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
