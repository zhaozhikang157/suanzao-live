var login = angular.module("maps",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.maps = {

        }
        $scope.getTeams = function(){
            mainService.getTeams(id).then(function(result) {
                    var success = result.success;
                    $scope.maps = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.getTeams();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        getTeams : function(id){
            return $http.get('/appUser369/getTeams?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})