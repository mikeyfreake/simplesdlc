(function() {
    'use strict';
    angular
        .module('simplesdlcApp')
        .factory('Sprint', Sprint);

    Sprint.$inject = ['$resource', 'DateUtils'];

    function Sprint ($resource, DateUtils) {
        var resourceUrl =  'api/sprints/:id';

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
