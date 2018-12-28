$(function(){
	var MAX_INT = 2147483647;
		$('#btn_setDay').bind('click',function(){
			var natureDay=$('#natureDay').val();
			layer.prompt({
				  formType: 0,
				  title: '请输入值',
				  value:natureDay
				}, function(value, index, elem){
				  var reg = new RegExp("^[0-9]*$");  
				  if(!reg.test(value)){  
				        return '请输入数字！';
				  }
				if(parseInt(value) > MAX_INT){
					return '输入数字太大！';
				}
				  $.ajax({
						url : "manager/activity/resetNatureDay",
						type : "post",
			 			//async : false,  
						dataType : "json",
						data : {"days":value},
						success : function(data) {
							if (data && data.code==200) {
								layer.msg('设置成功！', {time:2000, icon:1});
								$('#natureDay').val(value);
							} else if(data && data.code==100){
								layer.msg('非法参数！', {time:3000, icon:0});
						    }else{
								layer.msg('设置失败！', {time:3000, icon:0});
							}
						},
						error : function() {
							layer.msg('设置异常！', {time:3000, icon:0});
							return;
						}
					});
				  layer.close(index); 
			});
		});
		$("#btn_batchBase").bind('click',function(){
					layer.prompt({
						  formType: 0,
						  title: '请输入值'
						}, function(value, index, elem){
						  var reg = new RegExp("^[0-9]*$");  
						  if(!reg.test(value)){  
						        return '请输入数字！';
						  }
						if(parseInt(value) > MAX_INT){
							return '输入数字太大！';
						}
						  $.ajax({
							    url : "manager/activity/updateBaseNumById",
								type : "post",
					 			//async : false,  
								dataType : "json",
								data : {"baseNum":value},
								success : function(data) {
									if (data && data.code==200) {
										layer.msg('设置成功！', {time:2000, icon:1}, function () {
											window.location.href=window.location.href;
										});
									} else if(data && data.code==100){
										layer.msg('非法参数！', {time:3000, icon:0});
								    }else{
										layer.msg('设置失败！', {time:3000, icon:0});
									}
								},
								error : function() {
									layer.msg('设置异常！', {time:3000, icon:0});
									return;
								}
							});
						  layer.close(index); 
					});
		});
		$("#btn_batchXishu").bind('click',function(){
					layer.prompt({
						  formType: 0,
						  title: '请输入值'
						}, function(value, index, elem){
						  var reg = new RegExp("^[0-9]*$");  
						  if(!reg.test(value)){  
						        return '请输入数字！';
						  }
						if(parseInt(value) > MAX_INT){
							return '输入数字太大！';
						}
						  $.ajax({
							    url : "manager/activity/updateXiShuById",
								type : "post",
					 			//async : false,  
								dataType : "json",
								data : {"xiShu":value},
								success : function(data) {
									if (data && data.code==200) {
										layer.msg('设置成功！', {time:2000, icon:1}, function () {
											window.location.href=window.location.href;
										});
									} else if(data && data.code==100){
										layer.msg('非法参数！', {time:3000, icon:0});
								    }else{
										layer.msg('设置失败！', {time:3000, icon:0});
									} 
								},
								error : function() {
									layer.msg('设置异常！', {time:3000, icon:0});
									return;
								}
							});
						  layer.close(index); 
					});
		});
		$("#btn_batchRandStart").bind('click',function(){
					layer.prompt({
						  formType: 0,
						  title: '请输入值'
						}, function(value, index, elem){
						  var reg = new RegExp("^[0-9]*$");  
						  if(!reg.test(value)){  
						        return '请输入数字！';
						  }
						if(parseInt(value) > MAX_INT){
							return '输入数字太大！';
						}
						  $.ajax({
							    url: 'manager/activity/updateRandStartById',
								type : "post",
					 			//async : false,  
								dataType : "json",
								data : {"randStart":value},
								success : function(data) {
									if (data && data.code==200) {
										layer.msg('设置成功！', {time:2000, icon:1}, function () {
											window.location.href=window.location.href;
										});
									} else if(data && data.code==100){
										layer.msg('非法参数！', {time:3000, icon:0});
								    }else{
										layer.msg('设置失败！', {time:3000, icon:0});
									}
								},
								error : function() {
									layer.msg('设置异常！', {time:3000, icon:0});
									return;
								}
							});
						  layer.close(index); 
					});
		});
		$("#btn_batchRandEnd").bind('click',function(){
					layer.prompt({
						  formType: 0,
						  title: '请输入值'
						}, function(value, index, elem){
						  var reg = new RegExp("^[0-9]*$");  
						  if(!reg.test(value)){  
						        return '请输入数字！';
						  }
						if(parseInt(value) > MAX_INT){
							return '输入数字太大！';
						}
						  $.ajax({
							  url: 'manager/activity/updateRandEndById',
								type : "post",
					 			//async : false,  
								dataType : "json",
								data : {"randEnd":value},
								success : function(data) {
									if (data && data.code==200) {
										layer.msg('设置成功！', {time:2000, icon:1}, function () {
											window.location.href=window.location.href;
										});
									} else if(data && data.code==100){
										layer.msg('非法参数！', {time:3000, icon:0});
								    }else{
										layer.msg('设置失败！', {time:3000, icon:0});
									}
								},
								error : function() {
									layer.msg('设置异常！', {time:3000, icon:0});
									return;
								}
							});
						  layer.close(index); 
					});
		});
	});


