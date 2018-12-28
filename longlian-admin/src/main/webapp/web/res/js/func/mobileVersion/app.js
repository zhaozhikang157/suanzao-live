var login = angular.module("mobileVersion", []);
var config = ["$httpProvider", function ($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
}];
login.config(config);
login.controller("task",
    ["mainService", "$filter", "$scope", "$http", "$window",
        function (mainService, $filter, $scope, $http, $window) {
            $scope.mobileVersion = {


            }
            $scope.versionTypes = [];
            $scope.statuses = [];
            $scope.isFoceUpdates = [];
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                    return false;
                }
                if (isUploading) {
                	jbox_notice({content: "文件正在上传中!", autoClose: 2000, className: "error"});
           		 	return false;
                }
                
                var data = {};
                data = mainService.doSave($scope.mobileVersion).then(function (result) {
                        var success = result.success;
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.setAddress= function(downloadAddress){
                $scope.mobileVersion.downloadAddress = downloadAddress;
            }
            $scope.getId = function () {
                mainService.getId(id).then(function (result) {
                        var success = result.success;
                        $scope.mobileVersion = result.data;
                        $scope.imgSrc = result.data.picAddress;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getStateList = function(){
                mainService.getStateList().then(function(result) {
                        $scope.statuses = result.data.statuses;
                    },
                    function(error){
                        jbox_notice({content: "获取版本状态失败！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getIsFoceUpdate = function(){
                mainService.getIsFoceUpdate().then(function(result) {
                        $scope.isFoceUpdates = result.data.isFoceUpdates;
                    },
                    function(error){
                        jbox_notice({content: "获取上传状态失败！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getVersionType = function(){
                mainService.getVersionType().then(function(result) {
                        $scope.versionTypes = result.data.versionTypes;
                    },
                    function(error){
                        jbox_notice({content: "获取版本类型失败！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getStateList();
            $scope.getIsFoceUpdate();
            $scope.getVersionType();
            if (id > 0) {
                $scope.getId();
            }
        }
    ])

login.service("mainService", function ($http, $q) {
    return {
        doSave : function(mobileVersion){
            delete mobileVersion["versionTypes"];
            delete mobileVersion["statuses"];
            delete mobileVersion["isFoceUpdates"];
            delete mobileVersion["type"];
            delete mobileVersion["time"];
            return $http.post('/mobileVersion/doSaveAndUpdate', mobileVersion ).then(function(response) {
                return response.data;
            });
        },
        getStateList: function () {
                    return $http.get('/mobileVersion/getStateList').then(function (response) {
                        return response.data;
            });
        },
        getIsFoceUpdate: function () {
            return $http.get('/mobileVersion/getIsFoceUpdate').then(function (response) {
                return response.data;
            });
        },
        getVersionType: function () {
            return $http.get('/mobileVersion/getVersionType').then(function (response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.get('/mobileVersion/findById?id=' + id).then(function(response) {
                return response.data;
            });
        }

    }
})

function doSave(callback) {
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var json = $scope.sub(function (json) {
        callback(json);
    });
}
