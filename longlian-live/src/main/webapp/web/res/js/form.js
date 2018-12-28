//表单验证方法
function inputTest(obj) {
    switch (obj.attr('validate')) {
        case 'mobile': //手机号
            var userZ = /^(13[0-9]{9})|(18[0-9]{9})|(14[0-9]{9})|(17[0-9]{9})|(15[0-9]{9})$/;
            if (!userZ.test($.trim(obj.val()))) {
                $('.errorMessages:visible').html('请输入正确的手机号!');
                return false;
            }
            break;
        case 'password': //密码
            var passwordZ = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$/;
            //密码规则由非空格字符组成的字符串，数字，大写字母，小写字母，特殊字符
            if (!passwordZ.test($.trim(obj.val()))) {
                $('.errorMessages:visible').html('密码格式为8-20位数字、字母或符号的组合!');
                return false;
            }
            break;
        case 'verification': //验证码
            var verificationZ = /^\d{6}$/; //6位的数字
            if (!verificationZ.test($.trim(obj.val()))) {
                $('.errorMessages:visible').html('验证码输入格式错误!');
                return false;
            }
            break;
        case 'wechat': //微信号
            var InvitationZ = /^[0-9a-zA-Z_\-]{6,20}$/; //
            if (!InvitationZ.test($.trim(obj.val()))) {
                $('.errorMessages:visible').html('微信号输入格式错误!');
                return false;
            }
            break;
        case 'money': //金额
            var moneyZ = /^([1-9][\d]{0,3}|0|10000)(\.[\d]{1,2})?$/;
            if (!moneyZ.test($.trim(obj.val())) || Number(obj.val()) > 10000 || Number(obj.val()) < 1) {
                if (Number(obj.val()) !== 0||obj.val() == '') {
                    $('.errorMessages:visible').html('金额为1 ~ 10000!');
                    return false;
                }
            }
            break;
        case 'emoji': //emoji表情
            if (isEmojiCharacter(obj.val())) {
				$('.errorMessages:visible').html('不支持emoji表情输入!');
                return false;
            }
            break;
        case 'fullname': //实名认证
            var fullname = /^[\u4e00-\u9fa5]{2,20}$/ //中文
            if (!fullname.test(obj.val())) {
                $('.errorMessages:visible').html('姓名格式不正确!');
                return false;
            }
            break;
        case 'identity': //身份证号
            var fullname15 = /^[1-9]\d{7}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}$/; //身份证号15日
            var fullname18 = /^[1-9]\d{5}[1-9]\d{3}((0\d)|(1[0-2]))(([0|1|2]\d)|3[0-1])\d{3}([0-9]|X)$/; //身份证号18日
            if (!fullname15.test(obj.val())&&!fullname18.test(obj.val())) {
                $('.errorMessages:visible').html('身份证号格式不正确!');
                return false;
            }
            break;	
		case 'schedule': //排课计划2·100正整数
            var schedule = /(^[1-9][0-9]$)|(^100&)|(^[2-9]$)$/ ; //2·100正整数
            if (!schedule.test($.trim(obj.val()))) {
                $('.errorMessages:visible').html('排课节数只能是2-100正整数!');
                return false;
            }
            break;
    }
    return true;
};

/*表单不能为空*/
function valT(obj, obj2, fileCount) { /*所有input值为真，Button可点击*/
    if (!fileCount) fileCount = 0;
    var num = 0 + fileCount;
    obj.each(function () {
        if ($.trim($(this).val()) && $(this).attr('type') != 'file') num += 1;
    });
    if (num >= obj.length) {
        obj2.removeClass('bgcol_c80');
        obj2.prop('disabled', false);
    } else {
        obj2.addClass('bgcol_c80');
        obj2.prop('disabled', true);
    }
    console.log(fileCount, num);
}

//选择图片，马上预览
function xmTanUploadImg(obj) {
    var file = obj.files[0];
    var img = $(obj).prev('.pic')[0];
    var reader = new FileReader();
    reader.onload = function (e) {
        img.src = e.target.result;
    };
    reader.readAsDataURL(file);
}
//判断是否有emoji表情
function isEmojiCharacter(substring) {
    for (var i = 0; i < substring.length; i++) {
        var hs = substring.charCodeAt(i);
        if (0xd800 <= hs && hs <= 0xdbff) {
            if (substring.length > 1) {
                var ls = substring.charCodeAt(i + 1);
                var uc = ((hs - 0xd800) * 0x400) + (ls - 0xdc00) + 0x10000;
                if (0x1d000 <= uc && uc <= 0x1f77f) {
                    return true;
                }
            }
        } else if (substring.length > 1) {
            var ls = substring.charCodeAt(i + 1);
            if (ls == 0x20e3) {
                return true;
            }
        } else {
            if (0x2100 <= hs && hs <= 0x27ff) {
                return true;
            } else if (0x2B05 <= hs && hs <= 0x2b07) {
                return true;
            } else if (0x2934 <= hs && hs <= 0x2935) {
                return true;
            } else if (0x3297 <= hs && hs <= 0x3299) {
                return true;
            } else if (hs == 0xa9 || hs == 0xae || hs == 0x303d || hs == 0x3030
                || hs == 0x2b55 || hs == 0x2b1c || hs == 0x2b1b
                || hs == 0x2b50) {
                return true;
            }
        }
    }
}
//判断图片地址是否为空
function isEmptyPic(photo) {
    var b = false;
    if (photo == "" || photo == null || photo == undefined || photo == "undefined") {
        b =  true;
    }
    return b;
}
