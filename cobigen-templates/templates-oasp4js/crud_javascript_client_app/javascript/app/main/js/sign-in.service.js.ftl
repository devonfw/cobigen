angular.module('app.main')
    .factory('signIn', function (oaspSecurityService, globalSpinner) {
        'use strict';

        return function ($scope, signInSuccessCallback) {
            $scope.errorMessage = {
                text: '',
                hasOne: function () {
                    return this.text ? true : false;
                },
                clear: function () {
                    this.text = '';
                }
            };

            $scope.credentials = {};

            $scope.validation = {
                userNameNotProvided: function () {
                    return ($scope.loginForm.userName.$dirty || this.forceShowingValidationErrors) &&
                        $scope.loginForm.userName.$error.required;
                },
                passwordNotProvided: function () {
                    return ($scope.loginForm.password.$dirty || this.forceShowingValidationErrors) &&
                        $scope.loginForm.password.$error.required;
                },
                forceShowingValidationErrors: false
            };

            $scope.signIn = function () {
                var addErrorMessageAndClearForm = function (message) {
                    $scope.errorMessage.text = message;
                    $scope.credentials = {};
                    $scope.validation.forceShowingValidationErrors = false;
                    $scope.loginForm.$setPristine();
                };
                if ($scope.loginForm.$invalid) {
                    $scope.validation.forceShowingValidationErrors = true;
                } else {
                    globalSpinner.decorateCallOfFunctionReturningPromise(function () {
                        return oaspSecurityService.logIn($scope.credentials.username, $scope.credentials.password);
                    }).then(function () {
                        signInSuccessCallback();
                    }, function () {
                        addErrorMessageAndClearForm('Sign-in failed. Please check your credentials and try again!');
                    });
                }
            };
        };
    });

