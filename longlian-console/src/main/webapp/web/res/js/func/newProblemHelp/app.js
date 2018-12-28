var login = angular.module("newProblemHelp", []);
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
            $scope.newProblemHelp = {

            };
            $scope.lecturers = [];
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                if($("#address").val() == ""){
                    jbox_notice({content: "请选择文件！", autoClose: 2000, className: "error"});
                    return;
                }

                var data = {};
                data = mainService.doSave($scope.newProblemHelp).then(function (result) {
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };


            $scope.findById = function () {
                mainService.findById(id).then(function (result) {
                        $scope.newProblemHelp = result.data;

                    },
                    function (error) {
                        jbox_notice({content: "数据回显失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            if (id > 0) {
                $scope.findById();
            }
        }
    ]);

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (newProblemHelp) {
            return $http.post('/newProblemHelp/doSaveAndUpdate', newProblemHelp).then(function (response) {
                return response.data;
            });
        },
        findById: function (id) {
            return $http.get('/newProblemHelp/findById?id=' + id).then(function (response) {
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

