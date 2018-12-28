var login = angular.module("func",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.func = {
           
            
        }

        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }
            
            

            var status =$("input[name='status']:checked").val()
            $scope.func.status =  status;
            var data = {};
            data=  mainService.doSave($scope.func).then(function(result) {
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
                 $scope.func = result.data;
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
        doSave : function(func){
            return $http.post('/roomFunc/saveFunc', func).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.post('/roomFunc/findById?id=' + id).then(function(response) {
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
