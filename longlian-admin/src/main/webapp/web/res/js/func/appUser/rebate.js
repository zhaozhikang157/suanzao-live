var login = angular.module("rebate",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.rebate = {

        }
        $scope.membershiprebate = function(){
            mainService.membershiprebate(id).then(function(result) {
                    var success = result.success;
                    $scope.rebate = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.membershiprebate();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        membershiprebate : function(id){
            return $http.get('/appUser/membershiprebate?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})