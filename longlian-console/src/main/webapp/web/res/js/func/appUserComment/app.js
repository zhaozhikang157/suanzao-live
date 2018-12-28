var login = angular.module("appUserComment", []);
var config = ["$httpProvider", function ($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
}];
login.config(config);
login.controller("task", ["mainService", "$filter", "$scope", "$http", "$window",
    function (mainService, $filter, $scope, $http, $window) {
        $scope.appUserComment = {};
        $scope.sub = function (callBack) {
            if ($("form").valid(null, "error!") == false) {
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            data = mainService.doSave($scope.appUserComment).then(function (result) {
                    data = result;
                    callBack(result);
                    return result;
                },
                function (error) {
                    alert("保存失败，请与管理员联系！");
                });
        };

        $scope.getHandStatus = function () {
            mainService.getHandStatus().then(function (result) {
                    var list = result.data;
                    var list1 = [];
                    for (var i = 0; i < list.length; i++) {
                        list1.push(list[i]);
                        $scope.handStatus = list1;
                    }
                },
                function (error) {
                    jbox_notice({content: "获取处理状态失败！", autoClose: 2000, className: "error"});
                });
        };

        $scope.getAppUserCommentById = function () {
            mainService.getAppUserCommentById().then(function (result) {
                    $scope.appUserComment = result.data;
                },
                function (error) {
                    jbox_notice({content: "数据回显失败！", autoClose: 2000, className: "error"});
                });
        };

        $scope.getHandStatus();  //获取处理状态
        if(id>0){
            $scope.getAppUserCommentById();
        }
    }
]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (appUserComment) {
            appUserComment.id = id;
            return $http.post('/appUserCommentController/setHandComment', appUserComment).then(function (response) {
                return response.data;
            });
        },
        getHandStatus: function () {
            return $http.get('/appUserCommentController/getHandStatus?handStatus=' + handStatus).then(function (response) {
                return response.data;
            });
        },
        getAppUserCommentById: function () {
            return $http.get('/appUserCommentController/getAppUserCommentById?id=' + id).then(function (response) {
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
