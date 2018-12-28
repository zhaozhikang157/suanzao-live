function doInit(){
    selectTree(0);
    $('#table-bootstrap').bootstrapTable({
        method: 'get',
        contentType: "application/x-www-form-urlencoded;charset=UTF-8",
        url: "/mRes/findMenuAllRole",
        classes: 'table table-hover',
        height: getHeight(),
        toolbar: "#toolbar",
        pageSize: 10,
        queryParams: queryParams,
        columns: [
            {
                field: 'name',
                title: '角色',
                align: 'center',
                valign: 'middle'
            },
            {
                field: 'status',
                title: '状态',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                    if (value == "0") {
                        return "<span style='color:green'>正常</span>"
                    } else {
                        return "<span style='color:red'>禁用</span>"
                    }
                }
            },
            {
                field: '_opt_',
                title: '设置资源',
                align: 'center',
                valign: 'middle',
                formatter: function (value, row) {
                	var str;
                	if(row.status == "0"){
                		str = '<a class="like" href="javascript:void(0)"  title="菜单设置" onclick="findSelectedRes(' + row.id + ');">---></a>  '
                	}else{
                		str = '<div style="color:#AAAAAA" class="like" title="菜单设置">---></div>  '
                	}
                	 return str;
                         
                }
            }]
    });
}
    $(document).on("click",".like",function(){
       $(this).parents("tr").addClass("on").siblings().removeClass("on");
    });
function getHeight() {
    return $(window).height() - $('h2').outerHeight(true);
}
function queryParams(params) {
    var name = $("input[name='name']").val();
    if (!params) {
        params = {};
    }
    params["name"] = name;
    return params;
}

function findSelectedRes(id){
    selectTree(id);
}