var login = angular.module("banner", []);
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
            $scope.banner = {
                status:"0"
                     };
            $scope.bannerTypeList = [];
            $scope.typeTableList = [];
            $scope.sub = function (callBack) {
                if ($scope.banner.picAddress == undefined) {
                    jbox_notice({content: "请上传图片！", autoClose: 2000, className: "error"});
                    return;
                }
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                data = mainService.doSave($scope.banner).then(function (result) {
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            $scope.setPicAddress = function (picAddress) {
                $scope.banner.picAddress = picAddress;
            };
            $scope.findById = function () {
                mainService.findById(id).then(function (result) {
                        $scope.banner = result.data;
                        $scope.imgSrc = result.data.picAddress;

                        if (result.data.picAddress != '' && result.data.picAddress != null) {
                            //编辑时，显示已上传图片
                            $("#src").css('display', 'block');
                            $(".Img").attr("ng-src", result.data.picAddress);

                        }
                        mainService.findTypeNameList(result.data.type).then(function (result) {
                                $scope.typeTableList = result.data.typeTableList;

                            },
                            function (error) {
                                jbox_notice({content: "获取分类名称失败，请与管理员联系！", autoClose: 2000, className: "error"});
                            });
                        $scope.banner.typeTable = result.data.typeTableId;

                    },
                    function (error) {
                        jbox_notice({content: "数据回显失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            $scope.getBannerTypeList = function () {
                mainService.getBannerTypeList().then(function (result) {
                        $scope.bannerTypeList = result.data.bannerTypeList;

                    },
                    function (error) {
                        jbox_notice({content: "获取轮播图类型失败，请与管理员联系！！", autoClose: 2000, className: "error"});
                    });
            };


            $scope.typeChange = function(type){
                mainService.findTypeNameList(type).then(function (result) {
                        $scope.typeTableList = result.data.typeTableList;

                    },
                    function (error) {
                        jbox_notice({content: "获取分类名称失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };



            $scope.getBannerTypeList();
            if (id > 0) {
                $scope.findById();
            }
        }
    ]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (banner) {
            return $http.post('/banner/doSaveAndUpdate', banner).then(function (response) {
                return response.data;
            });
        },
        findById: function (id) {
            return $http.get('/banner/findById?id=' + id).then(function (response) {
                return response.data;
            });
        },
        findTypeNameList: function (type) {
            return $http.get('/banner/findTypeNameList?type=' + type).then(function (response) {
                return response.data;
            });
        },
        getBannerTypeList: function () {
            return $http.get('/banner/getBannerTypeList').then(function (response) {
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

