
JQuery.extend({
	

    createUploadIframe: function(id, uri)
	{
			//create frame
            var frameId = 'jUploadFrame' + id;
            var iframeHtml = '<iframe id="' + frameId + '" name="' + frameId + '" style="position:absolute; top:-9999px; left:-9999px"';
			if(window.ActiveXObject)
			{
                if(typeof uri== 'boolean'){
					iframeHtml += ' src="' + 'javascript:false' + '"';

                }
                else if(typeof uri== 'string'){
					iframeHtml += ' src="' + uri + '"';

                }	
			}
			iframeHtml += ' />';
			JQuery(iframeHtml).appendTo(document.body);

            return JQuery('#' + frameId).get(0);
    },
    createUploadForm: function(id, fileElementId, data)
	{
		//create form
		var formId = 'jUploadForm' + id;
		var fileId = 'jUploadFile' + id;
		var form = JQuery('<form  action="" method="POST" name="' + formId + '" id="' + formId + '" enctype="multipart/form-data"></form>');
		if(data)
		{
			for(var i in data)
			{
				JQuery('<input type="hidden" name="' + i + '" value="' + data[i] + '" />').appendTo(form);
			}
		}
		var oldElement = JQuery('#' + fileElementId);
		var newElement = JQuery(oldElement).clone();
		JQuery(oldElement).attr('id', fileId);
		JQuery(oldElement).before(newElement);
		JQuery(oldElement).appendTo(form);



		//set attributes
		JQuery(form).css('position', 'absolute');
		JQuery(form).css('top', '-1200px');
		JQuery(form).css('left', '-1200px');
		JQuery(form).appendTo('body');
		return form;
    },

    ajaxFileUpload: function(s) {
        // TODO introduce global settings, allowing the client to modify them for all requests, not only timeout
        s = JQuery.extend({}, JQuery.ajaxSettings, s);
        var id = new Date().getTime()
		var form = JQuery.createUploadForm(id, s.fileElementId, (typeof(s.data)=='undefined'?false:s.data));
		var io = JQuery.createUploadIframe(id, s.secureuri);
		var frameId = 'jUploadFrame' + id;
		var formId = 'jUploadForm' + id;
        // Watch for a new set of requests
        if ( s.global && ! JQuery.active++ )
		{
			JQuery.event.trigger( "ajaxStart" );
		}
        var requestDone = false;
        // Create the request object
        var xml = {}
        if ( s.global )
            JQuery.event.trigger("ajaxSend", [xml, s]);
        // Wait for a response to come back
        var uploadCallback = function(isTimeout)
		{
			var io = document.getElementById(frameId);
            try
			{
				if(io.contentWindow)
				{
					 xml.responseText = io.contentWindow.document.body?io.contentWindow.document.body.innerText:null;
                	 xml.responseXML = io.contentWindow.document.XMLDocument?io.contentWindow.document.XMLDocument:io.contentWindow.document;

				}else if(io.contentDocument)
				{
					 xml.responseText = io.contentDocument.document.body?io.contentDocument.document.body.innerHTML:null;
                	xml.responseXML = io.contentDocument.document.XMLDocument?io.contentDocument.document.XMLDocument:io.contentDocument.document;
				}
            }catch(e)
			{
				JQuery.handleError(s, xml, null, e);
			}
            if ( xml || isTimeout == "timeout")
			{
                requestDone = true;
                var status;
                try {
                    status = isTimeout != "timeout" ? "success" : "error";
                    // Make sure that the request was successful or notmodified
                    if ( status != "error" )
					{
                        // process the data (runs the xml through httpData regardless of callback)
                        var data = JQuery.uploadHttpData( xml, s.dataType );
                        // If a local callback was specified, fire it and pass it the data
                        if ( s.success )
                            s.success( data, status );

                        // Fire the global callback
                        if( s.global )
                            JQuery.event.trigger( "ajaxSuccess", [xml, s] );
                    } else
                        JQuery.handleError(s, xml, status);
                } catch(e)
				{
                    status = "error";
                    JQuery.handleError(s, xml, status, e);
                }

                // The request was completed
                if( s.global )
                    JQuery.event.trigger( "ajaxComplete", [xml, s] );

                // Handle the global AJAX counter
                if ( s.global && ! --JQuery.active )
                    JQuery.event.trigger( "ajaxStop" );

                // Process result
                if ( s.complete )
                    s.complete(xml, status);

                JQuery(io).unbind()

                setTimeout(function()
									{	try
										{
											JQuery(io).remove();
											JQuery(form).remove();

										} catch(e)
										{
											JQuery.handleError(s, xml, null, e);
										}

									}, 100)

                xml = null

            }
        }
        // Timeout checker
        if ( s.timeout > 0 )
		{
            setTimeout(function(){
                // Check to see if the request is still happening
                if( !requestDone ) uploadCallback( "timeout" );
            }, s.timeout);
        }
        try
		{

			var form = JQuery('#' + formId);
			JQuery(form).attr('action', s.url);
			JQuery(form).attr('method', 'POST');
			JQuery(form).attr('target', frameId);
            if(form.encoding)
			{
				JQuery(form).attr('encoding', 'multipart/form-data');
            }
            else
			{
				JQuery(form).attr('enctype', 'multipart/form-data');
            }
            JQuery(form).submit();

        } catch(e)
		{
            JQuery.handleError(s, xml, null, e);
        }

		JQuery('#' + frameId).load(uploadCallback	);
        return {abort: function () {}};

    },

    uploadHttpData: function( r, type ) {
        var data = !type;
        data = type == "xml" || data ? r.responseXML : r.responseText;
        // If the type is "script", eval it in global context
        if ( type == "script" )
            JQuery.globalEval( data );
        // Get the JavaScript object, if JSON is used.
        if ( type == "json" )
            eval( "data = " + data );
        // evaluate scripts within html
        if ( type == "html" )
            JQuery("<div>").html(data).evalScripts();

        return data;
    }
})

