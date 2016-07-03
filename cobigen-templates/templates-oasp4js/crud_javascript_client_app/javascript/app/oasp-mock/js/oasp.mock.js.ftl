var oasp = {mock: {}};

oasp.mock.currentContextPathReturning = function (contextPath) {
    'use strict';

    return (function () {
        return {
            get: function () {
                return contextPath;
            }
        };
    }());
};

