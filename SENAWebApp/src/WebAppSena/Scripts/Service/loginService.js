(function () {
    'use strict';

    angular
        .module('senaApp')
        .factory('loginService', loginService);

    loginService.$inject = ['$http'];

    function loginService($http) {
        
        function getData() { }

        return {
            getData: getData
        }
    }
})();