/**
 *支付后台
 * @param payType  支付类型
 * @param password 支付密码
 * @returns {*}
 */
function paying(param) {
    //closePasswordBox();
   // var param = {payType: payType, password: password,amount:100, deviceNo: ""};
    param["deviceNo"]  = "";
    var result = ZAjaxRes({url: "/thirdPay/pay.user", type: "POST", param: param});
    if (result.code == "000000" || result.code == "100008"|| result.code == "100025"  || result.code == "100002"||result.code == "100012") {//支付成功或者已经支付
        return result;
    } else {
        sys_pop(result);
    }
}

/**
 * 充值学币
 * @param payType  支付类型
 * @param password 支付密码
 * @returns {*}
 */
function rechargePay(param) {
    param["deviceNo"]  = "";
    var result = ZAjaxRes({url: "/thirdPay/rechargePay.user", type: "POST", param: param});
    if (result.code == "000000" || result.code == "100008"|| result.code == "100025"  || result.code == "100002") {//支付成功或者已经支付
        return result;
    } else {
        sys_pop(result);
    }
}

/**
 * 第三方取消支付
 */
function cancelThirdPay(param){
    //获取分享信息
    var orderId = "0";
    if(param){
        if(param.orderId)orderId = param.orderId;;
    }
    var result = ZAjaxRes({url:"/thirdPay/cancelThirdPay.user?orderId=" + orderId,type:"GET" });
    if(result.code == "000000"){

    }else{
        alert(result.message);
    }
}

/**
 * 获取用户打赏类型列表
 */
function getUserRewardType(elem_obj){
    var result = ZAjaxRes({url:"/userRewardType/getUserRewardList" });
    if(result.code == "000000"){
        return result.data;
    }else{
        alert(result.message);
    }
}

/**
 * 用户打赏
 * @param userRewardTypeId 用户选择打赏类型Id
 * @param courseId 课程Id
 */
function userReward(userRewardTypeId , courseId){
    var result = ZAjaxRes({url:"/thirdPay/userRewardPay.user", type: "POST",param:{payType:"07" , courseId:courseId , payTypeId:userRewardTypeId,count:"1"} });
    if(result.code == "000000"){
        return  result.data;
    }else{
        //alert( result.message );
    }
}
/**
 * 转播课程
 * @param userRewardTypeId 用户选择打赏类型Id
 * @param courseId 课程Id
 */
function PayByRelpay(param){
    var result = ZAjaxRes({url:"/thirdPay/PayByRelpay.user", type: "POST",param:param });
    if(result.code == "000000" || result.code == "100008"|| result.code == "100025"  || result.code == "100002"||result.code == "100012"||result.code == "10505"||result.code=="10507"||result.code == '000125'){
        return  result;
    }else{
        //alert( result.message );
    }
}