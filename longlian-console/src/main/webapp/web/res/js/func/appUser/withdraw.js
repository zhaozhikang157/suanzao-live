var login = angular.module("withdraw",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.withdraw = {

        }
        $scope.withdrawDeposit = function(){
            mainService.withdrawDeposit(id).then(function(result) {
                    var success = result.success;
                    $scope.withdraw = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.withdrawDeposit();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        withdrawDeposit : function(id){
            return $http.get('/appUser/withdrawDeposit?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})