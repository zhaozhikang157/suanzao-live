var login = angular.module("course",[]);
login.controller("task",["mainService" ,"$filter","$scope" , "$http","$window",
    function(mainService, $filter, $scope, $http ,  $window) {
        $scope.course = {

        }
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
            $scope.course.coverssAddress = coversimagedata;
            $scope.course.colImgAddress = colimagedata;
            $scope.course.courseImg = coursePhoto.join("&&");

            var data = {};
            $scope.courseTypes = [];
            data=  mainService.doSave($scope.course).then(function(result) {
                    var success = result.success;
                    data = result;
                    callBack(result);
                    return result;
                },
                function(error){
                    alert("保存失败，请与管理员联系！");
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
        }

        $scope.findByIdForEdit = function(){
            mainService.findByIdForEdit(id).then(function(result) {
                    var success = result.success;
                    $scope.course = result.data
                    $scope.coverssAddressImgSrc = result.data.coverssAddress;
                    $scope.course.startTime = result.data.startTime;
                    $scope.colImageAddressImgSrc = result.data.colImgAddress;
                    $scope.pushImgSrc = result.data.pushContent;

                    var pushType = result.data.pushType;
                    if(pushType==1)
                    {
                        $("#word").show();
                        $("#image").hide();
                        $("#pushImgSrc").hide();
                    }else if(pushType==2){
                        $("#word").hide();
                        $("#image").show();
                        $("#pushImgSrc").show();
                    }else{
                        $("#word").hide();
                        $("#image").hide();
                        $("#pushImgSrc").hide();
                    }

                },
                function(error){
                    jbox_notice({content: "获取课程信息失败！", autoClose: 2000, className: "error"});
                });
        },
         $scope.getCourseImglist = function(){
         mainService.getCourseImglist(id).then(function(result) {
         $scope.courseImglist = result.data;
             var courseImgList = result.data;//课程图片简介
             if (courseImgList.length > 0) {//遍历简介图片
                 $.each(courseImgList, function (i, n) {
                     coursePhoto.push(n.address);
                     var html = "";
                     html += '<div class="picss">';
                     html += '<img class="pic" src="' + n.address + '"/>'
                     html += '<span class="closes" onclick="clocs(this)">X</span>'
                     html += "</div>";
                     $(".add_imgs").before(html);
                 });
                 uploadCoursePhoto(coursePhoto.join("&&"));
             }
         },
         function(error){
         jbox_notice({content: "获取介绍图片失败！", autoClose: 2000, className: "error"});
         });
         }
         
        $scope.setDate = function(obj){
            $scope.course.startTime =  new Date($(obj).val()).getTime();
        }

        $scope.getCoursesType = function(){
            mainService.getAllCourseType().then(function(result) {
                    $scope.courseTypes = result.data;
                },
                function(error){
                    alert("获取课程类型失败！");
                });
        };
        $scope.getCoursesType();
        $scope.getCourseImglist();
        if(id > 0){
            $scope.findByIdForEdit();
        }


    }
])
login.service("mainService",function($http , $q){
    return {
        doSave : function(course){
            console.log(course)
            return $http.post('/course/doUpdate', course).then(function(response) {
                return response.data;
            });
        }
        ,
        findByIdForEdit : function(id){
            return $http.get('/course/findByIdForEdit?id=' + id).then(function(response) {
                return response.data;
            });
        }  ,
        getCourseImglist : function(id){
         return $http.get('/course/getCourseImglist?id=' + id).then(function(response) {
         return response.data;
         });
         },
        getAllCourseType:function () {
            return $http.get('/courseType/getCourseTypeListBySORT').then(function (response) {
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
