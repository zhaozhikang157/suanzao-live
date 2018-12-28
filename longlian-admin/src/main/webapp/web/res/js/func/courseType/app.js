var login = angular.module("courseType", []);
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
            $scope.courseType = {

            };
            $scope.parentMenu = [];
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                data = mainService.doSave($scope.courseType).then(function (result) {
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };

            $scope.setPicAddress= function(picAddress){
                $scope.courseType.picAddress = picAddress;
            };
            $scope.getCoursesType = function(){
                mainService.getAllCourseType().then(function(result) {
                        $scope.parentMenu = result.data;
                    },
                    function(error){
                        alert("获取课程类型失败！");
                    });
            };

            $scope.findById = function () {
                mainService.findById(id).then(function (result) {
                        $scope.courseType = result.data;
                        $scope.imgSrc = result.data.picAddress;

                        if(result.data.picAddress!='' && result.data.picAddress!=null){
                            //编辑时，显示已上传图片
                            $("#src").css('display', 'block');
                            $(".Img").attr("ng-src",result.data.picAddress);
                        }

                    },
                    function (error) {
                        jbox_notice({content: "数据回显失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            if (id > 0) {
                $scope.findById();
            }
            $scope.getCoursesType();  //获取所有课程类型
        }
    ]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (courseType) {
            return $http.post('/courseType/doSaveAndUpdate', courseType).then(function (response) {
                return response.data;
            });
        },
        findById: function (id) {
            return $http.get('/courseType/findById?id=' + id).then(function (response) {
                return response.data;
            });
        },
        getAllCourseType:function () {
            return $http.get('/courseType/getCourseTypeListBySORT').then(function (response) {
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

