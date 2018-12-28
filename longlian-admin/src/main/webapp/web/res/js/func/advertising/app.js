var login = angular.module("advertising", []);
var config = [ "$httpProvider", function ($httpProvider) {
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
            $scope.advertising = {
            };
            $scope.advertTypes = [];
            $scope.systemTypes = [];
            $scope.sub = function (callBack) {
                if($scope.advertising.picAddress == undefined){
                    jbox_notice({content: "请上传图片！", autoClose: 2000, className: "error"});
                    return;
                }
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                data = mainService.doSave($scope.advertising).then(function (result) {
                        var success = result.success;
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.setPicAddress= function(picAddress){
                $scope.advertising.picAddress = picAddress;
            }
            $scope.getId = function () {
                mainService.getId(id).then(function (result) {
                        var success = result.success;
                        $scope.advertising = result.data;
                        $scope.imgSrc = result.data.picAddress;

                        if(result.data.picAddress!='' && result.data.picAddress!=null){
                            //编辑时，显示已上传图片
                            $("#src").css('display', 'block');
                            $(".Img").attr("ng-src",result.data.picAddress);
                        }

                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getList = function(){
                mainService.getList().then(function(result) {
                    $scope.advertTypes = result.data;
                    },
                    function(error){
                        jbox_notice({content: "获取类型名称失败！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getSystemTypeList = function(){
                mainService.getSystemTypeList().then(function(result) {
                        $scope.systemTypes = result.data.systemTypes;

                    },
                    function(error){
                        jbox_notice({content: "获取类型名称失败！", autoClose: 2000, className: "error"});
                    });
            }
            $scope.getList();
            $scope.getSystemTypeList();
            if (id > 0) {
                $scope.getId();
            }
        }
    ])

login.service("mainService", function ($http, $q) {
    return {
        doSave : function(advertising){
            return $http.post('/advertising/doSaveAndUpdate', advertising).then(function(response) {
                return response.data;
            });
        },
        getList: function () {
            return $http.post('/advertising/getList').then(function (response) {
                return response.data;
            });
        },

        getId : function(id){
            return $http.get('/advertising/findById?id=' + id).then(function(response) {
                return response.data;
            });
        },
        getSystemTypeList: function () {
            return $http.get('/advertising/getSystemTypeList').then(function (response) {
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
