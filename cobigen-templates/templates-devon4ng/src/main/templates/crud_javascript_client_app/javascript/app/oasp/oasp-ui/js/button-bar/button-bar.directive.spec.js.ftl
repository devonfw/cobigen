describe('button-bar directive specs', function () {
    'use strict';
    var $compile, $rootScope;

    beforeEach(module('oasp.oaspUi.buttonBar'));

    beforeEach(inject(function (_$compile_, _$rootScope_) {
        $compile = _$compile_;
        $rootScope = _$rootScope_;
    }));

    it('renders a button', function () {
        // given
        var buttonLabel = 'View Details', renderedButtonLabel, element;
        $rootScope.buttonDefs = [
            {
                label: buttonLabel
            }
        ];
        // when
        element = $compile('<div data-button-bar="" data-button-defs="buttonDefs"></div>')($rootScope);
        $rootScope.$digest();
        // then
        renderedButtonLabel = element.find('button').eq(0).text();
        expect(renderedButtonLabel.trim()).toEqual(buttonLabel);
    });
    it('deactivates a button when isNotActive function returns true', function () {
        // given
        var element;
        $rootScope.buttonDefs = [
            {
                label: 'View Details',
                isNotActive: function () {
                    return true;
                }
            }
        ];
        // when
        element = $compile('<div data-button-bar="" data-button-defs="buttonDefs"></div>')($rootScope);
        $rootScope.$digest();
        // then
        expect(element.find('button').is(':disabled')).toBeTruthy();
    });
    it('activates a button when isActive returns true', function () {
        // given
        var element, isActive = false;
        $rootScope.buttonDefs = [
            {
                label: 'View Details',
                isActive: function () {
                    return isActive;
                }
            }
        ];
        // when
        element = $compile('<div data-button-bar="" data-button-defs="buttonDefs"></div>')($rootScope);
        $rootScope.$digest();
        isActive = true;
        $rootScope.$digest();
        // then
        expect(element.find('button').is(':disabled')).toBeFalsy();
    });
    it('calls onClick callback when button clicked', function () {
        // given
        var element;
        $rootScope.buttonDefs = [
            {
                label: 'View Details',
                onClick: angular.noop
            }
        ];
        spyOn($rootScope.buttonDefs[0], 'onClick');
        // when
        element = $compile('<div data-button-bar="" data-button-defs="buttonDefs"></div>')($rootScope);
        $rootScope.$digest();
        element.find('button').click();
        $rootScope.$digest();
        // then
        expect($rootScope.buttonDefs[0].onClick).toHaveBeenCalled();
    });
});
