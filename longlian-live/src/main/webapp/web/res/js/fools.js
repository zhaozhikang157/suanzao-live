$(function(){
    $('body,html').on('touchmove',function(e){
        e.preventDefault();
    },false)
    $('.wraper').swipeUp(function(){
        if( $(this).find('.on').next().length == 0 )return;
        hidePage();
        $(this).find('.on').css({'transform':'translateY(0)','opacity':0}).animate({
            'transform':'translateY(-100%)',
            'opacity':1
        }).next().css({'transform':'translateY(100%)','opacity':0}).animate({
            'transform':'translateY(0%)',
            'opacity':1
        },function(){
            var index = $(this).index();
            showpage( index );

        }).addClass('on').siblings().removeClass('on')

    }).swipeDown(function(){
        if( $(this).find('.on').prev().length == 0 )return;
        hidePage();
        $(this).find('.on').css('transform','translateY(0)').animate({
            "transform":'translateY(100%)'
        }).prev().css('transform','translateY(-100%)').animate({
            'transform':'translateY(0)'
        },200,"ease-in",function(){

            var index = $(this).index();
            showpage( index );

        }).addClass('on').siblings().removeClass('on')

    })
    function showpage( index ){
        switch (index){
            case 0:
                page1();
                $(".foocation").show();
                break;
            case 1:
                $(".foocation").hide();
                page2()
                break;
        }

    }

    function page1(){
        $('.seaione').children().show();
    }

    function page2(){
        $(".seaitwo,.seaifour").children().show();
    }
    function hidePage(){
        $(".wraper .seaipublic" ).not(".on").children().hide();
    }
})