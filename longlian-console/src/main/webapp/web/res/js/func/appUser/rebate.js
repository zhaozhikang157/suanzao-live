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
        $scope.toquery = function() {
            $scope.rebate= $scope.rebate;
            var map={};
            map["beginTime"]=$("#beginTime").val();
            map["endTime"]=$("#endTime").val();
            var mapRequest=JSON.stringify(map);
            mainService.duQuery(id,map).then(function(result) {
                    var success = result.success;
                    $scope.rebate = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        };
        if(id > 0){
            $scope.membershiprebate();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        membershiprebate : function(id){
            var param = {};
            param.id = id;
            var data=JSON.stringify(param)
            return $http.post('/appUser/membershiprebate' , data).then(function(response) {
                return response.data;
            });
        },
        duQuery:function(id,map){
            var param = {};
            param.id = id;
            param.timeMap = map;
            var data=JSON.stringify(param)
            return $http.post('/appUser/membershiprebate', param).then(function(response) {
                return response.data;
            });
       }
    }
})