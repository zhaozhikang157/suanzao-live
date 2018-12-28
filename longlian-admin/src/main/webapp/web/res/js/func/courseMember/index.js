var login = angular.module("courseMember", []);
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
        $scope.courseMember = {};
        $scope.courseMemberList = [];
        $scope.getList = function () {
            mainService.getList($scope.courseMember).then(function (result) {
                    if (result.success) {
                        $scope.courseMemberList = result.data;
                        if (result.data.length == 0) {
                            jbox_notice({content: "该课程暂无人员报名！", autoClose: 2000, className: "error"});

                        }
                    } else {
                        jbox_notice({content: "无可展示报名数据！", autoClose: 2000, className: "success"});
                    }
                },
                function (error) {
                    alert("无可展示报名数据！");
                });
        };
        if (id > 0) {
            $scope.getList();
        }
    }
]);
login.service("mainService", function ($http, $q) {
    return {
        getList: function () {
            return $http.get('/courseMember/getList?id=' + id).then(function (response) {
                return response.data;
            });
        }
    }
});




