(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .controller('SprintDeleteController',SprintDeleteController);

    SprintDeleteController.$inject = ['$uibModalInstance', 'entity', 'Sprint'];

    function SprintDeleteController($uibModalInstance, entity, Sprint) {
        var vm = this;

        vm.sprint = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Sprint.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
