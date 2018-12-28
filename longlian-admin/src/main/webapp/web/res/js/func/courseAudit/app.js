var login = angular.module("course",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.course = {
        };
        $scope.selectAuditCourseById = function(){
            mainService.selectAuditCourseById(id).then(function(result) {
                    var success = result.success;
                    $scope.course = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        };
        if(id > 0){
            //$scope.selectAuditCourseById();
        }
    }
]);

login.service("mainService",function($http , $q){
    return {
        selectAuditCourseById : function(id){
            return $http.get('/courseAudit/selectAuditCourseById?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
});

/**
 * 审核
 * auditStatus为审核状态   1:通过； 2：不通过
 */
function toCheck(auditStatus){
    var remark=$("#remark").val();  //审核意见
    $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
    if(remark.trim().length>0){
        $.ajax({
            type: "POST",
            data:{id:id,isGarbage:auditStatus,remark:remark},
            url: "/courseAudit/updateAuditStatus",
            success: function (obj) {
                $(".footerBtn button").attr("disabled",false);  //还原点击事件
                if(obj.success){
                    window.parent.closeWindows(obj); //调用父页面方法关闭窗口
                }else{
                    jbox_notice({content:obj.msg,autoClose:2000 ,className:"warning"});
                }
            }
        })
    }else{
        jbox_notice({content:"请输入审核内容",autoClose:2000 ,className:"warning"});
        $(".footerBtn button").attr("disabled",false);  //还原点击事件
    }

}