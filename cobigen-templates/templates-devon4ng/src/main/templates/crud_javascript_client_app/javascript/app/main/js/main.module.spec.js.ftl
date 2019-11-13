describe('\'app.main\' module', function () {
    'use strict';
    var routeProvider;

    beforeEach(function () {
        module('ngRoute', function ($routeProvider) {
            routeProvider = $routeProvider;
            spyOn(routeProvider, 'when').and.callThrough();
            spyOn(routeProvider, 'otherwise').and.callThrough();
        });
        module('app.main');
        // this is necessary to trigger loading the modules
        inject();
    });

    // This is necessary to trigger loading the modules above; use it to inject services once they are needed
    beforeEach(inject());

    it('defines a route for the welcome dialog', function () {
        expect(routeProvider.when).toHaveBeenCalledWith('/main/welcome', { templateUrl : 'main/html/welcome.html' });
    });

    it('defines a route for \'/\' delegating the RedirectorCntl', function () {
        expect(routeProvider.when).toHaveBeenCalledWith('/', {
            templateUrl: 'main/html/blank.html',
            controller: 'RedirectorCntl'
        });
    });

    it('defines the default route redirecting to the welcome dialog', function () {
        expect(routeProvider.otherwise).toHaveBeenCalledWith({templateUrl: 'main/html/page-not-found.html'});
    });
});