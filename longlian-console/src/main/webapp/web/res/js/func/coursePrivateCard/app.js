var login = angular.module("coursePrivateCardDto", []);
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
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                data = mainService.doSave($scope.coursePrivateCardDto).then(function (result) {
                        var success = result.success;
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            }

            $scope.getCourseAndTeacherInfo = function () {
                mainService.getCourseAndTeacherInfo($scope.coursePrivateCardDto.courseId).then(function(result){
                    var success = result.success;
                   // $scope.coursePrivateCardDto = result.data;  //获取  coursePrivateCardDTO;
                    $scope.coursePrivateCardDto.liveTopic = result.data.liveTopic;
                    $scope.coursePrivateCardDto.userId = result.data.userId;
                    $scope.coursePrivateCardDto.appUserName = result.data.appUserName;

                });
            }
            $scope.getId = function () {
                mainService.getId(id).then(function (result) {
                        var success = result.success;
                        console.log(result.data.courseId);
                        $scope.coursePrivateCardDto={};
                        $scope.coursePrivateCardDto.id = result.data.id;
                        $scope.coursePrivateCardDto.courseId = result.data.courseId;
                        $scope.coursePrivateCardDto.liveTopic = result.data.liveTopic;
                        $scope.coursePrivateCardDto.userId = result.data.userId;
                        $scope.coursePrivateCardDto.appUserName = result.data.appUserName;
                    },
                    function (error) {
                        jbox_notice({content: "请求失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            }
            if (id > 0) {
                $scope.getId();
            }
        }
    ])

login.service("mainService", function ($http, $q) {
    return {
        doSave : function(coursePrivateCardDto){
            return $http.post('/coursePrivateCard/doAddOrUpdate', coursePrivateCardDto).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.get('/coursePrivateCard/getCoursePrivateById?id=' + id).then(function(response) {
                return response.data;
            });
        },
        getCourseAndTeacherInfo : function(courseId){
            return $http.get('/coursePrivateCard/getCourseInfo?courseId=' + courseId).then(function(response) {
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
