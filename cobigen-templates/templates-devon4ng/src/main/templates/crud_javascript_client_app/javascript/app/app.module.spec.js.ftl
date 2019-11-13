describe('Module: \'app\'', function () {
    'use strict';

    it('sets the \'Hashbang\' mode', function () {
        // given
        var locationProvider;
        module('ng', function ($locationProvider) {
            locationProvider = $locationProvider;
            spyOn(locationProvider, 'html5Mode').and.callThrough();
        });
        // when
        module('app');
        // this is necessary to trigger loading the modules
        inject();
        // then
        expect(locationProvider.html5Mode).toHaveBeenCalledWith(false);
    });

    it('adds the spinner upon route changes', function () {
        // given
        module('oasp.oaspUi', function ($provide) {
            $provide.value('globalSpinner',
                jasmine.createSpyObj('globalSpinner', ['showOnRouteChangeStartAndHideWhenComplete']));
            // when
            module('app');
            // then
            inject(function (globalSpinner) {
                expect(globalSpinner.showOnRouteChangeStartAndHideWhenComplete).toHaveBeenCalled();
            });
        });
    });
});
