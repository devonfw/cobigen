describe('language-change directive specs', function () {
    'use strict';
    var $compile, $rootScope;

    beforeEach(module('oasp.oaspI18n'));

    beforeEach(inject(function (_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('should render change-language directive', function () {
        // given
        var element, dropdownToggle, dropdownToggleSpans, dropdownMenu;

        //when
        element = $compile('<language-change></language-change>')($rootScope);
        $rootScope.$digest();
        dropdownToggle = $('.dropdown-toggle', element);
        dropdownToggleSpans = $('span', dropdownToggle);
        dropdownMenu = $('.dropdown-menu', element);

        //then
        expect(dropdownToggleSpans[0].className).toEqual('icon-container');
        expect(dropdownToggleSpans[1].className).toEqual('icon icon--24');
        expect(dropdownToggleSpans[2].innerText).toEqual('OASP.LANGUAGE');
        expect(dropdownToggleSpans[3].className).toEqual('caret');
        expect(dropdownMenu.length).toEqual(1);
    });
});
