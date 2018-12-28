var login = angular.module("followRewardRule", []);
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
            $scope.followRewardRule = {
            };
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                data = mainService.doSave($scope.followRewardRule).then(function (result) {
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
                        var success = result.success;
                        $scope.followRewardRule = result.data;

                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };

            if (id > 0) {
                $scope.getId();
            }
        }
    ]);

login.service("mainService", function ($http, $q) {
    return {
        doSave : function(followRewardRule){
            return $http.post('/followRewardRule/doSaveAndUpdate', followRewardRule).then(function(response) {
                return response.data;
            });
        },

        getId : function(id){
            return $http.get('/followRewardRule/findById?id='+ id).then(function(response) {
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
