describe('Provider: oaspTranslation', function () {
    'use strict';
    var oaspTranslation;
    beforeEach(function () {
        angular.module('fake.module', ['oasp.oaspI18n'], function (oaspTranslationProvider) {
            oaspTranslationProvider.enableTranslationForModule('dummy');
            oaspTranslationProvider.enableTranslationForModule('main', true);
            oaspTranslationProvider.setSupportedLanguages(
                [
                    {
                        key: 'en',
                        label: 'English'
                    },
                    {
                        key: 'de',
                        label: 'German',
                        'default': true
                    }
                ]
            );
        });

        module('fake.module');

        inject(function (_oaspTranslation_) {
            oaspTranslation = _oaspTranslation_;
        });
    });
    it('return default language', function () {
        expect(oaspTranslation.getDefaultLanguage().key).toBe('de');
    });
    it('sets translations for modules', function () {
        expect(oaspTranslation.moduleHasTranslations('main')).toBeTruthy();
        expect(oaspTranslation.moduleHasTranslations('dummy')).toBeTruthy();
    });
    it('not enable translation for configured module', function () {
        expect(oaspTranslation.moduleHasTranslations('not_configured')).toBeFalsy();
    });
    it('returns default module', function () {
        expect(oaspTranslation.getDefaultTranslationModule()).toBe('main');
    });
});