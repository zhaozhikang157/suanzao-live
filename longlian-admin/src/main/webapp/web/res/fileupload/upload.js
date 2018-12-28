
/**
 * 
 * @param ctrlId
 * @param filetypes 不限制
 * @param maxSize 可以不传为：5M
 * @param callback 
 * @param progressCallBack
 */
function bindFileUpload(ctrlId , filetypes , maxSize , callback , progressCallBack , startCallBack , params ) {
	$('#' + ctrlId).prop("multiple");
	var size = maxSize ? maxSize : 1024 * 1024 * 5;
	$('#' + ctrlId).fileupload({
        dataType: 'json',
        url :'/file/upload' + ( params ? "?" + params : ""),
        acceptFileTypes:filetypes ? filetypes :/.*$/,
        maxFileSize:  size,
        done: function (e, data) {
        	if (callback) {
        		callback(data , ctrlId);
        	} 
        },
    	progressall: function (e, data) {
    		if (progressCallBack)
    			progressCallBack(data);
    	} ,
    	start: function (e, data) {
    		if (startCallBack)
    			startCallBack(data , ctrlId);
    	}
    }).on('fileuploadprocessalways', function (e, data) {
        var currentFile = data.files[data.index];
        if (data.files.error && currentFile.error) {
 		   if (window.jbox_notice) {
 			   jbox_notice({content: currentFile.error, autoClose: 2000, className: "error"});
 		   } else {
 			   alert(currentFile.error );
 		   }
        }
      });
}

/**
 * 图片上传
 * @param ctrlId
 * @param maxSize 大小限制 默认为：5M
 * @param callback 上传回调
 * @param progressCallBack 进度回调
 */
function bindImageUpload(ctrlId  , maxSize , callback , progressCallBack, startCallBack ) {
	bindFileUpload(ctrlId , /(\.|\/)(gif|jpe?g|png)$/i , maxSize , callback , progressCallBack , startCallBack );
}
/**
 * 
 * @param ctrlId
 * @param filetypes
 * @param maxSize
 * @param callback
 * @param progressCallBack
 */
function bindDefaultUpload(ctrlId , filetypes , maxSize , callback , progressCallBack, startCallBack) {
	bindFileUpload(ctrlId , "" , "" , callback , progressCallBack , startCallBack )
}
