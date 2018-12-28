var login = angular.module("appUser",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.appUser = {

        }

        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            data=  mainService.doSave($scope.appUser).then(function(result) {
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
            $scope.appUser.photo = picAddress;
        }

        $scope.getAppUserById = function(){
            mainService.getAppUserById(id).then(function(result) {
                    var success = result.success;
                    $scope.appUser = result.data;
                    $scope.photo = result.data.photo;

                    if(result.data.photo!='' && result.data.photo!=null){
                        //编辑时，显示已上传图片
                        //$("#src").css('display', 'block');
                        $(".Img").attr("ng-src",result.data.photo);
                    }
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.getAppUserById();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        getAppUserById : function(id){
            return $http.get('/avatar/getById?id=' + id).then(function(response) {
                return response.data;
            });
        },
        doSave : function(appUser){
            return $http.post('/avatar/doSave', appUser).then(function(response) {
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