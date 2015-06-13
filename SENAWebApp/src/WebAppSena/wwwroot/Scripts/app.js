(function () {
    'use strict';

    angular.module('senaApp', ['ngRoute'])
    .config(function ($routeProvider) {
        $routeProvider
            //.when('/', {
            //    templateUrl: '/Views/createPassword.html',
            //    controller: 'loginController'
            //})
            .when('/user/:userId', {
                templateUrl: 'Views/createPassword.html',
                controller: 'loginController'
            });

        //$locationProvider.html5Mode(true);
    });
})();