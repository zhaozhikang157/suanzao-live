package com.huaxin.util.weixin.ParamesAPI;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.huaxin.util.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;

public class HttpRequestUtil {


	/**
	 * 发起https请求并获取结果
	 *
	 * @param request
	 *            请求地址
	 * @param RequestMethod
	 *            请求方式（GET、POST）
	 * @param output
	 *            提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest(String request, String RequestMethod, String output) {
		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 建立连接
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setRequestMethod(RequestMethod);
			if (output != null) {
				//conn.setRequestProperty("Content-Type", "plain/text; charset=UTF-8");
				connection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
				OutputStream out = connection.getOutputStream();
				out.write(output.getBytes("UTF-8"));
				out.flush();
				out.close();
			}
			// 流处理
			InputStream input = connection.getInputStream();
			InputStreamReader inputReader = new InputStreamReader(input, "UTF-8");
			BufferedReader reader = new BufferedReader(inputReader);
			String line;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			// 关闭连接、释放资源
			reader.close();
			inputReader.close();
			input.close();
			input = null;
			connection.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}


	/**
	 * 发起https请求并获取结果
	 * @param request 请求地址
	 * @param filePath 提交的文件路径
	 * @param fileType type
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequestFile(String request, String filePath , String fileType) {
		JSONObject jsonObject = null;
		// 创建SSLContext对象，并使用我们指定的信任管理器初始化
		TrustManager[] tm = { new MyX509TrustManager() };
		//安全套接字的上下文
		SSLContext sslContext;
		StringBuffer buffer = new StringBuffer();

		// 输入流来读取URL的响应
		BufferedReader reader = null;
		String result = null;
		String fileName = "default.jpg";
		File file = null;
		InputStream  input = null;
		try {
			if(filePath.startsWith("http")){//远程
				URL Fileurl = new URL(filePath);
				HttpURLConnection conn = (HttpURLConnection) Fileurl.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5 * 1000);
				input = conn.getInputStream();// 通过输入流获取图片数据
				fileName = filePath.substring(filePath.lastIndexOf("/")+1);
			}else{
				file = new File(filePath);//本地
				fileName = file.getName();
				input = new DataInputStream(new FileInputStream(file));
			}
			sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new java.security.SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();
			// 建立连接
			URL url = new URL(request);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false); // post方式不能使用缓存
			// 设置请求头信息
			connection.setRequestProperty("Connection", "Keep-Alive");
			connection.setRequestProperty("Charset", "UTF-8");
			// 设置边界
			String boundary = "-----------------------------"+System.currentTimeMillis();
			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);
			StringBuffer sbuff=new StringBuffer();
			sbuff.append("--").append(boundary).append("\r\n");
			sbuff.append("Content-Disposition: form-data;name=\"type\" \r\n\r\n");
			sbuff.append(fileType);
			sbuff.append("\r\n--").append(boundary).append("\r\n");
			//在上传视频素材时需要POST另一个表单，id为description
			if("video/mp4".equals(fileType)){
				sbuff.append("Content-Disposition: form-data;name=\"description\"\r\n\r\n");
				sbuff.append("{\"title\":\"hello title\", \"introduction\":\"hello introduction\"}");
				sbuff.append("\r\n--").append(boundary).append("\r\n");
			}
			sbuff.append("Content-Disposition: form-data;name=\"media\";filename=\"" + fileName + "\" \r\n");
			sbuff.append("Content-Type:application/octet-stream\r\n\r\n");
			System.out.println(sbuff.toString());

			byte[] head = sbuff.toString().getBytes("utf-8");
			// 获得输出流
			OutputStream output = new DataOutputStream(connection.getOutputStream());
            // 输出表头
			output.write(head);
            // 文件正文部分
			// 把文件已流文件的方式 推入到url中
			byte[] data = new byte[1024];
			int len =0;
			while((len=input.read(data))>-1){
				output.write(data, 0, len);
			}
			input.close();
			// 结尾部分
			byte[] foot = ("\r\n--" + boundary + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
			output.write(foot);
			output.flush();
			output.close();

			// 定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
			jsonObject = JSONObject.fromObject(buffer.toString());
		} catch (Exception e) {
			System.out.println("发送POST请求出现异常！" + e);
			e.printStackTrace();
		}finally {

		}
		return jsonObject;
	}

	/**
	 * 发起https请求并获取结果
	 * 
	 * @param requestUrl 请求地址
	 * @param requestMethod 请求方式（GET、POST）
	 * @param outputStr 提交的数据
	 * @return JSONObject(通过JSONObject.get(key)的方式获取json对象的属性值)
	 */
	public static JSONObject httpRequest2(String requestUrl, String requestMethod, String outputStr) {

		System.err.println(requestMethod+"\toutputStr="+outputStr);

		JSONObject jsonObject = null;
		StringBuffer buffer = new StringBuffer();
		try {
			// 创建SSLContext对象，并使用我们指定的信任管理器初始化
			TrustManager[] tm = { new MyX509TrustManager() };
			SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
			sslContext.init(null, tm, new SecureRandom());
			// 从上述SSLContext对象中得到SSLSocketFactory对象
			SSLSocketFactory ssf = sslContext.getSocketFactory();

			URL url = new URL(requestUrl);
			HttpsURLConnection httpUrlConn = (HttpsURLConnection) url.openConnection();
			httpUrlConn.setSSLSocketFactory(ssf);

			httpUrlConn.setDoOutput(true);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if ("GET".equalsIgnoreCase(requestMethod))
				httpUrlConn.connect();

			// 当有数据需要提交时
			if (null != outputStr) {
				httpUrlConn.connect();
				OutputStream outputStream = httpUrlConn.getOutputStream();
				// 注意编码格式，防止中文乱码
				outputStream.write(outputStr.getBytes("UTF-8"));
				outputStream.close();
			}

			// 将返回的输入流转换成字符串
			InputStream inputStream = httpUrlConn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

			String str = null;
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
			}
			bufferedReader.close();
			inputStreamReader.close();
			// 释放资源
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
			jsonObject = JSONObject.fromObject(buffer.toString());
			//System.out.println("jsonObject="+jsonObject);
		} catch (ConnectException ce) {
			ce.printStackTrace();
			System.out.println("网络链接失败！");
		}catch (UnknownHostException uhe) {
			uhe.printStackTrace();
			System.out.println("微信API无法访问....！");
			//httpRequest(requestUrl, requestMethod, outputStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonObject;
	}
	
