(function(){
	$(document).ready(function(){
		var i=1,
			wait=60;
		function time(obj) {
				if (wait == 0) {
					obj.removeAttribute("disabled");
					obj.className = "downNum";
					obj.value="获取验证码";
					wait = 60;
				} else {
					obj.setAttribute("disabled", true);
					obj.value="" + wait + "s后重新获取";
					obj.className = "downNum on";
					wait--;
					setTimeout(function() {
						time(obj)
					},1000)
				}


	    }
		document.getElementById("downNum").onclick=function(){
			var tex = $("input[name='telNum']").val(),
				texLength = tex.length;
			if( isNaN(tex) ||tex=="" ||texLength<11){
				$(".telPhoneNum input").next().html("*请输入正确的手机号码");
				return;
			}else {
				$.ajax({
					type: "POST",
					url: "/getRegisterSms",
					data: "mobile="+tex,
					async: false,
					success: function(msg){
						if(msg.code=="000004"){
							$(".telPhoneNum input").next().html("*请输入手机号码");
						}
						if(msg.code=="000006"){
							$(".telPhoneNum input").next().html("*请输入正确的手机号码");
						}
						if(msg.code=="000000"){
							$(".telPhoneNum input").next().html("");

						}
					}
				});
				time(this);
			}
		}
	
		
		//勾选并同意协议
		$(".checkAgreeBtn").find("img").show();
		$(".checkAgreeBtn").tap(function(){
			if(i==1){
				$(this).find("img").hide();
				i++;
			}else if(i==2){
				$(this).find("img").show();
				i--;
			}
		})
		function checkBlurFunc(that,obj){

			var tex = $.trim(that.val());

			if( obj == 'telNum' ){
				if( isNaN(tex) ){
					that.next().html("*请输入正确的手机号码");
				}else{
					that.next().html("");
					$.ajax({
						type: "POST",
						url: "/checkUnique",
						data: "mobile="+tex,
						async: false,
						success: function(msg){
							if(msg.code=="000005"){
								that.next().html("*该用户已存在,请前往龙链369APP直接登录!");
								$("input[name='telNum']").val("");
								return
							}
						}
					});
				}
			}
		}
		$(".telPhoneNum input").on("blur",function(){
			var obj = $(this).attr("name");
			checkBlurFunc($(this),obj)
		})
		function checkBlurFunc2(that) {
			var authCode = $.trim($(that).val());
			var authCodeLength = authCode.length;
			if (authCodeLength < 6) {
				$(that).val("");
				noticeMsgFunc("请输入6-16位密码!");
				return;
			}
		}
		var noticeMsgBox = '';
		noticeMsgBox += '<div class="noticeMsgBox" style="display: none;"></div>';
		$("body").append(noticeMsgBox);
		function noticeMsgFunc(tex){
			$(".noticeMsgBox").text(tex);
			$(".noticeMsgBox").show();
			setTimeout(noticeMsgHideFunc,2000);
		}
		function noticeMsgHideFunc(){
			$(".noticeMsgBox").hide();
		}
		var timerss = null;
		$(".pwdNums").on("blur",function(){
			checkBlurFunc2($(this))
		})
		$(".loginBtn").tap(function(){//点击注册并判断不为空


			var telPhoneNumVal = $(".telPhoneNum input").val(),
				pwNumVal = $(".pwNum input").val(),
				authCodeVal = $(".authCode input").val();
			if(
				telPhoneNumVal == "" || telPhoneNumVal == null ||
				pwNumVal == "" || pwNumVal == null ||
				authCodeVal == "" || authCodeVal == null
			){
				noticeMsgFunc("所填不能为空")
			}else{
				if(i==2){
					noticeMsgFunc("请阅读协议并勾选");
					return false;
				}
				var codem = $("#codem").text();
				$.ajax({
					type: "POST",
					url: "/registerH5",
					data: "mobile="+telPhoneNumVal+"&password="+pwNumVal+"&code="+authCodeVal+"&inviteCode="+codem+ "&machineType=" + machineType,
					async: false,
					success: function(msg){
						if(msg.code=="000000"){
							window.location.href = "/goto/register/registeredPage";
						}
						if(msg.code=="000008"){
							noticeMsgFunc("验证码输入错误");
						}
						if(msg.code=="000096"){
							noticeMsgFunc("验证码超时");
						}
						if(msg.code=="000006"){
							noticeMsgFunc("不是一个手机号");
						}
						if(msg.code=="000007"){
							noticeMsgFunc("网络原因注册失败");
						}
						if(msg.code=="000004"){
							noticeMsgFunc("输入信息不完整");
						}
						if(msg.code=="000017"){
							noticeMsgFunc(msg.message);
						}

					}
				});
			}
		})
		
	})
})(this.jQuery);
