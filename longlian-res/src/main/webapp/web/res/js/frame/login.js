function sub() {
	$.post("/doLoginIn" , $("#myform").serialize(), function(data) {
		var success = data.success;
		if(success){
			var token = data.data.token;
			location.href = "/main/home";
		}else{
			//验证码过期
			if (data.code == -1) {
				$("#verifCode").val("");
				reA();
			}
			jbox_notice({content: data.msg, autoClose: 2000, className: "error"});
		}
	}, "json");
}

