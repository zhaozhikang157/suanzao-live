var login = angular.module("course",[]);
var isSaving = false;
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.course = {

        }
        $scope.courseTypes = [];
        $scope.sub = function(callBack){
            $('.errorMessages').html('');
            var src = $(".coverssAddressImg").attr("src");
            console.log(src);
            if(!src){
                $('.errorMessages').html('*请上传课程封面图片！');
                return;
            }
            var liveTopic = $("#liveTopic").val();
            if(!liveTopic){
                $('.errorMessages').html('请填写课程名称！');
                return; 
            }
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

            var isRelay = $("#isRelay").val();
            if(!isRelay){
                $('.errorMessages').html('请选择是否转播！');
                return false;
            }else if(isRelay == '1'){
                var relayCharge = $("#relayCharge").val();
                var relayScale = $("#relayScale").val();
                if(relayCharge){
                    if(relayCharge < 0|| relayScale > 10000){
                        $('.errorMessages').html('转播金额在0~10000之间！');
                        return false;
                    }
                }
                if($(".getMuch").hasClass("btn-primary")) {
                   if(relayScale){
                      var pattern = /^[0-9]{1,3}$/;
                      if(!pattern.exec(relayScale)){
                          $('.errorMessages').html('请输入正确的分成比例（0~100之间的整数）');
                          return false;
                      }
                    if(relayScale < 0 || relayScale >100){
                        $('.errorMessages').html('请输入正确的分成比例（0~100之间的整数）');
                        return false;
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
            var remark = $("#remark").val().trim();
            var remark = $("#remark").val().trim();
            if(remark.length>800){
                $('.errorMessages').html('课程简介不能超过800字！');
                return;
            }

            //if ($("form").valid(null,"error!")==false){
            //    //$("#error-text").text("error!"); 1.0.4版本已将提示直接内置掉，简化前端 。
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
            $scope.course.coverssAddress = coversimagedata;
            //$scope.course.colImgAddress = colimagedata;
            $scope.course.courseImg = coursePhoto.join("&&");
              addDatext();
            $scope.course.courseContent=JSON.stringify(courseContent)
            if ($("form").valid(null, "error!") == false) {
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

        $scope.setColImgAddress= function(colImgAddress){
            $scope.course.colImgAddress = colImgAddress;
        }
        $scope.setPushContent= function(pushContent){
            $scope.course.pushContent = pushContent;
        }
    
        $scope.setCourseImg= function(coursePhoto){
            $scope.course.courseImg = coursePhoto;
        } ,
        $scope.setDate = function(obj){

            $scope.course.startTime =  new Date($(obj).val());
        },
        $scope.setLiveWay = function(liveWay){
            $scope.course.liveWay =liveWay;
        },
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
  
login.service("mainService",function($http , $q){
    return {
        doSave : function(course){
            console.log(course)
            return $http.post('/pcCourse/createPcSreiesCourse.user', course).then(function(response) {
                return response.data;
            });
        }, getSeriesList: function () {
            return $http.get('/pcCourse/getSeriesList.user').then(function (response) {
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
