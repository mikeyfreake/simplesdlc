(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .factory('SprintSearch', SprintSearch);

    SprintSearch.$inject = ['$resource'];

    function SprintSearch($resource) {
        var resourceUrl =  'api/_search/sprints/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
