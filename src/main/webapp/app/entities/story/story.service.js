(function() {
    'use strict';
    angular
        .module('simplesdlcApp')
        .factory('Story', Story);

    Story.$inject = ['$resource'];

    function Story ($resource) {
        var resourceUrl =  'api/stories/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
