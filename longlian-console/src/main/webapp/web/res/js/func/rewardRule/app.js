var login = angular.module("rewardRule",[]);
var isAdding = false;
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.rewardRule = {
            id:0,
        }

        $scope.sub = function(callBack){
        	
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }
            
            if (isAdding) {
            	return false;
            }
            isAdding = true;
            
            var data = {};
            data=  mainService.doSave($scope.rewardRule).then(function(result) {
                    var success = result.success;
                    if(!success){
                        jbox_notice({content:result.msg,autoClose:2000 ,className:"warning"});
                        return false
                    }
                    data = result;
                    callBack(result);
                    isAdding = false;
                    return result;
                },
                function(error){
                	 isAdding = false;
                    alert("保存失败，请与管理员联系！");
                });
        }

        $scope.getId = function(){
             mainService.getId(id).then(function(result) {
                var success = result.success;
                 $scope.rewardRule = result.data;
            },
            function(error){
                alert("保存失败，请与管理员联系！");
            });
        }
        if(id > 0){
            $scope.getId();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(rewardRule){
            return $http.post('/rewardRule/saveRewardRule', rewardRule).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.post('/rewardRule/findRewardRuleById?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})

function doSave(callback ){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.sub(function(json){
        callback(json);
    });
}
