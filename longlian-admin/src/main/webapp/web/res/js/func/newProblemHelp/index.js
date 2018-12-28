var login = angular.module("newProblemHelp", []);
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
        $scope.newProblemHelp = {

        };
        $scope.newProblemHelpList = [];
        $scope.getList=function(){
            mainService.getList($scope.newProblemHelp).then(function (result) {
                    if(result.success){
                        $scope.newProblemHelpList=result.data;
                    }else{
                        jbox_notice({content:"无可展示问题帮助！",autoClose:2000 ,className:"success"});
                    }
                },
                function (error) {
                    alert("无可展示问题帮助！");
                });
        };


        $scope.query=function(){
            $scope.newProblemHelp.title = $("input[name='title']").val().trim();
            var logTimeBeginStr = $("#logTimeBeginStr").val();
            if (logTimeBeginStr != "") {
                $scope.newProblemHelp.timeBegin = $("#logTimeBeginStr").val() + " 00:00:00";  //起始时间
            }
            var logTimeEndStr = $("#logTimeEndStr").val();
            if (logTimeEndStr != "") {
                $scope.newProblemHelp.timeEnd = $("#logTimeEndStr").val() + " 23:59:59";    //结束时间
            }
            mainService.getList($scope.newProblemHelp).then(function (result) {
                    if(result.success){
                        $scope.newProblemHelpList=result.data;
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
        getList: function (newProblemHelp) {
            return $http.post('/newProblemHelp/getList',newProblemHelp).then(function (response) {
                return response.data;
            });
        }
    }
});




