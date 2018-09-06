describe('Module: \'oasp-security\', service: \'oaspUnauthenticatedRequestResender\'', function () {
    'use strict';
    var token = 'SADS8788sa86d8sa', headerName = 'CSRF_TOKEN',
        $httpBackend, $q, $scope, oaspUnauthenticatedRequestResender, authenticatorPromise, successCallback, failureCallback,
        myAuthenticator, oaspSecurityService;

    beforeEach(function () {
        myAuthenticator = (function () {
            return {
                execute: function () {
                    return authenticatorPromise;
                }
            };
        }());
        oaspSecurityService = (function () {
            return {
                getCurrentCsrfToken: function () {
                    return (function () {
                        return {
                            getHeaderName: function () {
                                return headerName;
                            },
                            getToken: function () {
                                return token;
                            }
                        };
                    }());
                },
                checkIfUserIsLoggedInAndIfSoReinitializeAppContext: angular.noop
            };
        }());

        angular.module('module-using-oasp-security', ['oasp.oaspSecurity'])
            .config(function (oaspUnauthenticatedRequestResenderProvider) {
                oaspUnauthenticatedRequestResenderProvider.setAuthenticatorServiceName('myAuthenticator');
            })
            .value('myAuthenticator', myAuthenticator)
            .value('oaspSecurityService', oaspSecurityService);

        module('module-using-oasp-security');
    });

    /*jslint nomen: true*/
    beforeEach(inject(function (_$httpBackend_, _$q_, $rootScope, _oaspUnauthenticatedRequestResender_) {
        $httpBackend = _$httpBackend_;
        $q = _$q_;
        $scope = $rootScope;
        oaspUnauthenticatedRequestResender = _oaspUnauthenticatedRequestResender_;
        successCallback = jasmine.createSpy('success');
        failureCallback = jasmine.createSpy('failure');
    }));
    /*jslint nomen: false*/

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('resends a request (updating CSRF protection data) upon successful authentication', function () {
        // given
        var requestPath = '/some-url',
            csrfProtection = {
                token: 'SADS8788sa86d8sa',
                headerName: 'CSRF_TOKEN'
            },
            originalRequest = {
                method: 'GET',
                url: requestPath,
                headers: {}
            };
        $httpBackend.whenGET(requestPath).respond(200);
        authenticatorPromise = $q.when(csrfProtection);
        // when
        oaspUnauthenticatedRequestResender.addRequest(originalRequest)
            .then(successCallback, failureCallback);
        $scope.$apply();
        $httpBackend.flush();
        // then
        expect(successCallback).toHaveBeenCalled();
        expect(failureCallback).not.toHaveBeenCalled();
        expect(originalRequest.headers[csrfProtection.headerName]).toEqual(csrfProtection.token);
    });

    it('resends two requests upon successful authentication (making sure authentication called only once)',
        function () {
            // given
            var requestPath = '/some-url',
                csrfProtection = {
                    token: 'SADS8788sa86d8sa',
                    headerName: 'CSRF_TOKEN'
                },
                originalRequest = {
                    method: 'GET',
                    url: requestPath,
                    headers: {}
                },
                successCallback2 = jasmine.createSpy('successCallback2'),
                failureCallback2 = jasmine.createSpy('failureCallback2');
            $httpBackend.whenGET(requestPath).respond(200);
            $httpBackend.whenGET(requestPath).respond(200);
            spyOn(myAuthenticator, 'execute').and.returnValue($q.when(csrfProtection));
            // when
            oaspUnauthenticatedRequestResender.addRequest(originalRequest)
                .then(successCallback, failureCallback);
            oaspUnauthenticatedRequestResender.addRequest(originalRequest)
                .then(successCallback2, failureCallback2);
            $scope.$apply();
            $httpBackend.flush();
            // then
            expect(successCallback).toHaveBeenCalled();
            expect(failureCallback).not.toHaveBeenCalled();
            expect(successCallback2).toHaveBeenCalled();
            expect(failureCallback2).not.toHaveBeenCalled();
            expect(myAuthenticator.execute.calls.count()).toEqual(1);
        });

    it('rejects adding a request when authentication failed', function () {
        // given
        var originalRequest = {
            method: 'GET',
            url: '/some-url',
            headers: {}
        };
        authenticatorPromise = $q.reject();
        // when
        oaspUnauthenticatedRequestResender.addRequest(originalRequest)
            .then(successCallback, failureCallback);
        $scope.$apply();
        // then
        expect(failureCallback).toHaveBeenCalled();
        expect(successCallback).not.toHaveBeenCalled();
    });

    it('rejects adding a request when resending it failed', function () {
        // given
        var requestPath = '/some-url',
            csrfProtection = {
                token: 'SADS8788sa86d8sa',
                headerName: 'CSRF_TOKEN'
            },
            originalRequest = {
                method: 'GET',
                url: requestPath,
                headers: {}
            };
        $httpBackend.whenGET(requestPath).respond(401);
        authenticatorPromise = $q.when(csrfProtection);
        // when
        oaspUnauthenticatedRequestResender.addRequest(originalRequest)
            .then(successCallback, failureCallback);
        $scope.$apply();
        $httpBackend.flush();
        // then
        expect(successCallback).not.toHaveBeenCalled();
        expect(failureCallback).toHaveBeenCalled();
        expect(originalRequest.headers[csrfProtection.headerName]).toEqual(csrfProtection.token);
    });
});
