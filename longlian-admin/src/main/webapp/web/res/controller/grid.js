var appModule = angular.module('grid', ['ui.router']);

appModule.controller('MainCtrl', ['mainService','$scope','$http','$location','$window',
        function(mainService, $scope, $http , $location , $window) {
            $scope.greeting = 'Welcome to the JSON Web Token / AngularJR / Spring example!';
            $scope.token = null;
            $scope.error = null;
            $scope.roleUser = false;
            $scope.roleAdmin = false;
            $scope.roleFoo = false;

            $scope.login = function() {
                $scope.error = null;
                mainService.login($scope.userName).then(function(token) {
                    $scope.token = token;
                    $http.defaults.headers.common.Authorization = 'Bearer ' + token;
                    $scope.checkRoles();
                },
                function(error){
                    $scope.error = error
                    $scope.userName = '';
                });
            }


            $scope.checkRoles = function() {
                mainService.hasRole('user').then(function(user) {$scope.roleUser = user});
                mainService.hasRole('admin').then(function(admin) {$scope.roleAdmin = admin});
                mainService.hasRole('foo').then(function(foo) {$scope.roleFoo = foo});
            }

            $scope.logout = function() {
                //$scope.userName = '';
                alert( "token:" + $scope.token);
                alert("Authorization:" + $http.defaults.headers.common.Authorization );
                //$location.path('/user/index');
               // $window.location.href = "/user/index";
               /* var url = "/user/index";
                $.ajax({
                    type: "GET",
                    dataType:"html",
                    url: url,
                    async:  false,
                    success: function (data) {
                        try {
                            jsonObj = eval("(" + data + ")");
                        } catch (e) {
                            jsonObj = null;
                        }
                        if (callback) {
                            callback(jsonObj);
                        }
                    },
                    error: function (XMLHttpRequest, textStatus, errorThrown) {
                        jsonObj = {rtState: false, rtMsg: "Ajax Request Error"};
                    }
                });*/
                //$location.path("/user/index" , false);
                // $scope.token = null;
                //$http.defaults.headers.common.Authorization = '';

            }

            $scope.loggedIn = function() {
                return $scope.token !== null;
            }
        } ]);



appModule.service('mainService', function($http) {
    return {
        login : function(username) {
            return $http.post('/user/login', {name: username}).then(function(response) {
                return response.data.token;
            });
        },

        hasRole : function(role) {
            return $http.get('/api/role/' + role).then(function(response){
                console.log(response);
                return response.data;
            });
        },
        test : function(role,$location) {
          //  $http.path('/user/index2');
           // $location.path("/user/index");
          /*  return $http.get('/user/index2').then(function(response){
                console.log(response);
                return response.data;
            });*/
        }


    };
});
