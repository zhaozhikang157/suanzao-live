var login = angular.module("dataChargeLevel", []);
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
            $scope.dataChargeLevel = {};
            $scope.sub = function (callBack) {
                if ($("form").valid(null, "error!") == false) {
                    return false;
                }
                $scope.dataChargeLevel.invalidDateUnit =  $('#invalidDateUnit').val();
                var isRetail =$("input[name='isRetail']:checked").val()
                var isHot= $("input[name='isHot']:checked").val();
             
                if(isRetail=="1" && isHot=="1"){
                    alert("热门和零售只能选择一种！");
                    return;
                }
                var amount = $("#amount").val();
                if(!isPositiveNum(amount)){
                    alert("流量必须为正整数")
                    return;
                }
                
                $scope.dataChargeLevel.isRetail =  isRetail;
                $scope.dataChargeLevel.isHot = isHot;
                var data = {};
                data = mainService.doSave($scope.dataChargeLevel).then(function (result) {
                        var success = result.success;
                        data = result;
                        callBack(result);
                        return result;
                    },
                    function (error) {
                        jbox_notice({content: "保存失败，请与管理员联系！", autoClose: 2000, className: "error"});
                    });
            };
            function isPositiveNum(s){//是否为正整数  
                var re = /^[0-9]*[1-9][0-9]*$/ ;
                return re.test(s)
            }
            $scope.getId = function () {
                mainService.getId(id).then(function (result) {
                        var success = result.success;
                        $scope.dataChargeLevel = result.data;
                        $("#invalidDateUnit").val($scope.dataChargeLevel.invalidDateUnit);

                        var muchs =  $scope.dataChargeLevel.isRetail;

                        if(muchs=="1"){
                            $(".muchs").show();
                        }else{
                            $(".muchs").hide();
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
    ]);
function isPositiveInteger(s){//是否为正整数
    var re = /^[0-9]+$/ ;
    return re.test(s)
}
login.service("mainService", function ($http, $q) {
    return {
        doSave: function (dataChargeLevel) {
            return $http.post('/dataChargeLevel/doSaveAndUpdate', dataChargeLevel).then(function (response) {
                return response.data;
            });
        },
        getId: function (id) {
            return $http.post('/dataChargeLevel/findById?id=' + id).then(function (response) {
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
