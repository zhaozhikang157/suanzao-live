var login = angular.module("shareChannel",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.shareChannel = {
           
            
        }

        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }
            var data = {};
            data=  mainService.doSave($scope.shareChannel).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    //alert("保存失败，请与管理员联系！");
                });
        }

        $scope.getId = function(){
             mainService.getId(id).then(function(result) {
                var success = result.success;
                 $scope.shareChannel = result.data;
            },
            function(error){
                //alert("保存失败，请与管理员联系！");
            });
        }
        if(id > 0){
            $scope.getId();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(shareChannel){
            return $http.post('/shareChannel/saveShareChannel', shareChannel).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.post('/shareChannel/findById?id=' + id).then(function(response) {
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
