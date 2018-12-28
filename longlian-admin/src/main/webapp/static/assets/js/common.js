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
	/*$(".add-detail table tbody tr").mouseover(function() {
		$(this).css({'background-color' : '#4894d8'});  //#ebebeb
	}).mouseout(function() {
		$(".add-detail table tbody tr:even").css({"background-color" : "#FFFFFF"});
		$(".add-detail table tbody tr:odd").css({"background-color" : "#f7f7f7"});
	
	});*/
	
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

	/**
	 * 摘自jquery.form.js总的一段代码，用户清空表单。
	 * Clears the form data.  Takes the following actions on the form's input fields:
	 *  - input text fields will have their 'value' property set to the empty string
	 *  - select elements will have their 'selectedIndex' property set to -1
	 *  - checkbox and radio inputs will have their 'checked' property set to false
	 *  - inputs of type submit, button, reset, and hidden will *not* be effected
	 *  - button elements will *not* be effected
	 */
	$.fn.clearForm = function(includeHidden) {
		return this.each(function() {
			$('input,select,textarea', this).clearFields(includeHidden);
		});
	};

	/**
	 * Clears the selected form elements.
	 */
	$.fn.clearFields = $.fn.clearInputs = function(includeHidden) {
		var re = /^(?:color|date|datetime|email|month|number|password|range|search|tel|text|time|url|week)$/i; // 'hidden' is not in this list
		return this.each(function() {
			var t = this.type, tag = this.tagName.toLowerCase();
			if (re.test(t) || tag == 'textarea') {
				this.value = '';
			}
			// if (re.test(t) || tag == 'textarea' || tag == 'select') {
			// 	debugger;
			// 	this.value = '';
			// }
			else if (t == 'checkbox' || t == 'radio') {
				this.checked = false;
			}
			else if (tag == 'select') {
				this.selectedIndex = 0;
			}
			else if (t == "file") {
				if (/MSIE/.test(navigator.userAgent)) {
					$(this).replaceWith($(this).clone(true));
				} else {
					$(this).val('');
				}
			}
			else if (includeHidden) {
				// includeHidden can be the value true, or it can be a selector string
				// indicating a special test; for example:
				//  $('#myForm').clearForm('.special:hidden')
				// the above would clean hidden inputs that have the class of 'special'
				if ( (includeHidden === true && /hidden/.test(t)) ||
					(typeof includeHidden == 'string' && $(this).is(includeHidden)) ) {
					this.value = '';
				}
			}
		});
	};
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

/**
 * 是否为空字符串
 * @param value 需要判断的字符串
 * @returns {boolean} true:空字符串，false:非空字符串
 */
function isBlank(value) {
	return null == value || undefined == value || '' == $.trim(value);
}

/**
 * 判断是否为合法url地址
 * @param url 需要判断的url字符串
 * @returns {boolean} true:合法url， false:不合法url
 */
function isUrl(url) {
    var RegUrl = new RegExp();
    var reg=/(http|ftp|https|watchlive|teacher):\/\/[\w\-_]+(\.[\w\-_]+)+([\w\-\.,@?^=%&:/~\+#]*[\w\-\@?^=%&/~\+#])?/;
    RegUrl.compile(reg);//jihua.cnblogs.com
    if (!RegUrl.test(url)) {
        return false;
    }
    return true;
}

/**
 * 是否是数字
 * @param value 需要判断的数字
 * @returns {boolean} true:数字， false：非数字
 */
function isNumber(value) {
	return /\d+$/ig.test(value);
}