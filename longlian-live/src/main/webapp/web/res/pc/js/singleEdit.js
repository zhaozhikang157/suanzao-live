var login = angular.module("course",[]);
var isSaving = false;
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.course = {
            startTime: "",
        }
        $scope.courseTypes = [];
        $scope.sub = function(callBack){
            //if ($("form").valid(null,"error!")==false){
            //    //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端。
            //    return false;
            //}
            // 获取Base64编码后的图像数据，格式是字符串
            // "data:image/png;base64,"开头,需要在客户端或者服务器端将其去掉，后面的部分可以直接写入文件。
            var colimagedata = "";
            if($("#colImageAddressImg-canvas-file").find("canvas")[0]) colimagedata = $("#colImageAddressImg-canvas-file").find("canvas")[0].toDataURL("image/jpeg");
            // var colimagedata =  encodeURIComponent(dataurl);//列表页
            var coversimagedata = "";
            if($("#coverssAddressImg-canvas-file").find("canvas")[0]) coversimagedata = $("#coverssAddressImg-canvas-file").find("canvas")[0].toDataURL("image/jpeg");
            //var coversimagedata =  encodeURIComponent(dataurl);//封面
            //$scope.course.coverssAddress = coversimagedata;
            //$scope.course.colImgAddress = colimagedata;
            if(coversimagedata)
            {
                $scope.course.coverssAddress =coversimagedata;
            }else{

                $scope.course.coverssAddress = $('#coverssAddress').val();
            }
            $scope.course.coursePhoto = coursePhoto.join(";");
              addDatext();
            var minute = $('#minute').val();
            var second =$('#second').val();
            $scope.course.trySeeTime =time_to_sec(minute+":"+second);
            $scope.course.recTime =  $('#recTime').val();
            $scope.course.courseContent=JSON.stringify(courseContent)
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
            $scope.course.modelUrl = $('#modelUrl').val();
            $scope.course.cardUrl = $('#cardUrl').val();
            var isRelay = $("#isRelay").val();
            var relayCharge = $("#relayCharge").val();
            var relayScale = $("#relayScale").val();
            $scope.course.isRelay = isRelay;
            if(isRelay && isRelay == '1'){
                if(relayCharge){
                    if(relayCharge < 0|| relayScale > 10000){
                        $('.errorMessages').html('转播金额在0~10000之间！');
                        isSaving = false ;
                        return false;
                    }
                }

                if($scope.course.chargeAmt>0) {
                  if(relayScale){
                    var pattern = /^[0-9]{1,3}$/;
                    if(!pattern.exec(relayScale)){
                        $('.errorMessages').html('请输入正确的分成比例（0~100之间的整数）');
                        isSaving = false;
                        return false;
                    }
                    if(relayScale < 0 || relayScale > 100){
                        $('.errorMessages').html('请输入正确的分成比例（0~100之间的整数）');
                        isSaving = false;
                        return;
                    }
                  }
                }
            }else{
                $scope.course.relayScale = relayScale;
                $scope.course.relayCharge = relayCharge;
            }
            if(!relayCharge){
                $scope.course.relayCharge = 0;
            }
            if(!relayScale){
                $scope.course.relayScale = 0;
            }
            var data = {};
            data=  mainService.doSave($scope.course).then(function(result) {
                    var success = result.success;
                    data = result;
                    //callBack(result);
                    isSaving = false;
                    isSaved = true;
                    window.location.href="/pcCourse/index.user";
                },
                function(error){
                    isSaving = false;
                    alert("保存失败，请与管理员联系！");
                });
        }
        $scope.setCoverssAddress= function(picAddress){
            $scope.course.coverssAddress = picAddress;
        }

        $scope.setColImgAddress= function(colImgAddress){
            $scope.course.colImgAddress = colImgAddress;
        }
        $scope.setVideoAddress= function(videoAddress){
            $scope.course.videoAddress = videoAddress;
            $("#container2").html($scope.course.videoAddress);
        }

        $scope.setCourseWare= function(coursePhoto){
            $scope.course.coursePhoto= coursePhoto;
        } 
        $scope.setDate = function(obj){

            $scope.course.startTime =  new Date($(obj).val());
        }
        $scope.setLiveWay = function(liveWay){
            $scope.course.liveWay =liveWay;
        }
        $scope.setCourseImg = function(courseImg){
            $scope.course.courseContent =courseImg;
        }
            $scope.findByIdForEdit = function(){
                mainService.findByIdForEdit(id).then(function(result) {

                        var success = result.success;
                        $scope.course = result.data.course;
                        //alert()
                        console.log(result.data.course.recTime)
                        $("#coverssAddress").val(result.data.course.coverssAddress);
                        var isEnd = false;
                        //已结束的课
                        if ($scope.course.endTime &&  $scope.course.endTime != 'null') {
                            isEnd = true;
                            $("#startTimeGroup").hide();
                            $("#startTimeText").html($scope.course.startTime);
                            $("#startTimeGroup2").show();
                        }
                        $scope.course.startTime =$filter("date")(result.data.course.startTime, "yyyy-MM-dd HH:mm:ss");
                        var startTime =  new Date(Date.parse($scope.course.startTime.replace(/-/g,   "/")));
                        var now = new Date();
                        //正在播放中或者结束
                        if (now.getTime() > startTime.getTime()) {
                            //不能修改播放时间
                            $("#startTimeGroup").hide();
                            $("#startTimeText").html($scope.course.startTime);
                            $("#startTimeGroup2").show();
                            //在线上，且正在播放
                            if ($scope.course.status == '0' && !isEnd) {
                                //不能修改视频
                                $('#uploadVideoGroup').hide();
                            }
                        }


                        //$scope.course = result.data.course.liveTopic;
                        $scope.coverssAddressImgSrc = result.data.course.coverssAddress;
                        $scope.course.videoAddress = result.data.course.videoAddress;
                        $("#videoAddress").val($scope.course.videoAddress);
                        $("#container2").html($scope.course.videoAddress);
                        $("#recTime").val($scope.course.recTime);

                        //$scope.course.recTime = result.data.course.recTime;
                        //$scope.course.remark = result.data.course.remark;
                   
                        if(result.data.course.isSeriesCourse==0 && result.data.course.seriesCourseId==0){
                            if(result.data.course.chargeAmt=='0.00'){
                                $(".trySeeTime").hide();
                            }
                        }
                        if(result.data.course.seriesCourseId>0){
                            if(result.data.seriesChargeAmt=='0.00'){
                                $(".trySeeTime").hide();
                            }
                        }
                     
                        var minute = $('#minute').val(parseInt(result.data.course.trySeeTime/60));
                        var second =$('#second').val(result.data.course.trySeeTime%60);
                        var isRelay = result.data.course.isRelay;
                        if(isRelay == null || isRelay == '0'){
                            $("#relayCharge").parent().parent().hide();
                        }
                        $scope.course.isRelay = isRelay;
                        $("#isRelay").val(isRelay) ;
                        if(result.data.seriesCourseName != ""){
                            $("#seriesCourse").show();
                            $("#seriesCourseName").text(result.data.seriesCourseName);
                        }else{
                            $("#seriesCourse").hide();
                        }
                        $(function () {
                            $("#form1").validation({icon: true});
                        });
                        var chargeAmt = $scope.course.chargeAmt;
                        if(chargeAmt > 0){
                            $("#relayScale").show();
                            $("#relayScale_tx").show();
                        }else{
                            $("#relayScale").hide();
                            $("#relayScale_tx").hide();
                            $("#relayScale").val('');
                        }
                        //处理系列课节课
                        if($scope.course.seriesCourseId > 0){
                            $("#isRelay").parent().parent().hide();
                        }
                    },
                    function(error){
                        jbox_notice({content: "获取课程信息失败！", autoClose: 2000, className: "error"});
                    });
            }
          $scope.getCourseWareList = function(){
                mainService.getCourseWareList(id).then(function(result) {
                        $scope.courseWarelist = result.data;
                        var courseWarelist = result.data;//课程图片简介
                        if (courseWarelist.length > 0) {//遍历简介图片
                            $.each(courseWarelist, function (i, n) {
                                coursePhoto.push(n.address);
                                var html = "";
                                html += '<div class="picss">';
                                html += '<img class="pic" src="' + n.address + '"/>'
                                html += '<span class="closes" onclick="clocs(this)"></span>'
                                html += "</div>";
                                $(".add_imgs").before(html);
                            });
                            uploadCoursePhoto(coursePhoto.join(";"));
                        }
                    },
                    function(error){
                        jbox_notice({content: "获取介绍图片失败！", autoClose: 2000, className: "error"});
                    });
            }
        $scope.getCourseImglist = function(){
            mainService.getCourseImglist(id).then(function(result) {
                    $scope.courseImglist = result.data;
                    var courseImglist = result.data;//课程图片简介
                    if (courseImglist.length > 0) {//遍历简介图片
                        $.each(courseImglist, function (i, n) {
                            var json = {"img": n.address, "content": n.content};
                            courseContent.push(json);
                            var html = "";
                            if(n.address==''){
                                html += '<div class="contBoxall">';
                                html += '<div class="wordCont"><dl>' +
                                    '<dd>';
                                html += '<textarea class="datext" placeholder="请输入文字简介" maxlength="800">'+n.content+'</textarea></dd></dl>';
                                html += '<span class="newcloss" onclick="colssesWx(this)"></span></div><div class="rightBox">'
                                html += '<span class="oPrev" onclick="oPrev(this)"></span>'
                                html +=' <span class="oNext" onclick="oNext(this)"></span>'
                                html += "</div></div>";
                            }else{
                                html += '<div class="contBoxall">';
                                html += '<div class="wordCont"><dl>' +
                                    '<dt><img src="' + n.address + '"></dt><dd>';
                                html += '<textarea class="datext" placeholder="请输入图片简介" maxlength="800">'+n.content+'</textarea></dd></dl>';
                                html += '<span class="newcloss" onclick="colssesWx(this)"></span></div><div class="rightBox">'
                                html += '<span class="oPrev" onclick="oPrev(this)"></span>'
                                html += '<span class="oNext" onclick="oNext(this)"></span>'
                                html += "</div></div>";
                            }
                          
                            $("#detailsList").append(html);
                        });
                        uploadCourseImg(JSON.stringify(courseContent));
                    }
                },
                function(error){
                    jbox_notice({content: "获取介绍图片失败！", autoClose: 2000, className: "error"});
                });
        }
        $scope.getCoursesType = function(){
            mainService.getAllCourseType().then(function(result) {
                    $scope.courseTypes = result.data;
                },
                function(error){
                    alert("获取课程类型失败！");
                });
        }
        $scope.getCoursesType();
           $scope.getCourseWareList();
           $scope.getCourseImglist();
        if(id > 0){
            $scope.findByIdForEdit();
        }

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
//删除图片
function colssesWx(that) {
    var index = $(that).parents('.contBoxall').index();
    $(that).parents('.contBoxall').remove();
    courseContent.splice(index,1);
};

login.service("mainService",function($http , $q){
    return {
        doSave : function(course) {
            return $http.post('/pcCourse/uploadPcCourse.user', course).then(function (response) {
                return response.data;
            });
        },
        findByIdForEdit : function(id){
                return $http.get('/pcCourse/findByIdForEdit?id=' + id).then(function(response) {
                    return response.data;
                });
        },
        getCourseWareList : function(id){
        return $http.get('/pcCourse/getCourseWarelist?id=' + id).then(function(response) {
            return response.data;
        });
        } ,
        getCourseImglist : function(id){
            return $http.get('/pcCourse/getCourseImglist?id=' + id).then(function(response) {
                return response.data;
            });
        },
        getAllCourseType:function () {
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
        callback(json);
    });
}

function setDate(obj){
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var  json = $scope.setDate(obj);
}
