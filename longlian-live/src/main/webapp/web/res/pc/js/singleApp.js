var login = angular.module("course",[]);
var isSaving = false;
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.course = {
            startTime: "",
        }
        $scope.seriesList= [];
        $scope.courseTypes = [];
        $scope.seriesChange = function(courseId){
            mainService.seriesChange(courseId).then(function(result){
                var seriesChargeAmt = result.data.chargeAmt;
                if(seriesChargeAmt=="0.00"){
                    $('.trySeeTime').hide();
                }else{
                    $('.trySeeTime').show();
                }
            })

        }
        $scope.sub = function(callBack){
            $('.errorMessages').html('');
            var src = $(".coverssAddressImg").attr("src");
            //console.log(src);
            if(!src){
                $('.errorMessages').html('请上传课程封面图片！');
                return;
            }
            var liveTopic = $("#liveTopic").val();
            if(!liveTopic){
                $('.errorMessages').html('请填写课程名称！');
                return;
            }
            var startTime = $("#startTime").val();
            if(!startTime){
                $('.errorMessages').html('请选择开课时间！');
                return;
            }
        /*    var recTime = $("#recTime").val();
            if(!recTime){
                $('.errorMessages').html('请选择直播时长！');
                return;
            }*/
            //验证金额
            if($(".getMuch").hasClass("btn-primary")) {
                if (isNaN($('#chargeAmt').val())) {
                    $('.errorMessages:visible').html("金额为1 ~ 10000!");
                    return;
                }
                if ($('#chargeAmt').val() < 1 && $('#chargeAmt').val() !== '0.00') {
                    $('.errorMessages:visible').html("金额为1 ~ 10000!");
                    return;
                } else {
                    if ($('#chargeAmt').val() > 10000) {
                        $('.errorMessages:visible').html("金额为1 ~ 10000!");
                        return;
                    }
                }
                //modify by cyc
                var customDistribution = $('#customDistribution').val();
                if(customDistribution != null && customDistribution.length > 0){
                    var pattern = /^[0-9]{1,3}$/;
                    if(!pattern.exec(customDistribution)){
                        $('.errorMessages').html('分销比列只能1-3位正整数！');
                        return false;
                    }
                    // add by cyc
                    if(customDistribution < 0 || customDistribution > 100){
                        $('.errorMessages').html('分销比列只能在0~100之间！');
                        return false;
                    }
                }
            }
            if($(".series").hasClass("btn-primary")){
                var seriesCourseId =$("#seriesCourseId").val();
                if(!seriesCourseId){
                    $('.errorMessages').html('暂无系列课,请先创建系列课！');
                    return;
                }
            }else{
                var isRelay = $("#isRelay").val();
                if(!isRelay){
                    $('.errorMessages').html('请选择是否转播！');
                    return;
                }else if(isRelay == '1'){
                    var relayCharge = $("#relayCharge").val();
                    var relayScale = $("#relayScale").val();
                    if(relayCharge){
                        if(relayCharge < 0|| relayCharge > 10000){
                            $('.errorMessages').html('转播金额在0~10000之间！');
                            return;
                        }
                    }
                    if($(".getMuch").hasClass("btn-primary")) {
                        if (relayScale) {
                            var pattern = /^[0-9]{1,3}$/;
                            if (!pattern.exec(relayScale)) {
                                $('.errorMessages').html('请输入正确的分成比例（0~100之间的整数）');
                                return false;
                            }
                            if (relayScale < 0 || relayScale > 100) {
                                $('.errorMessages').html('请输入正确的分成比例（0~100之间的整数）');
                                return;
                            }
                        }
                    }
                }
            }
            if(!relayCharge){
                $scope.course.relayCharge = 0;
            }
            if(!relayScale){
                $scope.course.relayScale = 0;
            }
            var liveWay =$('#liveWay li p.on').attr('liveWay');
            if(!liveWay){
                $('.errorMessages').html('请选择直播方式！');
                return;
            }
            var videoSrc = $("#videoAddress").val();
            if(!videoSrc){
                $('.errorMessages').html('请上传课程视频！');
                return;
            }
            var remark = $("#remark").val().trim();
            if(remark.length>800){
                $('.errorMessages').html('课程简介不能超过800字！');
                return;
            }
            var minute = $('#minute').val();
            var second =$('#second').val();
            $scope.course.trySeeTime =time_to_sec(minute+":"+second);
            //if ($("form").valid(null,"error!")==false){
            //    //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
            //    return false;
            //}
            // 获取Base64编码后的图像数据，格式是字符串
            // "data:image/png;base64,"开头,需要在客户端或者服务器端将其去掉，后面的部分可以直接写入文件。
            //var photoImagedata = "";
            //if($("#photoImageAddressImg-canvas-file").find("canvas")[0]) photoImagedata = $("#colImageAddressImg-canvas-file").find("canvas")[0].toDataURL("image/jpeg");
            // var colimagedata =  encodeURIComponent(dataurl);//列表页
            var coversimagedata = "";
            if($("#coverssAddressImg-canvas-file").find("canvas")[0]) coversimagedata = $("#coverssAddressImg-canvas-file").find("canvas")[0].toDataURL("image/jpeg");
            //var coversimagedata =  encodeURIComponent(dataurl);//封面
            $scope.course.coverssAddress = coversimagedata;
            //$scope.course.colImgAddress = colimagedata;
            $scope.course.coursePhoto = coursePhoto.join(";");
            addDatext();
            $scope.course.courseContent=JSON.stringify(courseContent);
            if ($("form").valid(null, "error!") == false) {
                return false;
            }
            if (isUploading) {
                alert("视频正在上传中请稍后！")
                return false;
            }

            if (isRemarkImageUploading) {
                alert("图片正在上传中请稍后！")
                return false;
            }

            if (isSaving) {
                return false;
            }
            isSaving = true;

            //$scope.course.seriesCourseId = $("select[name=seriesCourseId] option[selected]").val();
            var data = {};
            data=  mainService.doSave($scope.course).then(function(result) {
                    var success = result.success;
                    data = result;
                    isSaving = false;
                    isSaved = true;
                    window.location.href="/pcCourse/index.user";

                },
                function(error){
                    //alert("保存失败，请与管理员联系！");
                    isSaving = false;
                });
        }

        $scope.setCoverssAddress= function(picAddress){
            $scope.course.coverssAddress = picAddress;
        }

        $scope.setVideoAddress= function(videoAddress){
            $scope.course.videoAddress = videoAddress;
        }

        $scope.setColImgAddress= function(colImgAddress){
            $scope.course.colImgAddress = colImgAddress;
        }
        $scope.setCourseWare= function(coursePhoto){
            $scope.course.coursePhoto = coursePhoto;
        } ,
            $scope.setDate = function(obj){

                $scope.course.startTime =  new Date($(obj).val());
            },
            $scope.setLiveWay = function(liveWay){
                $scope.course.liveWay =liveWay;
            }

        $scope.getSeriesList = function () {
            mainService.getSeriesList().then(function (result) {
                    $scope.seriesList = result.data;
                },
                function (error) {
                    jbox_notice({content: "获取系列课列表失败！", autoClose: 2000, className: "error"});
                });

        },
            $scope.getCoursesType = function(){
                mainService.getAllCourseType().then(function(result) {
                        $scope.courseTypes = result.data;
                        $scope.course.courseType=1003;
                    },
                    function(error){
                        alert("获取课程类型失败！");
                    });
            }
        $scope.getCoursesType();
            $scope.getSeriesList();
    }
])