function delLiveRoom(webinarId){
	layer.confirm("确定删除么？", {
		icon: 4,
		title:'提示',
		btn: ['确定','取消'] //按钮
	}, function(index, elem){
		layer.close(index);
		layer.msg('加载中', {icon: 16});
		$.ajax({
			url : "live/delLiveRoom?webinarId="+webinarId,
			type : "post",
			//async : false,
			dataType : "json",
			data : null,
			success : function(data) {
				if (data!="" && data=='1') {
					layer.msg('删除成功！', {time: 2000,icon: 1}, function () {
						window.parent.location.href = window.parent.location.href;
					});
				} else if(data!=""&&data=='0'){
					layer.msg('删除失败！', {time: 3000,icon: 1});
					return;
				}
			},
			error : function() {
					layer.msg('操作异常！', {time: 3000,icon: 0});
					return;
			}
		});
	});
}


function closeSts(webinarId,status){
	
	var msg='';
	if(status==1){
		msg='确定关闭直播间么？';
	}else if(status==0){
		msg='确定开启直播间么？';
	}
	layer.confirm(msg, {
		icon: 4,
		title:'提示',
		btn: ['确定','取消'] //按钮
	}, function(index, elem){
		layer.close(index);
		layer.msg('加载中', {icon: 16});
		$.ajax({
			url : "manager/activity/closeLiveRoom?webinarId="+webinarId+"&status="+status,
			type : "post",
			//async : false,
			dataType : "json",
			data : null,
			success : function(data) {
				if (data!="" && data=='1') {
					var msg = '';
					if(status==0){
						msg = '开启成功！';
					}else if(status==1){
						msg = '关闭成功！';
					}
					layer.msg(msg, {time: 2000,icon: 1}, function () {
						window.parent.location.href = window.parent.location.href;
					});
				} else if(data!=""&&data=='0'){
					if(status==0){
						layer.msg('开启失败！', {time: 3000,icon: 0});
					}else if(status==1){
						layer.msg('关闭失败！', {time: 3000,icon: 0});
					}
					return;
				}
			},
			error : function() {
				layer.msg('操作异常！', {time: 3000,icon: 0});
				return;
			}
		});
	});
}
function changeVersion(webinarId,version){
	var msg='';
	if(version&&version=='online'){//正式直播间转为测试
		msg='确定转为测试直播间么？';
	}else if(version&&version=='offline'){//测试直播间转为正式
		msg='确定转为正式直播间么？';
	}
	layer.confirm(msg, {
		icon: 4,
		title:'提示',
		btn: ['确定','取消'] //按钮
	}, function(index, elem){
		layer.close(index);
		$.ajax({
			url : "manager/activity/updateLiveRoomVersion?webinarId="+webinarId+"&version="+version,
			type : "post",
			//async : false,
			dataType : "json",
			data : null,
			success : function(data) {
				if (data!="" && data=='1') {
					layer.msg('转换成功！', {time: 2000,icon: 1}, function () {
						window.parent.location.href = window.parent.location.href;
					});
				}else{
					layer.msg('转换失败！', {time: 3000,icon: 0});
				}
			},
			error : function() {
				layer.msg('转换异常！', {time: 3000,icon: 0});
				return;
			}
		});
	});
}
function changeFront(webinarId,isFront,type,sts){
	if(sts=='0'){
		layer.msg('关闭直播间无法置顶操作！',{time:3000, icon: 0});
		return;
	}
	if(isFront==0){//置顶操作，判断是否置顶的已经达到4个
		var frontCount='${frontCount}';
		if(frontCount&&parseInt(frontCount)==4){
			layer.msg('最多只能有4个置顶操作！',{time:3000, icon: 0});
			return;
		}
		isFront=1;
	}else{//取消置顶操作
		isFront=0;
	}
	if(type==3||type==4){
		//直播结束的去查4条最近回顾记录
		layer.open({
			  type: 2,
			  title: '回放最近主题',
			  shadeClose: true,
			  shade: 0.8,
			  area: ['380px', '50%'],
			  // offset : ['0px' , '0px'],
			  content: 'manager/activity/historyList?isFront='+isFront+'&webinarId='+webinarId
			}); 
	}else{
		var msg='';
		if(isFront==0){
			msg='确定此条记录取消置顶么？';
		}else{
			msg='确定此条记录置顶么？';
		}
		layer.confirm(msg, {
			icon: 4,
			title:'提示',
			btn: ['确定','取消'] //按钮
		}, function(index, elem){
			layer.msg('加载中', {icon: 16});
			layer.close(index);
			$.ajax({
				url : "manager/activity/updateLiveRoomOrder?webinarId="+webinarId+"&isFront="+isFront,
				type : "post",
				//async : false,
				dataType : "json",
				data : null,
				success : function(data) {
					if (data!="" && data=='1') {
						var msg = '';
						if(isFront==0){
							msg = '取消置顶成功！';
						}else{
							msg = '置顶成功！';
						}
						layer.msg(msg, {time:2000, icon:1}, function () {
							window.parent.location.href = window.parent.location.href;
						})
					} else if(data!=""&&data=='0'){
						layer.msg('置顶失败！', {time: 3000,icon: 0});
						return;
					}else if(data!=""&&data=='2'){
						layer.msg('最多只能有4个置顶操作！', {time: 3000,icon: 0});
					}
				},
				error : function() {
					if(isFront==1){
						layer.msg('置顶异常！', {time: 3000,icon: 0});
					}else{
						layer.msg('取消置顶异常！', {time: 3000,icon: 0});
					}
					return;
				}
			});
		});
	}
	
}

