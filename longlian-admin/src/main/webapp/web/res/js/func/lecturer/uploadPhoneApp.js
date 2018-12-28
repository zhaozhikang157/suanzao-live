var login = angular.module("lecturerUpload", []);
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
            $scope.lecturer = {
            	homePhoto: "",
                id: ""
            };
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                if($scope.lecturer.homePhoto ==""){
                	jbox_notice({content: "请选择头像！", autoClose: 2000, className: "error"});
                	return;
                }
                data =  mainService.doSave($scope.lecturer).then(function (result) {
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            $scope.setHomePhoto = function(homePhoto) {
                $scope.lecturer.homePhoto = homePhoto;
            };
            $scope.setId = function(id) {
                $scope.lecturer.id = id;
            };
        }
    ])

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (lecturer) {
            return $http.post('/lecturer/uploadHomePhone', lecturer).then(function (response) {
            	var str = JSON.stringify(response.data);
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
