package com.huaxin.util;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.digest.Md5Crypt;


/**
 * @author
 * 
 */
public class EmailUtil {
    
	private static BlockingQueue<Map> sendQueue = new LinkedBlockingQueue<Map>(20000);
	
	private static Set<String> hasSend = new HashSet<String>();
	private final static int keepSend = 120;
	
	private boolean isSendEmail = true;
    /**
     * 设置isSendEmail=false关闭 isSendEmail=true开启
     * @param isSendEmail
     * @throws Exception 
     */
    public void setSendEmail(boolean isSendEmail ) throws Exception {
        this.isSendEmail = isSendEmail;
    }
	
	private static EmailUtil _instance = null;
    
	public static EmailUtil getInstance() {
		if (_instance == null)
			_instance = new EmailUtil();
		return _instance;
	}
    
	private EmailUtil() {
		new Thread(new Runnable() {
			public void run() {
				while (isSendEmail) {
					Map mail = null;
					try {
						mail = sendQueue.take();
						if (mail != null) {
							Set<String> file = (Set<String>) mail.get("file");
							if(file!=null&&!file.isEmpty()){
								realSend((String) mail.get("title"), (String) mail
										.get("content"),file, (String[]) mail
										.get("receivers"));
							}else{
								realSend((String) mail.get("title"), (String) mail
										.get("content"), (String[]) mail.get("receivers"));
							}

						}
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}).start();

		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(keepSend*1000l);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				while (true) {
					hasSend.clear();
					try {
						Thread.sleep(keepSend*1000l);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	
	
	public void send(String title, String content, String... receivers) {
		Map map = new HashMap();
		map.put("title", title);
		map.put("content", content);
		map.put("receivers", receivers);
		String md5=Md5Crypt.md5Crypt(content.getBytes());
		if(!hasSend.contains(md5)){
			hasSend.add(md5);
			sendQueue.add(map);
		}
		
	}
	
	public void send(String title,String content,Set<String> file,String... receivers) {
		Map map = new HashMap();
		map.put("title", title);
		map.put("content", content);
		map.put("receivers", receivers);
		map.put("file", file);
		sendQueue.add(map);
	}
	/*public static void main(String args[]){
		realSend("aa","bb",null,new String[]{"zhangzhipeng@suanzao.com.cn"});
	}*/
	private static void realSend(String title, String content,
			Set<String> file, String... receivers) {
		//String user = "liuhan@suanzao.com.cn";
		String user = "zhangzhipeng@suanzao.com.cn";
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.ym.163.com");
		props.put("mail.smtp.auth", "true");
		Session s = Session.getInstance(props);
		Address[] tos = null;
		MimeMessage message = new MimeMessage(s);
		try {
			InternetAddress from = new InternetAddress(user);
			message.setFrom(from);
			message.setSubject(title);
			message.setSentDate(new Date());
			if (receivers != null) {

				tos = new InternetAddress[receivers.length];
				for (int i = 0; i < receivers.length; i++) {
					String toname = receivers[i];
					tos[i] = new InternetAddress(toname);
				}
			}
			
			message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(user)});
			message.setRecipients(Message.RecipientType.CC, tos);
            
			
			BodyPart mdp = new MimeBodyPart();
			mdp.setContent(content, "text/html;charset=utf-8");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mdp);
			
			if (file!=null&&!file.isEmpty()) {
				Iterator<String> it=file.iterator();
				while (it.hasNext()) {
					mdp = new MimeBodyPart();
					String filename =it.next();
					FileDataSource fds = new FileDataSource(filename);
					mdp.setDataHandler(new DataHandler(fds));

					mdp.setFileName(MimeUtility.encodeText(fds.getName()));
					multipart.addBodyPart(mdp);
				}
				
			}
			message.setContent(multipart);
			message.saveChanges();
			Transport transport = s.getTransport("smtp");
//			transport.connect("smtp.163.com", user,
//					"longlian369");
			//transport.connect("smtp.ym.163.com", user,"861016");
			transport.connect("smtp.ym.163.com", user,"123456");
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void realSend(String title, String content,
			String... receivers) {
		realSend(title,content,null,receivers);
	}
	
}
