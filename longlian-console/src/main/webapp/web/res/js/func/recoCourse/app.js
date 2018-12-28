var login = angular.module("recoCourse",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.recoCourse = {
        }
        mainService.getPosList().then(function (result) {
            $scope.posList = result.data;
        })


        $scope.sub = function(callBack){
            if ($("form").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }
            var courseId = $("#courseId").val();
            if(isNaN(courseId)){
                jbox_notice({content: "课程id必须为数字", autoClose: 2000, className: "warning"});
                return;
            }
            var sort = $("#sort").val();
            if(isNaN(sort)){
                jbox_notice({content: "排序必须为数字", autoClose: 2000, className: "warning"});
                return;
            }
            var data = {};
            data=  mainService.doSave($scope.recoCourse).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    //alert("保存失败，请与管理员联系！");
                });
        }

        $scope.getId = function(){
             mainService.getId(id).then(function(result) {
                var success = result.success;
                 $scope.recoCourse = result.data;
            },
            function(error){
                //alert("保存失败，请与管理员联系！");
            });
        }
        if(id > 0){
            $scope.getId();
        }
    }
])

login.service("mainService",function($http , $q){
    return {
        doSave : function(recoCourse){
            return $http.post('/recoCourse/saveRecoCourse', recoCourse).then(function(response) {
                return response.data;
            });
        },
        getId : function(id){
            return $http.post('/recoCourse/findById?id=' + id).then(function(response) {
                return response.data;
            });
        },
       getPosList : function(id){
        return $http.get('/recoCourse/posList').then(function(response) {
            return response.data;
        });
    }
    }
})

function doSave(callback ){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.sub(function(json){
        callback(json);
    });
}

function toCheck(){

    var courseId=$("#courseId").val();
    var recoPos =$('#recoPos option:selected') .val();//选中的值
    if(recoPos == ""){
        alert("请选择推荐位置！")
        return;
    }
    var sort = $("#sort").val();
    if(sort == ""){
        alert("请填写排序！")
        return;
    }
    $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
        $.ajax({
            type: "POST",
            contentType: "application/json",
            data:JSON.stringify({courseId:courseId,recoPos:recoPos,sort:sort}),
            url: "/recoCourse/saveRecoCourse",
            success: function (obj) {
                $(".footerBtn button").attr("disabled",false);  //还原点击事件
                if(obj.success){
                    window.parent.closeWindows(obj); //调用父页面方法关闭窗口
                }else{
                    jbox_notice({content:obj.msg,autoClose:2000 ,className:"warning"});
                }
            }
        })
}
