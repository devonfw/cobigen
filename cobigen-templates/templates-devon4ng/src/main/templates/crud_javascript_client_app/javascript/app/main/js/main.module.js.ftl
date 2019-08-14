angular.module('app.main', ['ngRoute', 'oasp.oaspUi', 'oasp.oaspSecurity', 'app.main.templates', 'oasp.oaspI18n', 'ui.bootstrap'])
    .constant('SIGN_IN_DLG_PATH', '/main/sign-in')
    .config(function (SIGN_IN_DLG_PATH, $routeProvider, oaspTranslationProvider) {
        'use strict';
        $routeProvider
            .when('/', {
                templateUrl: 'main/html/blank.html',
                controller: 'RedirectorCntl'
            })
            .when(SIGN_IN_DLG_PATH, {
                templateUrl: 'main/html/sign-in.html',
                controller: 'SignInCntl',
                resolve: {
                    check: ['homePageRedirector', function (homePageRedirector) {
                        return homePageRedirector.rejectAndRedirectToHomePageIfUserLoggedIn();
                    }]
                }
            })
            .when('/main/welcome', {
                templateUrl: 'main/html/welcome.html',
                controller: 'RedirectorCntl'
            })
            .otherwise({templateUrl: 'main/html/page-not-found.html'});

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