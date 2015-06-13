(function () {
    'use strict';

    angular
        .module('senaApp')
        .factory('loginService', loginService);

    loginService.$inject = ['$http', '$q'];

    console.log("Outside LoginService");

    function loginService($http, $q) {

        console.log("Inside loginService");
        
        function verifyWillCreateNewPassword(userId) {
            var deferred = $q.defer();

            $http.get('http://localhost:50747/Api/User/GetUser?userId=' + userId)
                .success(function(data) {
                    deferred.resolve(data);
                })
                .error(function(data) {
                    deferred.resolve({ createNewPassword: false});
                });

            return deferred.promise;
        }

        function createMjrSitePassword(user) {
            //var deferred = $q.defer();

            //$http.post('http://localhost:50747/Api/User/ChangeUserPassword', user)
            //    .success(function (data) {
            //        deferred.resolve(data);
            //    })
            //    .error(function (data) {
            //        deferred.reject(data);
            //    });

            //return deferred.promise;
        }

        return {
            verifyWillCreateNewPassword: verifyWillCreateNewPassword,
            createMjrSitePassword: createMjrSitePassword
        }
    }
})();