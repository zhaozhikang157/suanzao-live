function sub() {
    //loading层
    var index = layer.load(1, {
        shade: [0.1,'#fff'] //0.1透明度的白色背景
    });
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
        layer.close(index);
	}, "json");
}

