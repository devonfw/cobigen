describe('Module: \'oasp-security\', service: \'oaspSecurityInterceptor\'', function () {
    'use strict';
    var $httpBackend, $http,
        addRequestSpy = jasmine.createSpy('addRequestSpy'),
        mockSendingRequestRespondingWith = function (response) {
            // given
            var requestPath = '/some-request';
            $httpBackend.whenGET(requestPath).respond(response);
            // when
            $http.get(requestPath);
            $httpBackend.flush();
        };

    beforeEach(function () {
        module('oasp.oaspSecurity', function ($provide) {
            $provide.value('oaspUnauthenticatedRequestResender', (function () {
                return {
                    addRequest: addRequestSpy
                };
            }()));
        });
    });

    /*jslint nomen: true*/
    beforeEach(inject(function (_$httpBackend_, _$http_) {
        $httpBackend = _$httpBackend_;
        $http = _$http_;
    }));
    /*jslint nomen: false*/

    afterEach(function () {
        $httpBackend.verifyNoOutstandingExpectation();
        $httpBackend.verifyNoOutstandingRequest();
    });

    it('passes by successful responses', function () {
        mockSendingRequestRespondingWith(200);
        // then
        expect(addRequestSpy).not.toHaveBeenCalled();
    });

    it('passes by error responses other than forbidden (403)', function () {
        mockSendingRequestRespondingWith(401);
        // then
        expect(addRequestSpy).not.toHaveBeenCalled();
    });

    it('adds an original request to the resending queue when the forbidden (403) response comes', function () {
        mockSendingRequestRespondingWith(403);
        // then
        expect(addRequestSpy).toHaveBeenCalled();
    });
});
