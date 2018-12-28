
/**
 * auther  syl
 * bsWindow("http://www.baidu.com","标题",{buttons:[{name:"关闭",classStyle:"1"},{name:"开始",classStyle:"2"}]});
 * @param url
 * @param title
 * @param opts
 * @return
 */
function bsWindow(url,title,opts){
    if(!opts){
        opts = {};
    }
    var windowWidth =  $(document).width()-100;
    var windowHeight = $(document).height()-200;
    var width =  opts.width || windowWidth;
    var height =  opts.height || windowHeight;
    width+="";
    height+="";
    if(width.indexOf("%")!=-1){
        width = width.replace("%","");
        width = $(document).width()*(parseInt(width)*0.01);
    }else{
        width = (opts.width && windowWidth > opts.width) ? opts.width :windowWidth;
    }
    if(height.indexOf("%")!=-1){
        height = height.replace("%","");
        height = parseInt(document.documentElement.clientHeight)*(parseInt(height)*0.01);
    }else{
        height = (opts.height && windowHeight > opts.height ) ? opts.height : windowHeight ;
    }

    if(!window.BSWINDOW){
        window.BSWINDOW = $("<div class=\"modal fade\" tabindex=\"-1\" role=\"dialog\" aria-labelledby=\"myModalLabel\" aria-hidden=\"true\" style=\"\"></div>");
        window.BSWINDOW_BS1 = $("<div class=\"modal-dialog\"></div>");
        window.BSWINDOW_BS2 = $("<div class=\"modal-content\"></div>");
        window.BSWINDOW_BS3 = $("<div class=\"modal-header\"></div>");
        window.BSWINDOW_BS4 = $("<button type=\"button\" class=\"close\" data-dismiss=\"modal\" aria-hidden=\"true\">&times;</button>");
        window.BSWINDOW_BS5 = $("<h4 class=\"modal-title\" style=\"font-size:18px;font-family:微软雅黑\" id=\"myModalLabel\"></h4>");
        window.BSWINDOW_BS6 = $("<div class=\"modal-body\" style=\"padding-top:0px;padding-bottom:0px;padding-top:3px;\"><center><img src=\"/web/res/style/img/loading1.gif\" /></center></div>");
        if(url.indexOf("html:")!=-1){//加载html
            window.BSWINDOW_BS7 = $("<div></div>").append(url.substring(5));
        }else{
            window.BSWINDOW_BS7 = $("<iframe id='BSWINDOW_BS7_FRAME' frameborder=0 style=\"width:100%;height:100%\"></iframe>");
        }
        window.BSWINDOW_BS8 = $("<div class=\"modal-footer\" style=\"padding-top:3px;\"></div>");

        window.BSWINDOW.append(window.BSWINDOW_BS1);
        window.BSWINDOW_BS1.append(window.BSWINDOW_BS2);
        window.BSWINDOW_BS2.append(window.BSWINDOW_BS3);
        window.BSWINDOW_BS3.append(window.BSWINDOW_BS4).append(window.BSWINDOW_BS5);
        window.BSWINDOW_BS2.append(window.BSWINDOW_BS6);
        window.BSWINDOW_BS6.append(window.BSWINDOW_BS7);
        window.BSWINDOW_BS2.append(window.BSWINDOW_BS8);

        $("body").append(window.BSWINDOW);
    }else{
        window.BSWINDOW_BS7.remove();
        window.BSWINDOW_BS7 = $("<iframe frameborder=0 style=\"width:100%;height:100%\"></iframe>");
        window.BSWINDOW_BS6.append(window.BSWINDOW_BS7);
    }
    window.BSWINDOW_BS7.attr("src","").hide();
    window.BSWINDOW_BS6.find("img:first").show();
    //渲染标题
    window.BSWINDOW_BS5.html(title);
    window.BSWINDOW_BS1.css({"width":width});
    window.BSWINDOW_BS6.css({"height":height});
    setTimeout(function(){
        window.BSWINDOW_BS6.find("img:first").hide();
        window.BSWINDOW_BS7.attr("src",url).show();
    },1000);
    window.BSWINDOW_BS8.html("");
    //渲染按钮
    if(opts.buttons){
        for(var i=0;i<opts.buttons.length;i++){
            var button = opts.buttons[i];
            var classStyle = button.classStyle||"btn btn-default";
            var name = button.name;
            var clickFun = button.clickFun ;
            var btn = $("<button class=\""+classStyle+"\">"+name+"</button>");
            btn.click(function(){
                if(opts.submit){
                    if(opts.submit($(this).html(),window.BSWINDOW_BS7)){
                      window.BSWINDOW.modal("hide");
                    }
                }else{
                    if( jQuery.isFunction(clickFun) ){
                        clickFun($(this).html() ,window.BSWINDOW_BS7 );
                    }
                }
            });
            btn.appendTo(window.BSWINDOW_BS8);
        }
    }else{
        var close = $("<button class=\"btn btn-default\"  data-dismiss=\"modal\">关闭</button>").appendTo(window.BSWINDOW_BS8);
        var ok = $("<button class=\"btn btn-primary\">确定</button>").click(function(){
        	
        	var cw = window.BSWINDOW_BS7[0].contentWindow;
        	
        	
            $(this).attr("disabled","disabled");
            if(opts.submit){
                if(opts.submit("ok",window.BSWINDOW_BS7)){
                    window.BSWINDOW.modal("hide");
                }else{
                    $(this).removeAttr("disabled");
                }
            }else{
            	$(this).removeAttr("disabled");
                window.BSWINDOW.modal("hide");
            }
        }).appendTo(window.BSWINDOW_BS8);
    }
    window.BSWINDOW.modal("show");
}
