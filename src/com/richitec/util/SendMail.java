package com.richitec.util;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMail {
	private String host = "smtp.exmail.qq.com"; // smtp服务器
	private String user = "noreply@00244dh.com"; // 用户名
	private String pwd = "uutalk123"; // 密码
	private String from = "noreply@00244dh.com"; // 发件人地址
	private String to = ""; // 收件人地址
	private String subject = ""; // 邮件标题
	private String content = ""; // 邮件内容

	public void setAddress(String to, String subject, String content) {
		this.to = to;
		this.subject = subject;
		this.content = content;
	}

	public void send() throws AddressException, MessagingException {
		Properties props = new Properties();
		// 设置发送邮件的邮件服务器的属性（这里使用网易的smtp服务器）
		props.put("mail.smtp.host", host);
		// 需要经过授权，也就是有户名和密码的校验，这样才能通过验证（一定要有这一条）
		props.put("mail.smtp.auth", "true");
		// 用刚刚设置好的props对象构建一个session
		Session session = Session.getDefaultInstance(props);
		// 有了这句便可以在发送邮件的过程中在console处显示过程信息，供调试使
		// 用（你可以在控制台（console)上看到发送邮件的过程）
		session.setDebug(true);
		// 用session为参数定义消息对象
		MimeMessage message = new MimeMessage(session);
//		try {
			// 加载发件人地址
			message.setFrom(new InternetAddress(from));
			// 加载收件人地址
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
			// 加载标题
			message.setSubject(subject);

			// 设置邮件的文本内容
			message.setContent(content, "text/html;charset=gb2312");
			// 保存邮件
			message.saveChanges();
			// 发送邮件
			Transport transport = session.getTransport("smtp");
			// 连接服务器的邮箱
			transport.connect(host, user, pwd);
			// 把邮件发送出去
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public static void main(String[] args) {
		SendMail sm = new SendMail();
		String title = "安中通喊你领话费啦";
		String content = "<h3>亲爱的用户，<br/>欢迎您使用安中通网络电话。<h3>" +
		"<p><h4>现在点击领取话费，即可获得<font color=\"red\">" + 16 + "元</font>话费！</h4><br/>" +
				"<a href=\"http://www.00244dh.com/\"><button type=\"button\">领取话费</button></a></p>";
		sm.setAddress("a00244dh@163.com", title, content);
		try {
			sm.send();
		} catch (AddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
