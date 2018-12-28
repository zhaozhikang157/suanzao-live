var login = angular.module("userLevel",[]);
var isAdding = false;
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.userLevel = {
            id:0,
            name:"",
            remark:"",
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
            data=  mainService.doSave($scope.userLevel).then(function(result) {
                    var success = result.success;
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
                 $scope.userLevel = result.data;
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
        doSave : function(userLevel){
            return $http.post('/userLevel/saveUserLevel', userLevel).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.post('/userLevel/findUserLevelById?id=' + id).then(function(response) {
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
