var login = angular.module("courseType", []);
var config = [ "$httpProvider", function ($httpProvider) {
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
        $scope.courseType = {

        };
        $scope.courseTypeList = [];
        $scope.getList=function(){
            mainService.getList($scope.courseType).then(function (result) {
                    if(result.success){
                        $scope.courseTypeList=result.data;
                    }else{
                        jbox_notice({content:"无可展示问题帮助！",autoClose:2000 ,className:"success"});
                    }
                },
                function (error) {
                    alert("无可展示问题帮助！");
                });
        };


        $scope.query=function(){
            $scope.courseType.name = $("input[name='name']").val().trim();
            mainService.getList($scope.courseType).then(function (result) {
                    if(result.success){
                        $scope.courseTypeList=result.data;
                    }else{
                        jbox_notice({content:"无可展示问题帮助！",autoClose:2000 ,className:"success"});
                    }
                },
                function (error) {
                    alert("无可展示问题帮助！");
                });

        };
        $scope.getList();
    }
]);
login.service("mainService", function ($http, $q) {
    return {
        getList: function (courseType) {
            return $http.post('/courseType/getList',courseType).then(function (response) {
                return response.data;
            });
        }
    }
});




