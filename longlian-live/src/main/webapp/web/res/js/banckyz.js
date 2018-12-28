/*personResetName*/
function CloseBtnIsShowFunc(that) {
    var Val = $.trim($(that).val());
    if (Val == '' || Val == null) {
        $(that).next().hide();
    } else {
        $(that).next().show();
    }
}
function processSpelChar(s) {
    var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]")
    var rs = "";
    for (var i = 0; i < s.length; i++) {
        rs = rs + s.substr(i, 1).replace(pattern, "");
    }
    return rs;
}
function CloseBtnFunc(that) {
    $(".per_ResetName").val('')
    $(that).hide();
}
function formatBankNo(BankNo) {//银行卡号格式化
    if (BankNo.value == "") return;
    var account = new String(BankNo.value);
    account = account.substring(0, 33);
    /*帐号的总数, 包括空格在内 */
    if (account.match(".[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{7}") == null) {
        /* 对照格式 */
        if (account.match(".[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{7}|" + ".[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{7}|" +
                ".[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{7}|" + ".[0-9]{4}-[0-9]{4}-[0-9]{4}-[0-9]{7}") == null) {
            var accountNumeric = accountChar = "", i;
            for (i = 0; i < account.length; i++) {
                accountChar = account.substr(i, 1);
                if (!isNaN(accountChar) && (accountChar != " ")) accountNumeric = accountNumeric + accountChar;
            }
            account = "";
            for (i = 0; i < accountNumeric.length; i++) {    /* 可将以下空格改为- */
                if (i == 4) account = account + " ";
                /* 帐号第四位数后加空格 */
                if (i == 8) account = account + " ";
                /* 帐号第八位数后加空格 */
                if (i == 12) account = account + " ";
                /* 帐号第十二位后数后加空格 */
                if (i == 16) account = account + " ";
                /* 帐号第十二位后数后加空格 */
                account = account + accountNumeric.substr(i, 1)
            }
        }
    }
    else {
        account = " " + account.substring(1, 5) + " " + account.substring(6, 10) + " " + account.substring(14, 18) + "-" + account.substring(18, 25);
    }
    if (account != BankNo.value) BankNo.value = account;
}
function checkIdCard(str, that) {//身份证号验证
    var strText = document.getElementById(str).value;
    if (strText == "" || strText == null) {
        that.next().find("span").text('该项不能为空');
        return;
    } else if (!(/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(strText))) {
        that.next().find("span").text('请填写18位身份证号');
        return;
    } else {
        that.next().find("span").text('');
    }
}
function showMoneyIconFunc(that) {
    $(that).next().show();
}

var noticeMsgBox = '';
noticeMsgBox += '<div class="noticeMsgBox" style="display: none;"></div>';
$("body").append(noticeMsgBox);
function noticeMsgFunc(tex) {
    $(".noticeMsgBox").text(tex);
    $(".noticeMsgBox").show();
    setTimeout(noticeMsgHideFunc, 2000);
}
function noticeMsgHideFunc() {
    $(".noticeMsgBox").hide();
}

$(".dealPwdNum2").on("blur", function () {
    againPwdFunc_deal($(this))
})
function againPwdFunc_deal(that) {/*交易密码*/
    var onePwd = $.trim($(".dealPwdNum1").val());
    var againPwd = $.trim($(that).val());
    var authCodeLength = onePwd.length;
    if (authCodeLength < 6) {
        $(that).val("");
        $(".dealPwdNum1").val("");
        noticeMsgFunc("请输入6位密码!");
        return;
    }
    if (againPwd == onePwd) {
        alert("密码一致!")
    } else {
        noticeMsgFunc("输入密码不一致,请重新输入!");
        $(that).val("");
        $(".dealPwdNum1").val("");
    }
}