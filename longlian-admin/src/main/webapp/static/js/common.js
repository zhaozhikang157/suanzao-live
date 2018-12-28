function URLencode(sStr) {
   sStr = escape(sStr).replace(/\+/g, '%2B').replace(/\"/g, '%22').replace(/\'/g, '%27').replace(/\//g, '%2F');
    return sStr;
}
/**
 * 设置列表隔行变色
 */
function set_table_color(){
	$(".add-detail > table > tbody   tr:not(:first)").css('background','#FFFFFF');
	$(".add-detail > table > tbody  tr:even").css('background', '#f7f7f7');


	/** 2、table tr td不同行背景颜色鼠标经过颜色变化* */
	$(".add-detail table tbody tr").mouseover(function() {
		$(this).css({'background-color' : '#EBEBEB'});
	}).mouseout(function() {
		$(".add-detail table tbody tr:even").css({"background-color" : "#FFFFFF"});
		$(".add-detail table tbody tr:odd").css({"background-color" : "#f7f7f7"});
	
	});
	
}

//到指定的分页页面
function topage(page){
	var form = document.forms['form'];
	form.pageNo.value=page;
	form.submit();
}

function checkAll(name, o){
    var o = $("#operation");
    var textVal = o.html();
    if(textVal == '全选'){
        var ary = document.getElementsByName(name);
        for(var i=0; i<ary.length; i++) {
            ary[i].checked = true;
        }
    }
    else{ // 全不选
        var ary = document.getElementsByName(name);
        for(var i=0; i<ary.length; i++) {
            ary[i].checked = false;
        }
    }
    textVal = textVal =='全选'?'取消全选':'全选';
    o.html(textVal);
    return;
}

function allChecked(allName,name,rowId){
	/** jQ实现全选全不选* */
	var _jq_chk = $(':checkbox[name='+allName+']');
	var _jqitems = $(':checkbox[name='+name+']');
	var _rows = $('#'+rowId+'>tr>td');

	// 全选与全不选一体实现
	_jq_chk.click(function() {
		// 列表中选框和全选选框统一状态
		_jqitems.add(_jq_chk).attr('checked', this.checked);
	});

	// 选框的点击事件
	_jqitems.click(function(e) {
		// 阻止冒泡,避免行点击事件中,直接选择选框无效
		e.stopPropagation();
		// 判断选中个数与实际个数是否相同,以确定全选/全不选状态
		_jq_chk.attr('checked', _jqitems.size() == _jqitems
				.filter(':checked').size());
	});

	// 点选行时选中行内的checkbox
	_rows.bind({
		mouseenter : function() {
			$(this).addClass('hover');
		},
		mouseleave : function() {
			$(this).removeClass('hover');
		},
		// 点选
		click : function() {
			// 行内点击时,行内的选框状态为原状态取反
			$(this).find(':checkbox').attr('checked',
					!$(this).find(':checkbox').get(0).checked)
			// 判断选中个数与实际个数是否相同,以确定全选/全不选状态
			_jq_chk.attr('checked', _jqitems.size() == _jqitems
					.filter(':checked').size());
		}
	});
}
//input 输入框清除内容函数
jQuery(function($) {
	  function tog(v){return v?'addClass':'removeClass';} 
	  $(document).on('input', '.clearable', function(){
	    $(this)[tog(this.value)]('x');
	  }).on('mousemove', '.x', function( e ){
	    $(this)[tog(this.offsetWidth-18 < e.clientX-this.getBoundingClientRect().left)]('onX');   
	  }).on('click', '.onX', function(){
	    $(this).removeClass('x onX').val('').change();
	  });
	  clockon();
	});
 
function clockon() {
    var now = new Date();
    var year = now.getFullYear(); //getFullYear getYear
    var month = now.getMonth();
    var date = now.getDate();
    var day = now.getDay();
    var hour = now.getHours();
    var minu = now.getMinutes();
    var sec = now.getSeconds();
    var week;
    month = month + 1;
    if (month < 10) month = "0" + month;
    if (date < 10) date = "0" + date;
    if (hour < 10) hour = "0" + hour;
    if (minu < 10) minu = "0" + minu;
    if (sec < 10) sec = "0" + sec;
    var arr_week = new Array("星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六");
    week = arr_week[day];
    var time = "";
    time = year + "年" + month + "月" + date + "日" + " " + hour + ":" + minu + ":" + sec + " " + week;

    $("#bgclock").html(time);

    var timer = setTimeout("clockon()", 200);
}