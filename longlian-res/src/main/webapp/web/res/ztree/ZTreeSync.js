
var log, className = "dark";
var ZTreeObj ;

/**
 * zTreeId : ZTree存放容器Id
 * requestURL :request 请求地址
 * param : 求提交的参数键值
 * onRightClickFunc:用于捕获 zTree 上鼠标右键点击之后的事件回调函数
 * onClickFunc: 用于捕获Ztree点击事件回调函数
 * callBack : 用于捕获异步加载的事件回调函数 ,一般就不建议使用
 * checkController :设置 zTree 的节点上是否显示 ,看ZTreeAPI
 * dataFilter :用于对 Ajax 返回数据进行处理的函数
 * isSelectCheckedParent:点击父级节点（checkbox）是否需要
 * isSelectParent : 点击父级节点是否需要
 * async : true - 异步加载  false-一次加载
 * onCheckFunc: 用户勾选事件函数
 * onAsyncSuccessFunc:异步加载成功后回调函数
 */
var ZTreeTool  = {
	zTreeId:'' ,
	param: {},
	callBack:{},
	checkController:{},
	dataFilter:null,
	onRightClickFunc:null,
	onClickFunc:null,
	isSelectParent:"",
	isSelectCheckedParent:"",
	callBack:{},
	onCheckFunc:null,
	config : function (parameters){
		ZTreeTool.zTreeId = parameters.zTreeId || "treeID";
		ZTreeTool.requestURL = parameters.requestURL;
		ZTreeTool.param = parameters.param ? parameters.param : {};
		ZTreeTool.onRightClickFunc = parameters.onRightClickFunc;
		ZTreeTool.onClickFunc = parameters.onClickFunc;
		ZTreeTool.beforeClickFunc = parameters.beforeClickFunc;
		ZTreeTool.zTreeBeforeRename = parameters.zTreeBeforeRename;
		ZTreeTool.onCheckFunc = parameters.onCheckFunc;
		ZTreeTool.onAsyncSuccessFunc = parameters.onAsyncSuccess;
		ZTreeTool.zTreeOnRename = parameters.zTreeOnRename;
		ZTreeTool.zTreeOnDblClick = parameters.zTreeOnDblClick;
		ZTreeTool.callBack = parameters.callBack ||
			{
				beforeAsync:ZTreeTool.beforeAsync,
				beforeClick: ZTreeTool.beforeClickFunc || ZTreeTool.beforeClick,
				beforeCheck: ZTreeTool.zTreeBeforeCheck,
				beforeRightClick:ZTreeTool.beforeRightClick,
				beforeRename: ZTreeTool.zTreeBeforeRename || ZTreeTool.beforeRename ,
				onAsyncError: ZTreeTool.onAsyncError,
				onAsyncSuccess : ZTreeTool.onAsyncSuccessFunc || ZTreeTool.onAsyncSuccess,
				onClick : ZTreeTool.onClickFunc || ZTreeTool.onClick,
				onCheck:  ZTreeTool.onCheckFunc || ZTreeTool.zTreeOnCheck,
				onNodeCreated: ZTreeTool.zTreeOnNodeCreated,
				onRemove: ZTreeTool.zTreeOnRemove,
				onRename: ZTreeTool.zTreeOnRename || ZTreeTool.onRename,
				onDblClick: ZTreeTool.zTreeOnDblClick || ZTreeTool.onDblClick,
				onRightClick: ZTreeTool.onRightClickFunc || null
			};
		ZTreeTool.checkController = parameters.checkController ? parameters.checkController :
		{
			enable : false,
			isSelectParentNode:true //默认获取是父级节点

		};

		ZTreeTool.dataFilter = parameters.dataFilter ? parameters.dataFilter : ZTreeTool.filter ;
		ZTreeTool.isSelectParent = parameters.isSelecctParent ? parameters.isSelecctParent : "";
		ZTreeTool.isSelectCheckedParent = parameters.isSelectCheckedParent ? parameters.isSelectCheckedParent : "";

		if(parameters.async== false){//同步
			if(parameters.asyncExtend == true){//同步扩展
				ZTreeObj =  ZTreeTool.createExtendZTree();
			}else{
				ZTreeObj =  ZTreeTool.createZTree();
			}


		}else{//异步
			ZTreeObj =  ZTreeTool.ZTreeAsync();
		}

		return ZTreeObj;
	},
	/**  异步加载数
	 *
	 * @param idStr : 树存放容器Id
	 * @param requestURLStr :URL路径
	 * @param param  : 传值参数
	 * @param callback : 返回方法参数
	 * @param dataFilter : 回数据进行处理的函数
	 * @param checkController : 是否显示checkBox/radio 参数设置
	 * @param otherParmSetting : 其它参数设置
	 * @param OnRightClick : 设置右击捕获函数
	 *
	 */
	ZTreeAsync : function  () {
		setting = {
			check : ZTreeTool.checkController,
			view : {
				selectedMulti : false
			},
			async : {
				enable : true,
				url : ZTreeTool.requestURL,
				autoParam : [ "id", "name=n" ],
				otherParam : ZTreeTool.param,
				dataFilter : ZTreeTool.dataFilter
			},

			callback : ZTreeTool.callBack,


			data : {
				key: {
					title:  "title"
				}
			}
		};
		$.fn.zTree.init($("#" + ZTreeTool.zTreeId), setting);
		$("#refreshNode").bind("click", {
			type : "refresh",
			silent : false
		}, ZTreeTool.refreshNode);
		$("#refreshNodeSilent").bind("click", {
			type : "refresh",
			silent : true
		}, ZTreeTool.refreshNode);
		$("#addNode").bind("click", {
			type : "add",
			silent : false
		}, ZTreeTool.refreshNode);
		$("#addNodeSilent").bind("click", {
			type : "add",
			silent : true
		}, ZTreeTool.refreshNode);
		$("#checkTrue").bind("click", {type:"checkTrue"},ZTreeTool.checkNode);
		$("#checkFalse").bind("click", {type:"checkFalse"}, ZTreeTool.checkNode);
		$("#toggle").bind("click", {type:"toggle"}, ZTreeTool.checkNode);
		$("#checkTruePS").bind("click", {type:"checkTruePS"}, ZTreeTool.checkNode);
		$("#checkFalsePS").bind("click", {type:"checkFalsePS"}, ZTreeTool.checkNode);
		$("#togglePS").bind("click", {type:"togglePS"}, ZTreeTool.checkNode);
		$("#checkAllTrue").bind("click", {type:"checkAllTrue"}, ZTreeTool.checkNode);
		$("#checkAllFalse").bind("click", {type:"checkAllFalse"}, ZTreeTool.checkNode);
		ZTreeObj = $.fn.zTree.getZTreeObj( ZTreeTool.zTreeId);
		return ZTreeObj;
	},
	/* showTitleForTree :function(treeId, treeNode) {
	 if(treeNode.remark){
	 return treeNode.remark;
	 }
	 return treeNode.name;
	 },*/

	/**
	 * 同步加载
	 */
	createZTree : function  () {
		setting = {
			check : ZTreeTool.checkController,
			view : {
				selectedMulti : false
			},
			data: {
				simpleData: {
					enable: true
				},
				key: {
					title:  "title"
				}
			},
			callback : ZTreeTool.callBack

		};
		var zNodes;
		$.ajax({
			url: ZTreeTool.requestURL, //url  action是方法的名称
			data: ZTreeTool.param ,
			type: 'POST',
			dataType: "json", //可以是text，如果用text，返回的结果为字符串；如果需要json格式的，可是设置为json
			ContentType: "application/json; charset=utf-8",
			success: function(dataJson) {
				if(dataJson.success){
					var zNodes = dataJson.data;
					var rtMsg = dataJson.msg;//扩展其它属性
					$.fn.zTree.init($("#" + ZTreeTool.zTreeId), setting,zNodes );
					if( ZTreeTool.onAsyncSuccessFunc ){
						ZTreeTool.onAsyncSuccessFunc(zNodes.length , dataJson , rtMsg);
					}
				}else{
					alert(dataJson.msg);
				}

			},
			error: function(msg) {
				alert("查询出错");
			}
		});


	},

	/*   同步扩展*/
	createExtendZTree : function  () {
		setting = {
			check : ZTreeTool.checkController,
			view : {
				selectedMulti : false
			},
			data: {
				simpleData: {
					enable: true
				},
				key: {
					title:  "title"
				}
			},
			callback : ZTreeTool.callBack

		};


		var zNodes;
		$.ajax({
			url: ZTreeTool.requestURL, //url  action是方法的名称
			data: ZTreeTool.param ,
			type: 'POST',
			dataType: "json", //可以是text，如果用text，返回的结果为字符串；如果需要json格式的，可是设置为json
			ContentType: "application/json; charset=utf-8",
			success: function(dataJson) {
				if(dataJson.success){
					var zNodes = dataJson.data;
					var rtMsg = dataJson.msg;//扩展其它属性
					$.fn.zTree.init($("#" + ZTreeTool.zTreeId), setting,zNodes );
					if( ZTreeTool.onAsyncSuccessFunc ){
						ZTreeTool.onAsyncSuccessFunc(zNodes.length , dataJson);
					}
				}else{
					alert(dataJson.msg);
				}

			},
			error: function(msg) {
				alert("查询出错");
			}
		});

	}

	,
	/**
	 * 点击是否异步请求
	 * @param treeId
	 * @param treeNode
	 * @returns {Boolean}
	 */
	beforeAsync : function  (treeId, treeNode) {
		ZTreeTool.className = (ZTreeTool.className === "dark" ? "" : "dark");
		ZTreeTool.showLog("[ " + ZTreeTool.getTime()
			+ " beforeAsync ]&nbsp;&nbsp;&nbsp;&nbsp;"
			+ ((!!treeNode && !!treeNode.name) ? treeNode.name : "root"));

		//return true;
	},
	/**
	 *  Ajax 返回数据进行预处理的函数
	 * @param treeId : 节点Id
	 * @param parentNode  ： 节点
	 * @param childNodes ： 下级下级节点数组
	 * @returns
	 */
	filter : function  (treeId, parentNode, childNodes) {
		if(childNodes.rtState){
			childNodes = childNodes.rtData;
			if (!childNodes)
				return null;
			for ( var i = 0, l = childNodes.length; i < l; i++) {
				var childNodesName = childNodes[i].name;
				if(!childNodesName){
					childNodesName = "";
				}
				childNodes[i].name = childNodesName.replace(/\.n/g, '.');
			}
		}else{
			alert(childNodes.rtMsg);
			return "";
		}

		return childNodes;
	},

	/**
	 * 点击前实行函数
	 * @param treeId
	 * @param treeNode
	 * @returns {Boolean}
	 */
	beforeClick : function  (treeId, treeNode) {

		/*if (!treeNode.isParent) {
		 //alert("请选择父节点");
		 return false;

		 } else {
		 return true;
		 }*/
		return true;
	},
	beforeRename:function (treeId, treeNode, newName, isCancel) {
		return true
	},

	onClick : function  (event, treeId, treeNode) {
		//alert("执行了onClick事件！");
		if(ZTreeTool.isSelectParent == false && treeNode.isParent ){//如果是不是叶子节点且不是叶子节点
			//alert(treeNode.name +":"+ treeNode.isParent);
		}else{
			//alert(treeNode.name +":"+ treeNode.isParent);
		}
	},

	zTreeBeforeCheck : function (event, treeId, treeNode) {

		return true;
	},
	zTreeOnCheck : function  (event, treeId, treeNode) {
		//alert("勾选调用原始事件函数");
	},


	/**
	 * 新增节点捕获事件
	 * @param event
	 * @param treeId
	 * @param treeNode
	 */
	zTreeOnNodeCreated : function(event, treeId, treeNode){
		// alert(treeNode.tId + ", " + treeNode.name);
	},

	zTreeOnRemove : function  (event, treeId, treeNode) {
		//alert(treeNode.tId + ", " + treeNode.name);
	},

	onRename : function  (event, treeId, treeNode, isCancel) {
		//alert(treeNode.tId + ", " + treeNode.name);
	},

	onDblClick :function (event, treeId, treeNode) {
		//alert(treeNode ? treeNode.tId + ", " + treeNode.name : "isRoot");
	},


	refreshNode : function  (e) {
		alert("执行refreshNode方法!");
		var zTree = ZTreeObj, type = e.data.type, silent = e.data.silent, nodes = zTree.getSelectedNodes();

		if (nodes.length == 0) {
			alert("请先选择一个父节点");
		}
		for ( var i = 0, l = nodes.length; i < l; i++) {
			zTree.reAsyncChildNodes(nodes[i], type, silent);
			if (!silent)
				zTree.selectNode(nodes[i]);
		}
	},
	checkNode : function  (e) {
		var zTree = ZTreeObj,
			type = e.data.type,
			nodes = zTree.getSelectedNodes();
		if (type.indexOf("All")<0 && nodes.length == 0) {
			alert("请先选择一个节点");
		}
		if (type == "checkAllTrue") {
			zTree.checkAllNodes(true);
		} else if (type == "checkAllFalse") {
			zTree.checkAllNodes(false);
		} else {
			var callbackFlag = $("#callbackTrigger").attr("checked");
			for (var i=0, l=nodes.length; i<l; i++) {
				if (type == "checkTrue") {
					zTree.checkNode(nodes[i], true, false, callbackFlag);
				} else if (type == "checkFalse") {
					zTree.checkNode(nodes[i], false, false, callbackFlag);
				} else if (type == "toggle") {
					zTree.checkNode(nodes[i], null, false, callbackFlag);
				}else if (type == "checkTruePS") {
					zTree.checkNode(nodes[i], true, true, callbackFlag);
				} else if (type == "checkFalsePS") {
					zTree.checkNode(nodes[i], false, true, callbackFlag);
				} else if (type == "togglePS") {
					zTree.checkNode(nodes[i], null, true, callbackFlag);
				}
			}
		}
	},

	getTime : function   () {
		var now = new Date(),
			h = now.getHours(),
			m = now.getMinutes(),
			s = now.getSeconds(),
			ms = now.getMilliseconds();
		return (h + ":" + m + ":" + s + " " + ms);
	},

	showLog : function  (str) {
		if (!log)
			log = $("#log");
		log.append("<li class='" + className + "'>" + str + "</li>");
		if (log.children("li").length > 8) {
			log.get(0).removeChild(log.children("li")[0]);
		}
	},



	/**
	 * 错误
	 * @param event
	 * @param treeId
	 * @param treeNode
	 * @param XMLHttpRequest
	 * @param textStatus
	 * @param errorThrown
	 */
	onAsyncError:function   (event, treeId, treeNode, XMLHttpRequest,textStatus, errorThrown) {
		ZTreeTool.showLog("[ " + ZTreeTool.getTime()
			+ " onAsyncError ]&nbsp;&nbsp;&nbsp;&nbsp;"
			+ ((!!treeNode && !!treeNode.name) ? treeNode.name : "root"));
	},

	/**
	 * 成功
	 * @param event
	 * @param treeId
	 * @param treeNode
	 * @param msg
	 */
	onAsyncSuccess :function  (event, treeId, treeNode, msg) {
		ZTreeTool.showLog("[ " + ZTreeTool.getTime()
			+ " onAsyncSuccess ]&nbsp;&nbsp;&nbsp;&nbsp;"
			+ ((!!treeNode && !!treeNode.name) ? treeNode.name : "root"));
	},

	/**
	 *
	 * @param isCheckSelect : 输入框被勾选 或 未勾选的节点集合 true /false
	 * @param SelectParentNode: 是否获取不是叶子节点
	 * @returns {String}
	 */
	getSelectCheckedNodeIds : function (isCheckSelect,SelectParentNode){
		if(!ZTreeObj){
			ZTreeObj = $.fn.zTree.getZTreeObj(ZTreeTool.zTreeId);

		}
		var nodes = ZTreeObj.getCheckedNodes(isCheckSelect);
		if(SelectParentNode == null){
			SelectParentNode = true;
		}
		var zTreeIds = "";
		for(var i = 0;i< nodes.length ;i++){
			var node = nodes[i];
			if(!SelectParentNode && node.isParent ){
				continue;
			}
			if(zTreeIds != ""){
				zTreeIds = zTreeIds + "," + node.id;
			}else{
				zTreeIds = node.id;
			}
		}
		return zTreeIds;
	},
	zTreeBeforeRightClick:function (treeId, treeNode) {
		return true;
	},
	//by syl ，通用comboCtrl选择控件
	comboCtrl:function(target,opts){
		target.hide();//隐藏目标控件
		var c = $("<span class='input-group input-group-sm' style='width:180px;'></span>");
		var proxy = $("<input readonly type='text' class='form-control'/>");
		var panel = $("<ul class='ztree zTreeSelectPanel' style='z-index:100000'></ul>");
		var arrow = $("<button type='button' class='btn btn-default dropdown-toggle'><span class='caret'></span></button>");
		var group = $("<div class='input-group-btn'></div>");
		group.append(arrow);
		c.insertBefore(target);
		c.append(proxy).append(group);

		//默认点击前事件,如果禁用，则disabled=false,默认为false
		var beforeClick = function(event, treeNode){
			if(treeNode.disabled==true){
				return false;
			}
			panel.hide();
			return true;
		}

		//默认点击事件
		var onClick = function(event, treeId, treeNode){
			proxy.attr("value",treeNode.name);
			target.attr("value",treeNode.id);
			if(opts.callback){
				opts.callback(treeNode);
			}
		}


		if(opts.url){//url加载
			var setting = {
				data:{
					simpleData:{
						enable: true,
						idKey: "id",
						pIdKey: "pId",
						rootPId: 0
					}
				},
				async:{
					enable : true,
					url : opts.url,
					dataFilter:function(treeId, parentNode, responseData){
						return responseData.rtData;
					}
				},
				callback:{
					onClick:onClick,
					beforeClick:beforeClick,
					onAsyncSuccess:function(){
						var node = treeDom.getNodeByParam("id",target.val(),null);
						if(node!=null){
							proxy.attr("value",node.name);
							treeDom.selectNode(node);
						}

						if(target.attr("readonly") || target.attr("disabled")){
							arrow.unbind("click");
						}
					}
				}
			};
		}

		$("body").append(panel).click(function(){
			panel.hide();
		});

		//渲染树节点
		var treeDom = $.fn.zTree.init(panel, setting);

		//注册代理事件
		arrow.click(function(){
			panel.css({top:proxy.offset().top+proxy.outerHeight()+panel.parent().scrollTop(),
				left:proxy.offset().left,
				width:proxy.outerWidth(),
				height:150}).show();
			window.event.cancelBubble = true;
		});
		//树面板事件
		panel.click(function(){
			window.event.cancelBubble = true;
		});

		return proxy;
	},
	/**
	 * input 文本框绑Ztree
	 * @param zTreeId : ztree树Id
	 * @param inputId ： inputId（隐藏） 对象Id
	 * @param inputNameValue ： inputName 名称值（第一次加载赋值）
	 */
	inputBindZtree :function (zTreeId,inputId,inputNameValue){
		ZTreeTool.zTreeId = zTreeId;
		ZTreeTool.inputId = inputId;
		if(!inputNameValue){
			inputNameValue = "";
		}

		if($("#menuContent_" + ZTreeTool.zTreeId )[0]){//如果存在 则，不需要绑定	
			$("#"+inputId + "Name").val(inputNameValue);//重新给名称赋值
			return;
		}
		//Ztree之前select样式的input
		var spanWidth = $("#" + zTreeId).outerWidth();
		$("#" + zTreeId).before("<span class='combo'  style='width: "+spanWidth+"px; height: 26px;background:#f1f1f1;' id='" + inputId + "NameSpan'>"
			+"<input title=''  class='combo-text' name='" + inputId + "Name' id='" +inputId+ "Name' value='" + inputNameValue + "' style=' height: 26px;line-height:26px; width: "+ (spanWidth - 28)+"px;background:#f1f1f1;' type='text'  readOnly='readonly'  />"
			+" <span>"
			+	"<span class='combo-arrow' style='height: 26px;background-color:#aecaf0;'  ></span>"
			+" </span>"
			+ "</span>");

		//ztree上包含一层div，浮动
		$("#" + zTreeId).wrap(function (){//
			return "<div id='menuContent_" + ZTreeTool.zTreeId + "' class='menuContent_" + ZTreeTool.zTreeId + "'  style=' position: absolute;display:none;z-index:10;'></div>";
		});
		$("#" + inputId + "Name").bind("click", ZTreeTool.showInputBindZtree);
		$("#" + inputId + "NameSpan").bind("click", ZTreeTool.showInputBindZtree);
		//inputBindingZtree(menuContentDeptId);
	},

	/**
	 * 显示上级树
	 */
	showInputBindZtree:function () {
		if($("#menuContent_" + ZTreeTool.zTreeId).is(':hidden')){
			var cityObj = $("#" + ZTreeTool.inputId+ "Name");
			var cityOffset = $("#" + ZTreeTool.inputId+ "Name").position();
			//$("#menuContent_" + ZTreeTool.zTreeId).show();
			$("#" + ZTreeTool.zTreeId).show();
			var leftLength = cityOffset.left;
			var topLength = cityOffset.top;
//	 		if (isBrowserVersonTop()) {  //判断是否需要处理兼容模式
//	 			leftLength = leftLength + xScrollLength;
//	 			topLength = topLength + yScrollLength;
//	 		}
			$("#menuContent_" + ZTreeTool.zTreeId).css({left:leftLength + "px", top:topLength+ cityObj.outerHeight() + "px"}).slideDown("fast");
			$("body").bind("mousedown", ZTreeTool.onBodyDown);
		}else{
			$("#menuContent_" + ZTreeTool.zTreeId).hide();
		}

	},

	//隐藏树
	hideZtreeMenu : function () {
		$("#menuContent_" + ZTreeTool.zTreeId).fadeOut("fast");
		$("body").unbind("mousedown", ZTreeTool.onBodyDown);
	},

	//点击body时触发事件
	onBodyDown : function (event) {
		if (!(event.target.id == "menuBtn" || event.target.id == "menuContent_" + ZTreeTool.zTreeId || $(event.target).parents("#menuContent_" + ZTreeTool.zTreeId).length>0)) {
			ZTreeTool.hideZtreeMenu();
		}
	}
};




