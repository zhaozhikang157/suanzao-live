var login = angular.module("appUser",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.appUser = {

        }
        $scope.getAppUserById = function(){
            mainService.getAppUserById(id).then(function(result) {
                    var success = result.success;
                    $scope.appUser = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.getAppUserById();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        getAppUserById : function(id){
            return $http.get('/appUser369/getById?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})