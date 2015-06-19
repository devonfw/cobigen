describe('Module: main, Controller: sign-in', function () {
    'use strict';
    var $scope, $location, oaspSecurityService, appContext, userHomeDialogPath;

    beforeEach(function () {
        oaspSecurityService = {
            logIn: angular.noop,
            checkIfUserIsLoggedInAndIfSoReinitializeAppContext: angular.noop
        };

        module('app.main', function ($provide) {
            $provide.value('oaspSecurityService', oaspSecurityService);
        });
    });

    beforeEach(inject(function ($rootScope, $controller, _$location_, $q) {
        appContext = {
            getCurrentUser: function () {
                return $q.when({
                    getHomeDialogPath: function () {
                        return userHomeDialogPath;
                    }
                });
            }
        };
        $location = _$location_;
        $scope = $rootScope;

        $controller('SignInCntl', {$scope: $scope, $location: $location, appContext: appContext});
    }));

    it('exposes errorMessage.text on $scope which is empty string initially', function () {
        expect($scope.errorMessage.text).toEqual('');
    });
    it('exposes errorMessage.hasOne() on $scope which returns false when no message', function () {
        expect($scope.errorMessage.hasOne()).toBeFalsy();
    });
    it('exposes hasErrorMessage() on $scope which returns true when message present', function () {
        // when
        $scope.errorMessage.text = 'Error occurred';
        // then
        expect($scope.errorMessage.hasOne()).toBeTruthy();
    });
    it('exposes clearMessage() on $scope which resets the errorMessage to empty string', function () {
        // given
        $scope.errorMessage.text = 'Error occurred';
        // when
        $scope.errorMessage.clear();
        // then
        expect($scope.errorMessage.text).toEqual('');
    });
    it('exposes signIn() on $scope which changes to the user\'s home dialog on success', inject(function ($q) {
        // given
        userHomeDialogPath = '/some-module/home';
        spyOn(oaspSecurityService, 'logIn').and.callFake(function () {
            return $q.when();
        });
        $scope.loginForm = {
            $invalid: false
        };
        $scope.validation.forceShowingValidationErrors = true;
        // when
        $scope.signIn();
        $scope.$apply();
        // then
        expect($location.path()).toEqual(userHomeDialogPath);

    }));
    it('exposes signIn() on $scope which adds an error message and clears the form on failure', inject(function ($q) {
        // given
        spyOn(oaspSecurityService, 'logIn').and.callFake(function () {
            return $q.reject();
        });
        $scope.loginForm = {
            $invalid: false,
            $setPristine: jasmine.createSpy('$setPristine')
        };
        $scope.validation.forceShowingValidationErrors = true;
        // when
        $scope.signIn();
        $scope.$apply();
        // then
        expect($scope.errorMessage.text).toEqual('Anmeldung fehlgeschlagen. Bitte prüfen Sie ihre Eingabe und versuchen es erneut!');
        expect($scope.credentials).toEqual({});
        expect($scope.validation.forceShowingValidationErrors).toBeFalsy();
        expect($scope.loginForm.$setPristine).toHaveBeenCalled();
    }));
    it('exposes signIn() on $scope which forces showing errors when the form invalid', function () {
        // given
        $scope.loginForm = {
            $invalid: true
        };
        $scope.validation.forceShowingValidationErrors = false;
        // when
        $scope.signIn();
        // then
        expect($scope.validation.forceShowingValidationErrors).toBeTruthy();
    });
    it('exposes validation.userNameNotProvided() on $scope which returns true if field dirty and empty', function () {
        // given // when
        $scope.loginForm = {
            userName: {
                $dirty: true,
                $error: {
                    required: true
                }
            }
        };
        $scope.validation.forceShowingValidationErrors = false;
        // then
        expect($scope.validation.userNameNotProvided()).toBeTruthy();
    });
    it('exposes validation.userNameNotProvided() on $scope which returns true if field empty and forced validation',
        function () {
            // given // when
            $scope.loginForm = {
                userName: {
                    $dirty: false,
                    $error: {
                        required: true
                    }
                }
            };
            $scope.validation.forceShowingValidationErrors = true;
            // then
            expect($scope.validation.userNameNotProvided()).toBeTruthy();
        });
    it('exposes validation.userNameNotProvided() on $scope which returns false if field empty and neither validation forced nor filed dirty',
        function () {
            // given // when
            $scope.loginForm = {
                userName: {
                    $dirty: false,
                    $error: {
                        required: true
                    }
                }
            };
            $scope.validation.forceShowingValidationErrors = false;
            // then
            expect($scope.validation.userNameNotProvided()).toBeFalsy();
        });
    it('exposes validation.passwordNotProvided() on $scope which returns true if field dirty and empty', function () {
        // given // when
        $scope.loginForm = {
            password: {
                $dirty: true,
                $error: {
                    required: true
                }
            }
        };
        $scope.validation.forceShowingValidationErrors = false;
        // then
        expect($scope.validation.passwordNotProvided()).toBeTruthy();
    });
    it('exposes validation.passwordNotProvided() on $scope which returns true if field empty and forced validation',
        function () {
            // given // when
            $scope.loginForm = {
                password: {
                    $dirty: false,
                    $error: {
                        required: true
                    }
                }
            };
            $scope.validation.forceShowingValidationErrors = true;
            // then
            expect($scope.validation.passwordNotProvided()).toBeTruthy();
        });
    it('exposes validation.passwordNotProvided() on $scope which returns false if field empty and neither validation forced nor filed dirty',
        function () {
            // given // when
            $scope.loginForm = {
                password: {
                    $dirty: false,
                    $error: {
                        required: true
                    }
                }
            };
            $scope.validation.forceShowingValidationErrors = false;
            // then
            expect($scope.validation.passwordNotProvided()).toBeFalsy();
        });
});