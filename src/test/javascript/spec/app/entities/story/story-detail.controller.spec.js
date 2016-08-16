'use strict';

describe('Controller Tests', function() {

    describe('Story Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockStory, MockProduct, MockRelease, MockUser, MockSprint;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockStory = jasmine.createSpy('MockStory');
            MockProduct = jasmine.createSpy('MockProduct');
            MockRelease = jasmine.createSpy('MockRelease');
            MockUser = jasmine.createSpy('MockUser');
            MockSprint = jasmine.createSpy('MockSprint');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'Story': MockStory,
                'Product': MockProduct,
                'Release': MockRelease,
                'User': MockUser,
                'Sprint': MockSprint
            };
            createController = function() {
                $injector.get('$controller')("StoryDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'simplesdlcApp:storyUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
