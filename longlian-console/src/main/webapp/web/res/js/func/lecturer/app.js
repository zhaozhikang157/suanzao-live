var login = angular.module("lecturer", []);
var config = ["$httpProvider", function ($httpProvider) {
    if (!$httpProvider.defaults.headers.get) {
        $httpProvider.defaults.headers.get = {};
    }
    $httpProvider.defaults.headers.common["X-Requested-With"] = 'XMLHttpRequest';
    $httpProvider.defaults.headers.get['Cache-Control'] = 'no-cache';
    $httpProvider.defaults.headers.get['Pragma'] = 'no-cache';
}];
login.config(config);
login.controller("task",
    ["mainService", "$filter", "$scope", "$http", "$window",
        function (mainService, $filter, $scope, $http, $window) {
            $scope.lecturer = {
                name: "",
                sex: "0",
                photo: "",
                honor: "",
                introduction: "",
                goodField:"",     //擅长领域
                videoAddress:"",  //宣传视频
                videoCoverAddress:""    //视频封面
            };
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                var data = {};
                if($scope.lecturer.honor.length >10){
                	jbox_notice({content: "头衔最多输入10个字", autoClose: 2000, className: "error"});
                	return;
                }
                if($scope.lecturer.goodField.length >20){
                	jbox_notice({content: "擅长领域最多20个字", autoClose: 2000, className: "error"});
                	return;
                }
                if($scope.lecturer.photo ==""){
                	jbox_notice({content: "请选择头像！", autoClose: 2000, className: "error"});
                	return;
                }
               /* if($scope.lecturer.videoAddress ==""){
                	jbox_notice({content: "请选择宣传视频！", autoClose: 2000, className: "error"});
                	return;
                }*/
                if($scope.lecturer.videoCoverAddress ==""){
                	jbox_notice({content: "请选择视频封面！", autoClose: 2000, className: "error"});
                	return;
                }
                
                
                data = mainService.doSave($scope.lecturer).then(function (result) {
                        var success = result.success;
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            $scope.setPicAddress = function(photo) {
                $scope.lecturer.photo = photo;
            };
            $scope.setVideoAddress = function(videoAddress) {
                $scope.lecturer.videoAddress = videoAddress;
            };
            $scope.setVideoCoverAddress = function(videoCoverAddress) {
                $scope.lecturer.videoCoverAddress = videoCoverAddress;
            };
            $scope.getId = function () {
                mainService.getId(id).then(function (result) {
                        var success = result.success;
                        $scope.lecturer = result.data;
                        $scope.imgSrc = result.data.photo;

                        if (result.data.photo != '' && result.data.photo != null) {
                            //编辑时，显示已上传图片
                            $("#src").css('display', 'block');
                            $("#tou").attr("ng-src", result.data.photo);
                        }
                        /*宣传视频*/
                        if (result.data.videoAddress != '' && result.data.videoAddress != null) {
                            //编辑时，显示已上传图片
                            $("#video_src").css('display', 'block');
                            $("#video_imgSrc").text(result.data.videoAddress);
                        }
                        /*宣传视频封面*/
                        if (result.data.videoCoverAddress != '' && result.data.videoCoverAddress != null) {
                            //编辑时，显示已上传图片
                            $("#videoCover_src").css('display', 'block');
                            $("#videoCover_imgSrc").attr("src",result.data.videoCoverAddress);
                        }

                    },
                    function (error) {
                        jbox_notice({content: "数据回显失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };

            if (id > 0) {
                $scope.getId();
            }
        }
    ])

login.service("mainService", function ($http, $q) {
    return {
        doSave: function (lecturer) {
            return $http.post('/lecturer/doSaveAndUpdate', lecturer).then(function (response) {
                return response.data;
            });
        },
        getId: function (id) {
            return $http.get('/lecturer/findById?id=' + id).then(function (response) {
                return response.data;
            });
        }
    }
});

function doSave(callback) {
    var appElement = document.querySelector('[ng-controller=task]');
    var $scope = angular.element(appElement).scope();
    var json = $scope.sub(function (json) {
        callback(json);
    });
}
