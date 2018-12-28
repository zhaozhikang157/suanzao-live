<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<title>jQuery File Upload Example</title>
<style type="text/css">
.bar {
    height: 18px;
    background: green;
}
</style>
</head>
<body>
<img class="Img" src="" height="50px" width="150px"/><BR>
<div id="progress">
    <div class="bar" style="width: 0%;"></div>
</div>
<input id="fileupload" type="file" name="files[]" >
<script src="${requestContext.contextPath}/web/res/jquery/jquery.min.js"></script>
<script src="${requestContext.contextPath}/web/res/fileupload/js/vendor/jquery.ui.widget.js"></script>
<script src="${requestContext.contextPath}/web/res/fileupload/js/jquery.iframe-transport.js"></script>
<script src="${requestContext.contextPath}/web/res/fileupload/js/jquery.fileupload.js"></script>
<script src="${requestContext.contextPath}/web/res/fileupload/js/jquery.fileupload-process.js"></script>
<script src="${requestContext.contextPath}/web/res/fileupload/js/jquery.fileupload-validate.js"></script>
<script src="${requestContext.contextPath}/web/res/fileupload/upload.js"></script>
<script>


function myCallBack(data) {
	data = data.result;
	if (data.success) {
	 	$('.Img').attr("src" , data.data.url);
	} else {
		alert(data.msg);
	}
	$('#progress').hide();
}
function progressCallBack(data) {
	$('#progress').show();
    var progress = parseInt(data.loaded / data.total * 100, 10);
    $('#progress .bar').css(
        'width',
        progress + '%'
    );
}

$(function () {
	bindImageUpload("fileupload" , 5 *1024 *1024 , myCallBack  )
	//bindFileUpload("fileupload" , "" , 7 *1024 *1024 , myCallBack , progressCallBack ) ;
});
</script>
</body> 
</html>