	/**
	 * 发起https请求并获取字节数组结果
	 * @param requestUrl
	 * @param requestMethod
	 * @param data
	 * @return
	 */
	public static byte[] httpRequest_byte(String requestUrl, String requestMethod, byte[] data) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			if (requestMethod == EnumMethod.GET.name() && data != null && data.length > 0) {
				if (requestUrl.indexOf('?') > 0) {
					requestUrl += '&';
				} else {
					requestUrl += '?';
				}
				requestUrl += new String(data);
			}
			URL url = new URL(requestUrl);
			HttpURLConnection httpUrlConn = (HttpURLConnection) url.openConnection();
			if (httpUrlConn instanceof HttpsURLConnection) {
				// 创建SSLContext对象，并使用我们指定的信任管理器初始化
				TrustManager[] tm = { new MyX509TrustManager() };
				SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
				sslContext.init(null, tm, new SecureRandom());
				// 从上述SSLContext对象中得到SSLSocketFactory对象
				SSLSocketFactory ssf = sslContext.getSocketFactory();
				((HttpsURLConnection) httpUrlConn).setSSLSocketFactory(ssf);
			}
			boolean truePost = requestMethod == EnumMethod.POST.name() && data != null && data.length > 0;
			httpUrlConn.setDoOutput(truePost);
			httpUrlConn.setDoInput(true);
			httpUrlConn.setUseCaches(false);
			// 设置请求方式（GET/POST）
			httpUrlConn.setRequestMethod(requestMethod);

			if (requestMethod == EnumMethod.GET.name()) {
				httpUrlConn.connect();
			} else if (truePost) {
				// 提交数据
				OutputStream outputStream = httpUrlConn.getOutputStream();
				outputStream.write(data);
				outputStream.close();
			}

