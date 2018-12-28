var login = angular.module("mRes", []);
login.controller("task",
    ["mainService", "$filter", "$scope", "$http", "$window",
        function (mainService, $filter, $scope, $http, $window) {
            $scope.mRes = {
                id:0,
                status: "0"
            };
            $scope.parentMenu = [];
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                    return false;
                }
                var data = {};
                data = mainService.doSave($scope.mRes).then(function (result) {
                        var success = result.success;
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        alert("保存失败，请与管理员联系！");
                    });
            };
            
            $scope.getAllMenu = function(){
                mainService.getAllMenu(id).then(function(result) {
                        $scope.parentMenu = result.data;
                    },
                    function(error){
                        alert("获取所有菜单失败！");
                    });
            };

            $scope.getId = function () {
                mainService.getId(id).then(function (result) {
                        var success = result.success;
                        $scope.mRes = result.data;
                        $scope.getAllMenu();
                    },
                    function (error) {
                        alert("保存失败，请与管理员联系！");
                    });
            };
            if (id > 0) {
                $scope.getId();
            } else {
                $scope.getAllMenu();
            }
        }
    ]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (mRes) {
            return $http.post('/mRes/saveCatalog', mRes).then(function (response) {
                return response.data;
            });
        },
        getId: function (id) {
            return $http.get('/mRes/findCataById?id=' + id).then(function (response) {
                return response.data;
            });
        },
        getAllMenu:function (id) {
            return $http.get('/mRes/getMenus?id=' + id).then(function (response) {
                return response.data;
            });
        }
    }
});

function doSave(callback) {
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var json = $scope.sub(function (json) {
        callback(json);
    });
}
