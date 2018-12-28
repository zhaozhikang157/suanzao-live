var login = angular.module("WechatOfficial",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.WechatOfficial = {
           
        }
        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }
            var data = {};
            data=  mainService.doSave($scope.WechatOfficial).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    //alert("保存失败，请与管理员联系！");
                });
        }
        $scope.getWechatOfficial = function(){
            mainService.getWechatOfficial().then(function(result) {
                    $scope.WechatOfficial = result.data;
                },
                function(error){
                    //alert("保存失败，请与管理员联系！");
                });
        }
        $scope.getWechatOfficial();
    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(WechatOfficial){
            return $http.post('/wechat/updateMessageTemplet.user', WechatOfficial).then(function(response) {
                return response.data;
            });
        },
        getWechatOfficial : function(){
            return $http.post('/wechat/getWechatOfficial.user').then(function(response) {
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
