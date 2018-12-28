var login = angular.module("proxyLevel", []);
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
        $scope.proxyLevel = {
            amount:"0"
        };
        $scope.sub = function (callBack) {
            if ($("form").valid(null, "error!") == false) {
                return false;
            }
            $scope.proxyLevel.amount = $("#amount").val();
            var data = {};
            data = mainService.doSave($scope.proxyLevel).then(function (result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function (error) {
                    jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                });
        };

        $scope.getId = function () {
            mainService.getId(id).then(function (result) {
                    $scope.proxyLevel = result.data;
                },
                function (error) {
                    jbox_notice({content: "数据回显失败，请与管理员联系！", autoClose: 2000, className: "error"});
                });
        };

        $scope.getLevel = function () {
            mainService.getLevel().then(function (result) {
                    $scope.proxyLevel.grade = result.data;
                },
                function (error) {
                    jbox_notice({content: "获取等级失败，请与管理员联系！", autoClose: 2000, className: "error"});
                });
        };

        if (id > 0) {
            $scope.getId();
        } else {
            $scope.getLevel();
        }
    }
]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (proxyLevel) {
            return $http.post('/proxyLevel/doSaveAndUpdate', proxyLevel).then(function (response) {
                return response.data;
            });
        },
        getId: function (id) {
            return $http.get('/proxyLevel/findById?id=' + id).then(function (response) {
                return response.data;
            });
        },
        getLevel: function () {
            return $http.get('/proxyLevel/getLevel?').then(function (response) {
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
