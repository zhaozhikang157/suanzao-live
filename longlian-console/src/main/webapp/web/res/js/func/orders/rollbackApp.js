var login = angular.module("orders",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.orders = {

        }

        $scope.getOrdersById = function(){
            mainService.getOrdersById(id).then(function(result) {
                    var success = result.success;
                    $scope.orders = result.data;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        if(id > 0){
            $scope.getOrdersById();
        }


    }
])

login.service("mainService",function($http , $q){
    return {
        getOrdersById : function(id){
            return $http.get('/ordersController/selectBankOutById?id=' + id).then(function(response) {
                return response.data;
            });
        }
    }
})

/**
 * 返钱 通过
 */
function pass(){
        var amount=$("#amount").text();  //提现金额
        var appId=$("#appId").text();  //用户id
        $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
        $.ajax({
            type: "POST",
            data:{id:id,amount:amount,appId:appId},
            url: "/ordersController/rollback",
            success: function (obj) {
                $(".footerBtn button").attr("disabled",false);  //还原点击事件
                if(obj.success){
                    window.parent.closeWindow(obj); //调用父页面方法关闭窗口
                }else{
                    jbox_notice({content:obj.msg,autoClose:2000 ,className:"warning"});
                }
            }
        })
}
