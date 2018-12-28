
var x=null;
var listObj=null;
//鼠标按下不放时的操作
function setTimeStart(type)
{//欢迎来到站长特效网，我们的网址是www.zzjs.net，很好记，zz站长，js就是js特效，本站收集大量高质量js代码，还有许多广告代码下载。
	listObj=document.getElementById('forder');
//	超过0.3秒启动连续的向上(下)的操作
	if(type=="up")
	{
		x=setTimeout(upListItem,300);
	}else
	{
		x=setTimeout(downListItem,300);
	}
}
//将选中item向上
function upListItem()
{
	var selIndex=listObj.selectedIndex;
	if(selIndex<0)
	{
		if(x!=null){clearTimeout(x);}
		return;
	}
	if(selIndex==0)
	{
		if(x!=null){clearTimeout(x);}
		return;
	}//欢迎来到站长特效网，我们的网址是www.zzjs.net，很好记，zz站长，js就是js特效，本站收集大量高质量js代码，还有许多广告代码下载。
	var selValue=listObj.options[selIndex].value;
	var selText=listObj.options[selIndex].text;
	listObj.options[selIndex].value=listObj.options[selIndex-1].value;
	listObj.options[selIndex].text=listObj.options[selIndex-1].text;
	listObj.options[selIndex-1].value=selValue;
	listObj.options[selIndex-1].text=selText;
	listObj.selectedIndex=selIndex-1;
	if(selIndex+1>0)
	{
		x=setTimeout(upListItem,200)
	}
}
//将选中item向下
function downListItem()
{
	var selIndex=listObj.selectedIndex;
	if(selIndex<0)
	{
		if(x!=null){clearTimeout(x);}
		return;
	}
	if(selIndex==listObj.options.length-1)
	{
		if(x!=null){clearTimeout(x);}
		return;
	}//欢迎来到站长特效网，我们的网址是www.zzjs.net，很好记，zz站长，js就是js特效，本站收集大量高质量js代码，还有许多广告代码下载。
	var selValue=listObj.options[selIndex].value;
	var selText=listObj.options[selIndex].text;
	listObj.options[selIndex].value=listObj.options[selIndex+1].value;
	listObj.options[selIndex].text=listObj.options[selIndex+1].text;
	listObj.options[selIndex+1].value=selValue;
	listObj.options[selIndex+1].text=selText;
	listObj.selectedIndex=selIndex+1;
	if(selIndex+1<listObj.options.length-1)
	{
		x=setTimeout(downListItem,200)
	}//欢迎来到站长特效网，我们的网址是www.zzjs.net，很好记，zz站长，js就是js特效，本站收集大量高质量js代码，还有许多广告代码下载。
}

function saveSortMenu(){
	
	var parm = "";
	$("#forder").find("OPTION").each(function(index,element){
		var id = $(element).attr("value");
		parm += id+"#"+(index+1)+",";
		console.info("id:"+id+",index:"+(index+1));
	});
	if(parm == null || parm == ""){
		 jbox_notice({content: "请选择菜单父节点", autoClose: 2000, className: "error"});
		 return;
	}
	 
	 var obj = tools.requestRs("/mRes/saveSortMenu", {parm:parm}, 'post');
	 
	 if (obj.success) {
         jbox_notice({content: "保存成功", autoClose: 2000, className: "success"});
     } else {
         jbox_notice({content: "保存失败", autoClose: 2000, className: "error"});
     }
	
}

function loadMenuSort(id){
	 $("#forder").html("");  
	 var obj = tools.requestRs("/mRes/findMenuByParentId", {id:id}, 'post');
	/* var str = JSON.stringify(obj);  
 	console.info(str);*/
 	  $.each(obj.data,function(n,v) {   
 		 var str = "<OPTION value='"+v.id+"'>"+v.name+"</OPTION>";
     $("#forder").append(str);  
      
 });
}


		

