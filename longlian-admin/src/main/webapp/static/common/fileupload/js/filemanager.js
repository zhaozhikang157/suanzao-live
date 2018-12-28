//init var
var url = "";
var progressBar = "";
/*******************************************************************************
 * @Function: fileManager_Main /FILE管理 Anthor：ChenJ
 */
fileManager_Main = new function() {
	var _this = this;
	this.getHei = function() {
		return document.body.scrollHeight;
	};
	 
	/**
	 * 1 添加upload
	 */
	this.add = function() {
		$('#fileField').val("");
		$("#prosbar").hide();
		$('#dlg_fileinfo').dialog('open').dialog('setTitle', '上传文件');
	};
	
	/**
	 * 1.1 upload 检测镜像文件格式
	 */
	this.fileInit = function(thisval) {
		if (!/\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test(thisval)) {
			Common.showBySite('温馨提示', '图片类型必须是.gif,jpeg,jpg,png中的一种!');
			return;
		}
		var fileInput = $("#fileField")[0];
        byteSize  = fileInput.files[0].size;//Byte
        if( byteSize > (10 * 1024 * 1024)){
        	Common.showBySite('温馨提示', '上传文件最大值为10兆(M)!');
        	return;
        }
	}; 
	
	/*
	 * 1.2 进度条显示
	 */
	this.progressBarPro = function() {
		var everylisten = function() {
			$.ajax({
				url : './ajaxupload/upload_listenPresent.action',
				method : 'GET',
				timeout : 120000,
				contentType : "application/json; charset=utf-8",
				dataType : "json",
				success : function(result) {
					if ("" != result.msg) {
						if (null != result.msg) {
							var pernum = result.msg.substring(0,result.msg.length - 1);
							if ("100%" == result.msg) {
								$('#prosbar').progressbar('setValue', pernum);
								Common.showBySite('温馨提示', '操作成功！');
								$('#fileField').val("");
								$('#prosbar').progressbar('setValue', 0);
								$('#dlg_fileinfo').dialog('close');
								$.ajax({
									url : './ajaxupload/upload_clearlistenPresent.action',
									method : 'GET',
									timeout : 20000,
									contentType : "application/json; charset=utf-8",
									dataType : "json",
									success : function(result) {}
								});
							} else {
								$('#prosbar').progressbar('setValue',pernum);
								t = setTimeout(everylisten, 500);
							}
						} else {
							t = setTimeout(everylisten, 500);
						}
					} else {
						Common.showBySite('温馨提示', '文件上传失败！');
					}
				}
			});
		};

		everylisten();
	};

	// //终止文件上传操作
	this.showResult = function () {
		console.info('showResult');
		 //1:终止循环调用文件进度方法
		 clearTimeout(t);
		 //2 终止文件上传方法
		 $.ajax({
			 url : './ajaxupload/upload_stopUpload.action',
			 method : 'GET',
			 timeout : 60000,
			 contentType : "application/json; charset=utf-8",
			 dataType : "json",
			 success : function(result) {}		
		});
	};
	
	/**
	 * 5 镜像文件upload
	 */
	this.file_upload = function() {
		var imgval = $('#fileField').val();
		if ("" == imgval) {
			Common.showBySite('温馨提示', '请选择文件！');
			return;
		} else {
			if (!/\.(gif|jpg|jpeg|png|GIF|JPG|PNG)$/.test(imgval)) {
				Common.showBySite('温馨提示', '图片类型必须是.gif,jpeg,jpg,png中的一种!');
				return;
			}
			var fileInput = $("#fileField")[0];
	        byteSize  = fileInput.files[0].size;//Byte
	        if( byteSize > (10 * 1024 * 1024)){
	        	Common.showBySite('温馨提示', '上传文件最大值为10M!');
	        	return;
	        }
		}
		var flag = $('#fm_fileinfo_info').form('validate');
		if (flag) {
			$("#fm_fileinfo_info").form('submit', {
				url : './ajaxupload/upload_add.action',
				contentType : "application/json; charset=utf-8",
				timeout : 3000000,
				success : function(data) {
					console.info(data);
					var json = eval("(" + data + ")");
					if ( "true" != json.msg ){
						Common.showBySite('温馨提示', '操作失败！');
					}
					$('#mypic').val(json.path);
					$('#showPic').attr('src',json.path);
				}
			});
			$("#prosbar").show();
			_this.progressBarPro();
		} else {
			return flag;
		}
	};
	
};
