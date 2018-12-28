var login = angular.module("bankCard",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.bankCard = {}
        $scope.getAppUsers = function(){
            mainService.getAppUsers(id).then(function(result) {
                    var success = result.success;
                    $scope.bankCard = result.data;
                },
                function(error){
                    alert("网络原因加载缓慢！");
                });
        }
            $scope.getAppUsers();
    }
])

login.service("mainService",function($http , $q){
    return {
        getAppUsers : function(id){
            return $http.get('/appUser/getAppUsers?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})
