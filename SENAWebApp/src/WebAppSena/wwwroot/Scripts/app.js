(function () {
    'use strict';

    angular.module('senaApp', ['ngRoute'])
    .config(function ($routeProvider) {

        $routeProvider
            .when('/user/:userId', {
                templateUrl: '/Views/createPassword.html',
                controller: 'loginController',
                controllerAs: 'vm'
            })
            .when('/passwordSuccess', {
                templateUrl: '/Views/createPasswordSuccess.html',
            });

        //$locationProvider.html5Mode({
        //    enabled: true,
        //    requireBase: false
        //});
    });
})();