			// 读取返回数据
			InputStream inputStream = httpUrlConn.getInputStream();
			byte[] buf = new byte[1024 * 1024];
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				out.write(buf, 0, len);
			}
			// 释放资源
			out.close();
			inputStream.close();
			inputStream = null;
			httpUrlConn.disconnect();
		} catch (ConnectException ce) {
		} catch (Exception e) {
		}
		return out.toByteArray();
	}

	/**
	 * 根据地址，下载文件，以及文件类型
	 * @param requestUrl
	 * @return
	 */
	public static Map getFileUploadMap(String requestUrl){
		HttpURLConnection conn = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		Map result = new HashMap();
		try {
			URL url = new URL(requestUrl);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setRequestMethod("GET");
			conn.connect();
			InputStream input = conn.getInputStream();
			System.out.println(conn.getResponseMessage());
			System.out.print(conn.getContentType());
			if("text/plain".equals(conn.getContentType())){
				StringBuffer buffer = new StringBuffer();
				// 将返回的输入流转换成字符串
				InputStreamReader inputStreamReader = new InputStreamReader(input, "utf-8");
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String str = null;
				while ((str = bufferedReader.readLine()) != null) {
					buffer.append(str);
				}
				bufferedReader.close();
				JSONObject jsonObject = JSONObject.fromObject(buffer.toString());
				result.put("contentType" , conn.getContentType());
				result.put("object" , jsonObject);
				return  result;
			}else{
				byte[] buf = new byte[1024 * 1024];
				int len;
				while ((len = input.read(buf)) != -1) {
					out.write(buf, 0, len);
				}
				result.put("contentType" , conn.getContentType());
				result.put("object" ,  out.toByteArray());
				return  result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 释放资源
			try {
				out.close();
				conn.disconnect();
			}catch (Exception e){
				e.printStackTrace();
			}
		}
		return  null;
	}

	/**
	 * 获取文件流
	 * @param requestUrl
	 * @return
	 */
	public static Object getFileUploadStream(String requestUrl) {
		Map result = getFileUploadMap(requestUrl);
		if (result == null) return result;
		 return result.get("object");
	}

	/**
	 *
	 * @param url
	 * @param fileName
	 * @param type audio/amr
	 * @throws Exception
	 */
	public static Map httpsClientPost(String url  , String fileName  ) throws Exception {

		// 获得utf-8编码的mbuilder
		MultipartEntityBuilder mBuilder = get_COMPATIBLE_Builder("UTF-8");
		/**
		 * 原生的微信使用的url是https://api.weixin.qq.com/cgi-bin/media/upload?
		 * access_token=##ACCESS_TOKEN##&type=##TYPE##
		 * 一般都会使用这个把参数直接携带在url中。我个人不喜欢这样，因为既然使用了httpclient，完全可以把参数
		 * 设置在我们的body体中。所以我们使用的url是这样的
		 * https://api.weixin.qq.com/cgi-bin/media/upload 然后通过在body体中设置参数来设置
		 * access_token和type这两个字段
		 *
		 * */
		// 设置type，我这里用一个缩略图来做实验，所以type是thumb
		//mBuilder.addTextBody("type", type);
		// 设置access_token，
		//mBuilder.addTextBody("access_token", accessToken);
		// 这里就是我要上传到服务器的多媒体图片
		mBuilder.addBinaryBody("media", getFile(fileName),
				ContentType.APPLICATION_OCTET_STREAM, getFile(fileName)
						.getName());
		// 建造我们的http多媒体对象
		HttpEntity he = mBuilder.build();
		// 建立一个sslcontext，这里我们信任任何的证书。
		SSLContext context = getTrustAllSSLContext();
		// 建立socket工厂
		SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(
				context);
		// 建立连接器
		CloseableHttpClient client = HttpClients.custom()
				.setSSLSocketFactory(factory).build();
		try {
			// 得到一个post请求的实体
			HttpPost post = getMultipartPost(url);
			// 给请求添加参数
			post.setEntity(he);
			// 执行请求并获得结果
			CloseableHttpResponse reponse = client.execute(post);
			try {
				// 获得返回的内容
				HttpEntity entity = reponse.getEntity();
				// 输出
				String jsonStr = EntityUtils.toString(entity);
				System.out.println(jsonStr);
				Map msg = (Map) JsonUtil.getObject(jsonStr, HashMap.class);
				return msg;
				// 消耗实体
			} finally {
				// 关闭返回的reponse
				reponse.close();
			}
		} finally {
			// 关闭client
			client.close();
		}

	}

	private static String getBoundaryStr(String str) {
		return "------------" + str;
	}

	private static File getFile(String path) {
		return new File(path);
	}

	private static MultipartEntityBuilder get_COMPATIBLE_Builder(String charSet) {
		MultipartEntityBuilder result = MultipartEntityBuilder.create();
		result.setBoundary(getBoundaryStr("7da2e536604c8"))
				.setCharset(Charset.forName(charSet))
				.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		return result;
	}



	private static HttpPost getMultipartPost(String url) {
    /* 这里设置一些post的头部信息，具体求百度吧 */
		HttpPost post = new HttpPost(url);
		post.addHeader("Connection", "keep-alive");
		post.addHeader("Accept", "*/*");
		post.addHeader("Content-Type", "multipart/form-data;boundary="
				+ getBoundaryStr("7da2e536604c8"));
		post.addHeader("User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
		return post;
	}

	private static SSLContext getTrustAllSSLContext() throws Exception {
		SSLContext context = SSLContexts.custom()
				.loadTrustMaterial(null, new TrustStrategy() {
					@Override
					public boolean isTrusted(X509Certificate[] arg0, String arg1)
							throws CertificateException {
						// 这一句就是信任任何的证书，当然你也可以去验证微信服务器的真实性
						return true;
					}
				}).build();
		return context;
	}
}
