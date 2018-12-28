var login = angular.module("inviCode",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        mainService.selectSingles().then(function(result){
            $scope.model = result.data;
        })
        $scope.inviCode = {};
        $scope.courseId = {};
        $scope.courseId.ID = 0;
        $scope.sub = function(callBack){
            if ($("#form1").valid(null,"error!")==false){
                $("#sub").attr('disabled',false);
                return false;
            }
            var data = {};
            if($scope.courseId.ID < 1){
                jbox_notice({content: "请选择课程", autoClose: 2000, className: "error"});
                return;
            }
            if($scope.inviCode.codeCount == '' || $scope.inviCode.codeCount == undefined){
                jbox_notice({content: "请输入邀请码数量!", autoClose: 2000, className: "error"});
                return;
            }
            if($scope.inviCode.codeCount > 1000){
                jbox_notice({content: "单批次邀请码数量不得超过1000!", autoClose: 2000, className: "error"});
                return;
            }
            $("#sub").attr('disabled',true);
            $scope.inviCode.courseId = $scope.courseId.ID;
            mainService.doSave($scope.inviCode).then(function(result) {
                    jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
                    setTimeout(function () {
                        window.location.href = "/inviCode/index.user";
                    }, 2000);
            },
            function(error){
                $("#sub").attr('disabled',false);
                alert("保存失败，请与管理员联系！");
            });
        }

        $scope.selectSingles=function(){
            mainService.selectSingles().then(function(result){
                $scope.model = result.data;
            })
        }

        $scope.selectSeries=function(){
            mainService.selectSeries().then(function(result){
                $scope.model = result.data;
            })
        }

        $scope.setStartDate = function(obj){
            $scope.inviCode.startTime =  new Date($(obj).val());
        }

        $scope.setEndDate = function(obj){
            $scope.inviCode.endTime =  new Date($(obj).val());
        }

    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(inviCode){
            return $http.post('/inviCode/insertInviCode.user', inviCode).then(function(response) {
                return response.data;
            });
        },
        selectSingles : function(){
            return $http.post('/inviCode/getAllSingleClass.user').then(function(response) {
                return response.data;
            });
        },
        selectSeries : function(){
            return $http.post('/inviCode/getAllseriesClass.user').then(function(response) {
                return response.data;
            });
        }
    }
})

function doSave(callback){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.sub(function(json){
        callback(json);
    });
}

function setStartDate(obj){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.setStartDate(obj);
}

function setEndDate(obj){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.setEndDate(obj);
}

