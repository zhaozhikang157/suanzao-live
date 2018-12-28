var login = angular.module("systemPara",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.systemPara = {
            name:"",
            code:"",
            value:"",
            describe:""
        }
        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            data=  mainService.doSave($scope.systemPara).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }

        $scope.getSystemParaById = function(){
            mainService.getSystemParaById(id).then(function(result) {
                    var success = result.success;
                    $scope.systemPara = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.getSystemParaById();
        }


    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(systemPara){
            return $http.post('/systemParaController/doSaveAndUpdate', systemPara).then(function(response) {
                return response.data;
            });
        },
        getSystemParaById : function(id){
            return $http.get('/systemParaController/selectById?id=' + id).then(function(response) {
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
