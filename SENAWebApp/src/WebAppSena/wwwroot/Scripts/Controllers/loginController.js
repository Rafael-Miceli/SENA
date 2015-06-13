(function () {
    'use strict';

    angular.module('senaApp').controller('loginController', ['loginService', '$routeParams', loginController]);

    console.log("Out controller");

    function loginController(loginService, $routeParams) {

        console.log("Inside Controller");

        var vm = this;
        vm.title = 'loginController';
        vm.willCreateNewPassword = false;
        vm.user = {};

        function verifyWillCreateNewPassword() {
            vm.userId = $routeParams.userId;

            if (vm.userId) {
                console.log("Verifying to create new password");

                loginService.verifyWillCreateNewPassword(vm.userId).then(
                    function (data) {
                        vm.user = data;
                        vm.willCreateNewPassword = vm.user.createNewPassword;
                    });
            }
        }

        vm.createPassword = function () {
            //Create user in Azure
            var client = new WindowsAzure.MobileServiceClient(
            "https://arduinoapp.azure-mobile.net/",
            "QkTMsFHSEaNGuiKVsywYYHpHnIHMUB64");

            var account = { username: vm.user.Username, cliente: vm.user.CompanyName, password: vm.password }
            var tableAccounts = client.getTable("accounts");

            tableAccounts.insert(account);

            //get new row id
            var senaId = "";

            tableAccounts.where({ username: vm.user.Username }).read().done(function(results) {
                console.log(results);

                senaId = results[0].id;
            });

            //Set new password to MjrSite
            vm.user.UserSenaId = senaId;
            vm.user.NewPassword = vm.password;
            loginService.createMjrSitePassword(vm.user).then(
                function(data) {
                    //Redirect to success page        

                });
        }


        verifyWillCreateNewPassword();
    }
})();


$(window, document, undefined).ready(function () {

    $('input').blur(function () {
        var $this = $(this);
        if ($this.val())
            $this.addClass('used');
        else
            $this.removeClass('used');
    });

    var $ripples = $('.ripples');

    $ripples.on('click.Ripples', function (e) {

        var $this = $(this);
        var $offset = $this.parent().offset();
        var $circle = $this.find('.ripplesCircle');

        var x = e.pageX - $offset.left;
        var y = e.pageY - $offset.top;

        $circle.css({
            top: y + 'px',
            left: x + 'px'
        });

        $this.addClass('is-active');

    });

    $ripples.on('animationend webkitAnimationEnd mozAnimationEnd oanimationend MSAnimationEnd', function (e) {
        $(this).removeClass('is-active');
    });

});
