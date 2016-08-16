(function() {
    'use strict';

    angular
        .module('simplesdlcApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('story', {
            parent: 'entity',
            url: '/story?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Stories'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/story/stories.html',
                    controller: 'StoryController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('story-detail', {
            parent: 'entity',
            url: '/story/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Story'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/story/story-detail.html',
                    controller: 'StoryDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Story', function($stateParams, Story) {
                    return Story.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'story',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('story-detail.edit', {
            parent: 'story-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/story/story-dialog.html',
                    controller: 'StoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Story', function(Story) {
                            return Story.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('story.new', {
            parent: 'story',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/story/story-dialog.html',
                    controller: 'StoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                shortDescription: null,
                                description: null,
                                acceptanceCriteria: null,
                                classification: null,
                                effortPoints: null,
                                state: null,
                                urgency: null,
                                priority: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('story', null, { reload: true });
                }, function() {
                    $state.go('story');
                });
            }]
        })
        .state('story.edit', {
            parent: 'story',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/story/story-dialog.html',
                    controller: 'StoryDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Story', function(Story) {
                            return Story.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('story', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('story.delete', {
            parent: 'story',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/story/story-delete-dialog.html',
                    controller: 'StoryDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Story', function(Story) {
                            return Story.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('story', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
