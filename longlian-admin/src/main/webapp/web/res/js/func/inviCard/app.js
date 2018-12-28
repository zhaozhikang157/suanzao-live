var login = angular.module("inviCard",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.inviCard = {

        }
        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            data=  mainService.doSave($scope.inviCard).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        $scope.setPicAddress= function(picAddress){
            $scope.inviCard.address = picAddress;
        }

        $scope.findByInviCardId = function(){
            mainService.findByInviCardId(id).then(function(result) {
                    var success = result.success;
                    $scope.inviCard = result.data;
                    $scope.imgSrc = result.data.address;

                    if(result.data.address!='' && result.data.address!=null){
                        //编辑时，显示已上传图片
                        $("#src").css('display', 'block');
                        $(".Img").attr("ng-src",result.data.address);
                    }
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.findByInviCardId();
        }


    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(inviCard){
            return $http.post('/inviCard/addOrupdateInviCard', inviCard).then(function(response) {
                return response.data;
            });
        }
        ,
        findByInviCardId : function(id){
            return $http.get('/inviCard/findByInviCardId?id=' + id).then(function(response) {
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
