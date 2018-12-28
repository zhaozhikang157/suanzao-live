var login = angular.module("userRewardType",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.userRewardType = {
            status:"0",
        }
        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            data=  mainService.doSave($scope.userRewardType).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }

        $scope.setPicAddress= function(picAddress){
            $scope.userRewardType.picAddress = picAddress;
        }
        $scope.setchatPicAddress= function(picAddress){
            $scope.userRewardType.chatPicAddress = picAddress;
        }

        $scope.getBankById = function(){
            mainService.getBankById(id).then(function(result) {
                    var success = result.success;
                    $scope.userRewardType = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.getBankById();
        }


    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(userRewardType){
            return $http.post('/userReward/createUserRewardType', userRewardType).then(function(response) {
                return response.data;
            });
        }
        ,
        getBankById : function(id){
            return $http.get('/userReward/findById?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})

function doSave(callback){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.sub(function(json){
        callback(json);
    });
}
