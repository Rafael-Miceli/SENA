(function () {
    'use strict';

    angular.module('senaApp', [
        // Angular modules 
        'ngRoute'

        // Custom modules 

        // 3rd Party Modules
        
    ]).config(config);

    function config($routeProvider, $locationProvider) {
        $routeProvider
            .when('/', {
                templateUrl: '/Views/login.html',
                controller: 'loginController'
            });

        $locationProvider.html5Mode(true);
    }
})();