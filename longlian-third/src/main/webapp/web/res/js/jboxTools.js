/**
 * 消息弹出框-提醒后 会自动消失
 *
 * @param option
 */
function jbox_notice(option){
    if(option  && option.className){
        var className = option.className;
        option.addClass = "jBox-Notice-" + className;
    }
    new jBox('Notice',option);
}

/**
 * 消息弹出框-提醒后 不会自动关闭
 * @param option
 */
function jbox_message(option){
    var div = $("<div/>");
    var button = $("<button type='button' class='btn btn-info' > 确定</button>");
    div.append(button);
    option["footer"] = true;
    option["footerContent"] = div;
    var jboxObj =  new jBox('Modal',option).open();
    $(".jBox-container").css("opacity","1");
    button.click(function(){
        jboxObj.close();
    });
    button.click(function(){
        if(option.confrimFunc && typeof(eval(option.confrimFunc)) == "function"){
            option.confrimFunc(jboxObj);
        }
    });
}

/**
 * 模拟原js confirm 功能
 * @param option
 */
function jbox_confirm(option){
    var div = $("<div/>");
    var button = $("<button type='button' class='btn btn-info' > 确定</button>");
    var button3 = $("<button type='button' class='btn' > 取消</button>");
    div.append(button).append(button3);
    div.append(button).append(button3);
    option["footer"] = true;
    option["footerContent"] = div;
    option["title"] = "温馨提示";
    option["draggable"] = "title";
    option["closeButton"] = "box";
    option["animation"] = {
        open: 'slide:left',
        close: 'slide:right'
    };
    var jboxObj =  new jBox('Modal',option).open();
    button3.click(function(){
        jboxObj.close();
    });
    $(".jBox-container").css("opacity","1");
    button.click(function(){
        if(option.confrimFunc && typeof(eval(option.confrimFunc)) == "function"){
            option.confrimFunc(jboxObj);
        }
    });
    return jboxObj;
}

