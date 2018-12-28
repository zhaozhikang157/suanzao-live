var login = angular.module("appUser",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.appUser = {
            name:"",
            mobile:""
        }
        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                return false;
            }
            var mobile = $("input[name='mobile']").val();
            mainService.isAppUserVip(mobile).then(function(result) {
                    if(!result.success){
                        $("input[name='mobile']").val("");
                        jbox_notice({content:"手机号已存在,请重新输入！",autoClose:2000 ,className:"error"});
                    }else{
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
                },
                function(error){
                    alert("错误，请与管理员联系！");
                });
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(appUser){
            return $http.post('/appUser369/saveUser?mobile=' + appUser.mobile + "&name=" + appUser.name).then(function(response) {
                return response.data;
            });
        },
        isAppUserVip : function(mobile){
            return $http.get('/appUser369/isAppUserVip?mobile=' + mobile).then(function(response) {
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
