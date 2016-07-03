angular.module('app.main').controller('RedirectorCntl', function (SIGN_IN_DLG_PATH, $location, appContext) {
    'use strict';

    appContext.getCurrentUser().then(function (currentUser) {
        var redirectUrl;
        if (currentUser.isLoggedIn()) {
            redirectUrl = currentUser.getHomeDialogPath();
        } else {
            redirectUrl = SIGN_IN_DLG_PATH;
        }
        $location.url(redirectUrl);
    });
});