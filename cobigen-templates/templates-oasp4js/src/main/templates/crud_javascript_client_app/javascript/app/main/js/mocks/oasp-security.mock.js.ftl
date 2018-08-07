/**
 * Provides a mock of the oasp-security module. The mock decorates the security service so that the
 * security.checkIfUserLoggedInAndReinitializeAppContext() method does nothing instead of calling some REST services.
 * This is necessary when testing modules which depend on the oasp-security module, so that the REST services' calls
 * are not executed when loading these modules.
 */
angular.module('oasp.oaspSecurity')
    .config(function ($provide) {
        'use strict';

        var securityDecorator = function ($delegate) {
            return {
                logIn: $delegate.logIn,
                logOff: $delegate.logOff,
                getCurrentCsrfToken: $delegate.getCurrentCsrfToken,
                getCurrentUserProfile: $delegate.getCurrentUserProfile,
                checkIfUserIsLoggedInAndIfSoReinitializeAppContext: angular.noop
            };
        };
        $provide.decorator('oaspSecurityService', securityDecorator);
    });
