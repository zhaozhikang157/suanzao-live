var login = angular.module("iosPayType",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.iosPayType = {
            type:"0",
            status:"0",
        }
        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            data=  mainService.doSave($scope.iosPayType).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }

        $scope.getBankById = function(){
            mainService.getBankById(id).then(function(result) {
                    var success = result.success;
                    $scope.iosPayType = result.data;
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
        doSave : function(iosPayType){
            return $http.post('/iosPayType/createIosPayType', iosPayType).then(function(response) {
                return response.data;
            });
        }
        ,
        getBankById : function(id){
            return $http.get('/iosPayType/findById?id=' + id).then(function(response) {
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
