var login = angular.module("wechatOfficialRoom",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.wechatOfficialRoom = {
            freeDate: "",
        }
        mainService.getManagerList().then(function (result) {
            $scope.managerList = result.data;
            console.log(result.data)
        })
        $scope.sub = function(callBack){
            if ($("form1").valid(null,"error!")==false){
                //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
                return false;
            }

            var data = {};
            $scope.wechatOfficialRoom.freeDate =  new Date($("#freeDate").val());
            data=  mainService.doSave($scope.wechatOfficialRoom).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
                });
        }
        $scope.findByIdForEdit = function(){
            mainService.findByIdForEdit(id).then(function(result) {
                    $scope.wechatOfficialRoom= result.data;
                    //$scope.wechatOfficial.freeDate = $filter("date")(result.data.freeDate, "yyyy-MM-dd HH:mm:ss");
                    $("#freeDate").val($filter("date")(result.data.freeDate, "yyyy-MM-dd HH:mm:ss"));
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
        });
        }
        $scope.setDate = function(obj){
            $scope.wechatOfficialRoom.freeDate =  new Date($(obj).val());
        }
        if (id > 0) {
            $scope.findByIdForEdit();
        }
    }
    
]);

login.service("mainService",function($http , $q){
    return {
        doSave : function(wechatOfficialRoom){
            return $http.post('/wechatOfficial/doSaveAndUpdate', wechatOfficialRoom).then(function(response) {
                return response.data;
            });
        },
        getManagerList: function () {
            return $http.get('/wechatOfficial/getManagerList').then(function (response) {
                return response.data;
            });
        } ,
        findByIdForEdit : function(id){
            return $http.get('/wechatOfficial/findByIdForEdit?id=' + id).then(function(response) {
              
                return response.data;
            });
        } ,
    }
});

/**
 * 审核
 * auditStatus为审核状态   1:通过； 2：不通过
 */
function toCheck(auditStatus){
    var remark=$("#remark").val();  //审核意见 
    var managerId =$('#managerId option:selected') .val();//选中的值
    var freeDate = $("#freeDates").val();
    $(".footerBtn button").attr("disabled",true);   //让按钮失去点击事件
    if(remark.trim().length>0){
        $.ajax({
            type: "POST",
            data:{id:id,auditStatus:auditStatus,remark:remark,managerId:managerId,freeDate:freeDate},
            url: "/wechatOfficial/updateAuditStatus",
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

function dataCharge() {
    var totalAmount = $("#totalAmount").val();
    var invalidDate = $("#invalidDate").val();
    // $(".footerBtn button").attr("disabled", true);   //让按钮失去点击事件
    if (totalAmount.trim().length <= 0 || parseInt(totalAmount) <= 0 ) {
        jbox_notice({content: "请输入充值流量", autoClose: 2000, className: "warning"});
        $(".footerBtn button").attr("disabled", false);  //还原点击事件
        return;
    }else if(isNaN(totalAmount)){
        jbox_notice({content: "充值流量需为数字", autoClose: 2000, className: "warning"});
        return;

    }else if (invalidDate.trim().length <= 0 || parseInt(invalidDate) < 0 ) {
        jbox_notice({content: "请输入有效时间", autoClose: 2000, className: "warning"});
        $(".footerBtn button").attr("disabled", false);  //还原点击事件
        return;
    }else if(isNaN(invalidDate)){
    jbox_notice({content: "有效时间需为数字", autoClose: 2000, className: "warning"});
    return;
   }
   else {
        $.ajax({
            type: "POST",
            data: {liveId: liveId, totalAmount: totalAmount, invalidDate: invalidDate},
            url: "/wechatOfficial/dataCharge",
            success: function (obj) {
                $(".footerBtn button").attr("disabled", false);  //还原点击事件
                if (obj.success) {
                    window.parent.closeWindows2(obj); //调用父页面方法关闭窗口
                } else {
                    jbox_notice({content: obj.msg, autoClose: 2000, className: "warning"});
                }
            }
        })
    }
}
function doSave(callback ){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.sub(function(json){
        callback(json);
    });
}
function setDate(obj){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.setDate(obj);
}
