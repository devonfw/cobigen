angular.module('oasp.oaspUi.modal')
    .constant('oaspUiModalDefaults', {
        backdrop: 'static',
        keyboard: false
    })
    .config(function ($provide, oaspUiModalDefaults) {
        'use strict';
        var $modalDecorator = function ($delegate, globalSpinner) {
            return {
                open: function (options) {
                    globalSpinner.show();
                    var result = $delegate.open(angular.extend({}, oaspUiModalDefaults, options));
                    result.opened
                        .then(function () {
                            globalSpinner.hide();
                        }, function () {
                            globalSpinner.hide();
                        });
                    return result;
                }
            };
        };
        $provide.decorator('$modal', $modalDecorator);
    });