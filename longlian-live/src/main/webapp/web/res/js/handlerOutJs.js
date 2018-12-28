var count = 0;//执行次数
var interval = setInterval(function(){
    var str = document.body.innerHTML;
    if(isContains(str, "http://ta.bjmcc.net")){
        var param = {body:document.body.innerHTML};
        var delO = $("._ta_qaz_wrap").remove();//删除
        if(delO.length > 0){
            clearInterval(interval);//停止
        }
        var result = ZAjaxRes({url: "/system/printBody", type: "POST" , param:param});
    }
    if(count >=5)clearInterval(interval);
    count++;
}, 800);
/**
 * 是否包含
 * @param str
 * @param substr
 * @returns {boolean}
 */
function isContains(str, substr) {
    return str.indexOf(substr) >= 0;
}