//首页热门
	function hotVideo(webinarId, type, sts, teacherId){
	if(sts=='0'){
		layer.msg('关闭直播间无法置顶操作！',{icon: 0});
		return;
	}
	// 直播结束
	if(type==3||type==4){
		layer.open({
			  type: 2,
			  title: '所有回放视频',
			  shadeClose: true,
			  shade: 0.8,
			  area: ['700px', '50%'],
			  content: 'manager/activity/allHistoryList?webinarId='+webinarId+"&teacherId="+teacherId
		}); 
	// 预约or直播中	
	} else {
		var msg = "确定推荐该直播到首页么？";
		layer.confirm(msg, {
			icon: 4,
			title:'提示',
			btn: ['确定','取消']
		}, function(index, elem){
			layer.close(index);
			layer.msg('加载中', {icon: 16});
			$.ajax({
				url : "manager/recvideo/recommend?webinarId="+webinarId +"&teacherId="+teacherId,
				type : "get",
				dataType : "json",
				data : null,
				success : function(data) {
					if (data.code == 200) {
						layer.msg('视频推荐成功！', {time: 2000,icon: 1}, function () {
							window.parent.location.href = "manager/recvideo/list";
						});
					} else {
						layer.msg('操作异常=>' + data.message, {time: 3000,icon: 0});
					}
				},
				error : function() {
					layer.msg('系统发生异常！', {time: 3000,icon: 0});
					return;
				}
			});
		});
	}
}