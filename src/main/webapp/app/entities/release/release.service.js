(function() {
    'use strict';
    angular
        .module('simplesdlcApp')
        .factory('Release', Release);

    Release.$inject = ['$resource', 'DateUtils'];

    function Release ($resource, DateUtils) {
        var resourceUrl =  'api/releases/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.plannedStartDate = DateUtils.convertDateTimeFromServer(data.plannedStartDate);
                        data.plannedEndDate = DateUtils.convertDateTimeFromServer(data.plannedEndDate);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
