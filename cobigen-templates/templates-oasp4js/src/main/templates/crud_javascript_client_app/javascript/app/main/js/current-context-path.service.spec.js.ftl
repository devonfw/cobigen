describe('Service: currentContextPath', function () {
    'use strict';
    var currentContextPath, $window;

    beforeEach(module('app.main'));

    beforeEach(function () {
        $window = (function () {
            var location = {};
            return {
                location: location,
                letLocationPathnameBe: function (pathnameToBeReturned) {
                    location.pathname = pathnameToBeReturned;
                }
            };
        }());

        module(function ($provide) {
            $provide.value('$window', $window);
        });

        inject(function ($injector) {
            currentContextPath = $injector.get('currentContextPath');
        });
    });
    it('extracts context path when location path has 2 elements', function () {
        //given
        var path;
        $window.letLocationPathnameBe('/myContext/some-other-elements');
        //when
        path = currentContextPath.get();
        //then
        expect(path).toEqual('/myContext/');
    });
    it('returns // when no path contained in the location', function () {
        //given
        var path;
        $window.letLocationPathnameBe('/');
        //when
        path = currentContextPath.get();
        //then
        expect(path).toEqual('/');
    });
    it('returns // when path contained in the location is undefined', function () {
        //given
        var path;
        //when
        path = currentContextPath.get();
        //then
        expect(path).toEqual('/');
    });
});