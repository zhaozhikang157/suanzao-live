var login = angular.module("orders",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.orders = {
        };
        $scope.getOrdersById = function(){
            mainService.getOrdersById(id).then(function(result) {
                    var success = result.success;
                    $scope.orders = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        };
        if(id > 0){
            $scope.getOrdersById();
        }
    }
]);

login.service("mainService",function($http , $q){
    return {
        getOrdersById : function(id){
            return $http.get('/ordersController/selectBankOutById?id=' + id).then(function(response) {
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
    var auditAgreed=$("#auditAgreed").val();  //审核意见
    var orderNo=$("#orderNo").text();  //订单编号
    $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
    if(auditAgreed.trim().length>0){
        $.ajax({
            type: "POST",
            data:{id:id,auditStatus:auditStatus,auditAgreed:auditAgreed,orderNo:orderNo},
            url: "/ordersController/updateAuditStatus",
            success: function (obj) {
                $(".footerBtn button").attr("disabled",false);  //还原点击事件
                if(obj.success){
                    window.parent.closeWindow(obj); //调用父页面方法关闭窗口
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