var login = angular.module("proxyBusinessTeam", []);
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
        $scope.proxyBusinessTeam = {
        };
        $scope.proxyLevels = [];
        $scope.sub = function (callBack) {
            if ($("form").valid(null, "error!") == false) {
                return false;
            }
            var data = {};
            $scope.proxyBusinessTeam.proxyLevel = $("#level").val();
            var amount =   $("#amount").val();
            var unitPrice = $("#unitPrice").val();
            var nums = $("#nums").val();
            var levelName = $("#levelName").val();
            var remarks = $("#remarks").val();
            $scope.proxyBusinessTeam.amount = amount;
            $scope.proxyBusinessTeam.unitPrice = unitPrice;
            $scope.proxyBusinessTeam.nums = nums;
            $scope.proxyBusinessTeam.levelName = levelName;
            $scope.proxyBusinessTeam.remarks = remarks;
            data = mainService.doSave($scope.proxyBusinessTeam).then(function (result) {
                    data = result;
                    callBack(result);
                    return result;
                },
                function (error) {
                    jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                });
        };

        $scope.getLevelList = function () {
            mainService.getLevelList().then(function (result) {
                    $scope.proxyLevels = result.data;
                },
                function (error) {
                    jbox_notice({content: "获取等级失败，请与管理员联系！", autoClose: 2000, className: "error"});
                });
        };
        $scope.getLevelList();
    }
]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (proxyBusinessTeam) {
            return $http.post('/proxyBusinessTeam/offlinePay?mobile=' + proxyBusinessTeam.mobile + "&levelName=" + encodeURIComponent(proxyBusinessTeam.levelName)+ "&remarks=" + encodeURIComponent(proxyBusinessTeam.remarks), proxyBusinessTeam).then(function (response) {
                return response.data;
            });
        },
        getLevelList: function () {
            return $http.get('/proxyLevel/getLevelList').then(function (response) {
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
