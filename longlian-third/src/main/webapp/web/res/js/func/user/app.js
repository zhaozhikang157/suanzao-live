var login = angular.module("mUser",[]);
var isAdding = false;
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.mUser = {
            id:0,
            name:"",
            gender:"1",
            tel:"",
            userId:"",
            qq:"",
            email:""
        }
        $scope.role = [];
        
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
            data=  mainService.doSave($scope.mUser).then(function(result) {
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

        $scope.getAllRole = function(){
            mainService.getAllRole().then(function(result) {
                    $scope.role = result.data;
                },
                function(error){
                    alert("获取角色失败！");
                });
        }

        $scope.getAllRole();

        $scope.getId = function(){
             mainService.getId(id).then(function(result) {
                var success = result.success;
                 $scope.mUser = result.data;
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
        doSave : function(mUser){
            return $http.post('/user/saveUser', mUser).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.post('/user/findById?id=' + id).then(function(response) {
                return response.data;
            });
        },
        getAllRole:function () {
            return $http.post('/user/getAllRole').then(function (response) {
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
