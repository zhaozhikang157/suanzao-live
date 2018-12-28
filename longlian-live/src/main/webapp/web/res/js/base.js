/**
 * 各类通用函数方法 2017/12/15.
 */

var BaseFun = {
    // 设置 localstorage
    SetStorage : function(key,value){
        var key = key,
            value = value;
        if(key != null && value != null){
            if(typeof (value) !== 'string'){
                value = JSON.stringify(value);
            }
            window.localStorage.setItem(key,value);
        }else{
            alert('setStorage 传入值不对');
        }
    },
    // 获取 localstorage
    GetStorage : function(key){
        var key = key;
        var reg = /^\$\{.*\}$/;
        if(key != null){
            var value = window.localStorage.getItem(key);
            if(!reg.test(value)){
                value = JSON.parse(value);
            }
            return value;
        }else{
            alert('GetStorage 传入值不对');
        }
    },
    // 删除 localstorage
    RemoveStorage : function(key){
        window.localStorage.removeItem(key);
    },
    // 设置 Sessionstorage
    SetSession : function(key,value){
        var key = key,
            value = value;
        if(key != null && value != null){
            if(typeof (value) !== 'string'){
                value = JSON.stringify(value);
            }
            window.sessionStorage.setItem(key,value);
        }else{
            alert('setSession 传入值不对');
        }
    },
    // 获取 Sessionstorage
    GetSession : function(key){
        var key = key;
        var reg = /^\$\{.*\}$/;
        if(key != null){
            var value = window.sessionStorage.getItem(key);
            if(reg.test(value)){
                value = JSON.parse(value);
            }
            return value;
        }else{
            alert('GetSession 传入值不对');
        }
    },
    // 删除 Sessionstorage
    RemoveSession : function(key){
        window.sessionStorage.removeItem(key);
    },
    // 公共弹出框
    Dialog : {
        Pop : function(json){
            var _this = this;
            var title = json.title || '提示';
            var text = json.text || '你确定吗？';
            var cancel = json.cancel || '取消';
            var confirm = json.confirm || '确定';
            var callback = json.callback || function(){};
            var configHtml = ('<div class="dialog-box">\
                                <div class="dialog-center" style="height: 6.5rem;">\
                                    <p class="dialog-title" style="font-weight: inherit;color: #333;">'+title+'</p>\
                                    <p class="dialog-text" style="color: #666;line-height: 2.2rem;"><span style="font-size: 0.7rem;">'+text+'</span></p>\
                                    <div class="dialog-btn">\
                                        <a href="javascript:;" style="font-size: 0.8rem;color: #666;" class="dialog-cancel">'+cancel+'</a>\
                                        <a href="javascript:;" style="font-size: 0.8rem;" class="dialog-confirm">'+confirm+'</a>\
                                    </div>\
                                </div>\
                           </div>');
            $(document.body).append(configHtml);
            $('.dialog-cancel').on('click',function(){
                callback(0);
                _this.RemoveDialog();
            });
            $('.dialog-confirm').on('click',function(){
                callback(1);
                _this.RemoveDialog();
            });
            $('.dialog-box').on('click',function(e){
                if(this == e.target){
                    $(this).remove();
                }
            })
        },
        Pop2 : function(json){
            var _this = this;
            var title = json.title || '提示';
            var text = json.text || '你确定吗？';
            var cancel = json.cancel || '取消';
            var confirm = json.confirm || '确定';
            var callback = json.callback || function(){};
            var configHtml = ('<div class="dialog-box">\
                                <div class="dialog-center" style="height: 6.5rem;">\
                                    <p class="dialog-title" style="font-weight: inherit;color: #333;">'+title+'</p>\
                                    <p class="dialog-text" style="color: #666;line-height: 2.2rem;"><span style="font-size: 0.7rem;">'+text+'</span></p>\
                                    <div class="dialog-btn">\
                                        <a href="javascript:;" style="font-size: 0.8rem;" class="dialog-confirm">'+confirm+'</a>\
                                    </div>\
                                </div>\
                           </div>');
            $(document.body).append(configHtml);
            $('.dialog-cancel').on('click',function(){
                callback(0);
                _this.RemoveDialog();
            });
            $('.dialog-confirm').on('click',function(){
                callback(1);
                _this.RemoveDialog();
            });
            $('.dialog-box').on('click',function(e){
                if(this == e.target){
                    $(this).remove();
                }
            })
        },
        Config : function(json){
            var _this = this;
            var title = json.title || '提示';
            var text = json.text || '你确定吗？';
            var cancel = json.cancel || '取消';
            var confirm = json.confirm || '确定';
            var callback = json.callback || function(){};
            var configHtml = ('<div class="dialog-box">\
                                <div class="dialog-center">\
                                    <p class="dialog-title">'+title+'</p>\
                                    <p class="dialog-text"><span>'+text+'</span></p>\
                                    <div class="dialog-btn">\
                                        <a href="javascript:;" class="dialog-cancel">'+cancel+'</a>\
                                        <a href="javascript:;" class="dialog-confirm">'+confirm+'</a>\
                                    </div>\
                                </div>\
                           </div>');
            $(document.body).append(configHtml);
            $('.dialog-cancel').on('click',function(){
                callback(0);
                _this.RemoveDialog();
            });
            $('.dialog-confirm').on('click',function(){
                callback(1);
                _this.RemoveDialog();
            });
            $('.dialog-box').on('click',function(e){
                if(this == e.target){
                    $(this).remove();
                }
            })
        },
        Config2 : function(json){
            var _this = this;
            var closeHtml ='';
            var title = json.title || '提示';
            var text = json.text || '你确定吗？';
            var cancel = json.cancel || '取消';
            var confirm = json.confirm || '确定';
            var close = json.close;
            var callback = json.callback || function(){};
            if(close){
                //closeHtml = '<a href="javascript:;" class="dialog-box-close"></a>';
                closeHtml = '<div class="dialog-box-close"><img src="/web/res/image/dealPwdClose.png"></div>'
            }
            //<img class="dialog-Jujubepic" src="/web/res/image/Jujube.png" alt="">\  小酸枣图标
            var configHtml = ('<div class="dialog-box">\
                                <div class="dialog-center" >\
                                        '+closeHtml+'\
                                    <p class="dialog-title">'+title+'</p>\
                                    <p class="dialog-text"><span >'+text+'</span></p>\
                                    <div class="dialog-btn" style="height: 2.1rem;">\
                                        <a href="javascript:;" class="dialog-cancel" style="font-size: 0.7rem;color:#333333;">'+cancel+'</a>\
                                        <a href="javascript:;" class="dialog-confirm" style="font-size: 0.7rem;">'+confirm+'</a>\
                                    </div>\
                                </div>\
                           </div>');
            $(document.body).append(configHtml);
            $('.dialog-cancel').on('click',function(){
                callback(0);
                _this.RemoveDialog();
            });
            $('.dialog-confirm').on('click',function(){
                callback(1);
                _this.RemoveDialog();
            });
            $('.dialog-box').on('click',function(e){
                callback(3)
                if(this == e.target){
                    $(this).remove();
                }
            })
            $('.dialog-box-close').on('click',function(){
                callback(2);
                _this.RemoveDialog();
            })
        },
        RemoveDialog : function(){
            $('.dialog-box').remove();
        }
    }
};
