var login = angular.module("appUserComment",[]);
var config = [ "$httpProvider", function ($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
}];
login.config(config);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.getCommentById = function(){
            mainService.getCommentById(id).then(function(result) {
                    var success = result.success;
                    $("#remarks0").text(result.data.remarks0)
                    $("#remarks1").text(result.data.remarks1)
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        };
        if(id > 0){
            $scope.getCommentById();
        }
    }
]);

login.service("mainService",function($http , $q){
    return {
        getCommentById : function(id){
            return $http.get('/appUserCommentController/getCommentByCommentId?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
});