/**
 * 时间转为秒
 * @param time 时间(00:00:00)
 * @returns {string} 时间戳（单位：秒）
 */
var time_to_sec = function (time) {
    var s = '';
    var min = time.split(':')[0];
    var sec = time.split(':')[1];
    s =Number(min*60) + Number(sec);
    return s;
};
login.service("mainService",function($http , $q){
    return {
        doSave : function(course) {
            console.log(course)
            return $http.post('/pcCourse/createPcCourse.user', course).then(function (response) {
                return response.data;
            });
        }
        , getSeriesList: function () {
            return $http.get('/pcCourse/getSeriesList.user').then(function (response) {
                return response.data;
            });
        }, seriesChange : function(courseId){
            return $http.get('/pcCourse/getSeriesCourse.user?courseId='+courseId).then(function(response) {
                return response.data;
            });
        }, getAllCourseType:function () {
            return $http.get('/courseType/getCourseType').then(function (response) {
                return response.data;
            });
        }
    }
})

function doSave(callback){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.sub(function(json){
    });
}

function setDate(obj){
    if(obj && $(obj).val()){
        var appElement = document.querySelector('[ng-controller=task]');
        var $scope = angular.element(appElement).scope();
        var  json = $scope.setDate(obj);
    }else{
       // $('.errorMessages').html('请选择开课时间！');
    }
}
