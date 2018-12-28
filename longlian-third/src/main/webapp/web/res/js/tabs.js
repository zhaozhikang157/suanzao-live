/**
 * 通用tab标签页   syl  2016.5.13
 */
(function($){
	$.addTab=function(c1,c2,opts){
		var _c1 = $("#"+c1);
		var _c2 = $("#"+c2);

		if(opts instanceof Array){
			for(var i=0;i<opts.length;i++){
				renderTab(_c1,_c2,opts[i]);
			}
		}else{
			if(opts.active==undefined){
				opts.active = true;
			}
			
			var exist = tabsExist(_c1,opts);
			if(exist){
				canceling(_c1);
				activing(exist);
				return;
			}
			
			renderTab(_c1,_c2,opts);
		}
		renderComplete(_c1,_c2);
		//initOutofBoundaryListener(_c1);
		initInnerContentWidth(_c1);

	};

	function renderTab(c1,c2,opts){
		var title = opts.title;
		var url = opts.url;
		var active = opts.active;
		var cache = opts.cache;
		var closable = opts.closable;
		
		var outerTabContainer = c1[0].outerTabContainer;
		if(!outerTabContainer){
			var left = $("<div style='float:left;display:none' class='ll_tab_pointer_left'><i class=\"glyphicon glyphicon-chevron-left\"></i></div>");
			var right = $("<div style='float:right;display:none' class='ll_tab_pointer_right'><i class=\"glyphicon glyphicon-chevron-right\"></i></div>");
			c1[0].tab_left = left[0];
			c1[0].tab_right = right[0];
			var center = $("<div style='overflow:hidden'></div>");
			
			c1.append(left).append(right).append(center);
			c1[0].tab_center = center[0];
			
			outerTabContainer = $("<div style='width:5000px;'></div>");
			c1[0].outerTabContainer = outerTabContainer[0];
			center.append(outerTabContainer);

			//注册按钮事件
			left.click(function(){
				var innerContentWidth = c1[0].innerContentWidth;
				var delta = center.width();
				var scrollLeftDelta = center.scrollLeft()-delta;
				scrollLeftDelta = scrollLeftDelta<=0?0:scrollLeftDelta;
				center.animate({scrollLeft:scrollLeftDelta},500);
			});
			
			right.click(function(){
				var innerContentWidth = c1[0].innerContentWidth;
				var delta = center.width();
				var scrollLeftDelta = center.scrollLeft()+delta;
				scrollLeftDelta = scrollLeftDelta>=innerContentWidth?innerContentWidth-delta:scrollLeftDelta;
				center.animate({scrollLeft:scrollLeftDelta},500);
			});
		}

		var tab = $("<div class='ll_tab'>"+title+"</div>");
		var closeBtn = $("<i class='tee_close glyphicon glyphicon-remove'></i>").hide();
		tab.append(closeBtn);
		
		tab[0].url = url;
		tab[0]._title = title;
		tab[0].active = active;
		tab[0].cache = cache;
		tab[0].closable = closable;

		var tabContent = $("<iframe style='height:100%;width:100%;dispaly:none;' frameborder=0></iframe>");
		tab[0].tabContent = tabContent[0];

		c2.append(tabContent);
		$(outerTabContainer).append(tab);

		tab.click(function(){
			canceling(c1);
			activing(tab);
			if(closable){
				closeBtn.show();
			}
		});
		
		closeBtn.click(function(){
			canceling(c1);
			var preTab = tab.prev();
			if(preTab.length!=0){
				activing(preTab);
			}else{
				var nextTab = tab.next();
				if(nextTab.length!=0){
					activing(nextTab);
				}
			}
			tab.remove();
			tabContent[0].contentWindow.document.write('');//清空iframe的内容
			tabContent[0].contentWindow.close();//避免iframe内存泄漏
			tabContent.remove();
			tabContent = null;
			tab = null;
			initInnerContentWidth(c1);
			try{
				CollectGarbage();
			}catch(e){}
		});
		
		if(active){
			$($(tab)[0].tabContent).attr("src",$(tab)[0].url);
			canceling(c1);
			activing(tab);
		}
	}
	
	function activing(tab){
		$($(tab)[0].tabContent).show();
		if(!$(tab)[0].active || !$(tab)[0].cache){
			$($(tab)[0].tabContent).attr("src",$(tab)[0].url);
		}
		$(tab).addClass("ll_tab_select");
		$(tab)[0].active = true;
		if($(tab)[0].closable){
			$(tab).find(".tee_close:first").show();
		}
	}

	function canceling(c1){
		$(c1[0].outerTabContainer).children().each(function(i,obj){
			$(obj.tabContent).hide();
			$(obj).removeClass("ll_tab_select");
		});
		$(c1[0].outerTabContainer).find(".tee_close").each(function(i,obj){
			$(obj).hide();
		});
	}
	
	function renderComplete(c1,c2){
		var lastActivedTab;
		$(c1[0].outerTabContainer).children().each(function(i,obj){
			if(obj.active){
				lastActivedTab = $(obj);
			}
			$(obj.tabContent).hide();
		});
		if(lastActivedTab){
			$(lastActivedTab[0].tabContent).show();
		}
	}

	function initOutofBoundaryListener(c1){
		if(!c1[0].listener){
			setInterval(function(){
				floatCtrlButtonDetach(c1);
			},1000);
			c1[0].listener = true;
		}
		floatCtrlButtonDetach(c1);
	}

	function floatCtrlButtonDetach(c1){
		var outerTabContainer = $(c1[0].outerTabContainer);
		var contentDelta = 0;
		var left = $(c1[0].tab_left);
		var right = $(c1[0].tab_right);

		contentDelta = c1[0].innerContentWidth;
		
		if(contentDelta>c1.outerWidth()){
			left.show();
			right.show();
		}else{
			left.hide();
			right.hide();
			$(c1[0].tab_center).scrollLeft(0);
		}
	}

	function initInnerContentWidth(c1){
		var outerTabContainer = $(c1[0].outerTabContainer);
		var contentDelta = 0;
		outerTabContainer.children().each(function(i,obj){
			contentDelta += N($(obj).outerWidth())+N($(obj).css("marginLeft"))+N($(obj).css("marginRight"));
		});
		c1[0].innerContentWidth = contentDelta;
	}

	function N(val){
		try{
			var o = parseInt(val);
			return (o+0)==o?o:-1;
		}catch(e){
			return -1;
		}
	}
	
	//该标签是否存在
	function tabsExist(_c1,opts){
		var exist;
		$(_c1[0].outerTabContainer).children().each(function(i,obj){
			if(obj._title==opts.title){
				exist = $(obj);
			}
		});
		return exist;
	}
	
	
})(jQuery);
