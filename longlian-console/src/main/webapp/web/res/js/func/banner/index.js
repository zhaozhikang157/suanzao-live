var login = angular.module("banner", []);
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
        $scope.banner = {

        };
        $scope.bannerList = [];
        $scope.getList=function(){
            mainService.getList($scope.banner).then(function (result) {
                    if(result.success){
                        $scope.bannerList=result.data;
                    }else{
                        jbox_notice({content:"查询失败，请找管理员！",autoClose:2000 ,className:"success"});
                    }
                },
                function (error) {
                    alert("查询失败，请找管理员！");
                });
        };


        $scope.query=function(){
            $scope.banner.name = $("input[name='name']").val().trim();
            var logTimeBeginStr = $("#logTimeBeginStr").val();
            if (logTimeBeginStr != "") {
                $scope.banner.timeBegin = $("#logTimeBeginStr").val() + " 00:00:00";  //起始时间
            }
            var logTimeEndStr = $("#logTimeEndStr").val();
            if (logTimeEndStr != "") {
                $scope.banner.timeEnd = $("#logTimeEndStr").val() + " 23:59:59";    //结束时间
            }
            mainService.getList($scope.banner).then(function (result) {
                    if(result.success){
                        $scope.bannerList=result.data;
                    }else{
                        jbox_notice({content:"查询失败，请找管理员！",autoClose:2000 ,className:"success"});
                    }
                },
                function (error) {
                    alert("查询失败，请找管理员！");
                });

        };


        $scope.getList();
    }
]);
login.service("mainService", function ($http, $q) {
    return {
        getList: function (banner) {
            return $http.post('/banner/getList',banner).then(function (response) {
                return response.data;
            });
        }
    }
});




