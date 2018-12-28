(function(){//6位密码输入
	var container = document.getElementById("inputBoxContainer");
	$("span[name='close_pwd']").click(function(){
		closePasswordBox();
	});
	$("p[name='forget_pwd']").click(function(){
		forgetPwd();
	});

	boxInput = {
		maxLength:"",
		realInput:"",
		bogusInput:"",
		bogusInputArr:"",
		callback:"",
		init:function(fun){
			var that = this;
			this.callback = fun;
			that.realInput = container.children[0];
			that.bogusInput = container.children[1];
			that.bogusInputArr = that.bogusInput.children;
			that.maxLength = that.bogusInputArr[0].getAttribute("maxlength");
			that.realInput.oninput = function(){
				that.setValue();
			}
			that.realInput.onpropertychange = function(){
				that.setValue();
			}
		},
		setValue:function(){
			this.realInput.value = this.realInput.value.replace(/\D/g,"");
			console.log(this.realInput.value.replace(/\D/g,""))
			var real_str = this.realInput.value;
			for(var i = 0 ; i < this.maxLength ; i++){
				this.bogusInputArr[i].value = real_str[i]?real_str[i]:"";
			}
			if(real_str.length >= this.maxLength){
				this.realInput.value = real_str.substring(0,6);
				setTimeout(this.callback ,100);
			}
		},
		getBoxInputValue:function(){
			var realValue = "";
			for(var i in this.bogusInputArr){
				if(!this.bogusInputArr[i].value){
					break;
				}
				realValue += this.bogusInputArr[i].value;
			}
			return realValue;
		}
	}
})()
/**
 * 开启设置面
 */
function openPasswordBox() {
	$(".bigDealPwdDiv").show();
	$(".bigDealPwdDiv").find(".realInput").attr("autofocus","autofocus");
}

/**
 * 关闭设置密码盒子
 */
function closePasswordBox(){
	$(".bigDealPwdDiv").hide()
	$("#inputBoxContainer input").val("");
}

/**
 * 忘记密码
 */
function forgetPwd(){
	window.location.href ="/weixin/forgetTradePwd.user";
}

