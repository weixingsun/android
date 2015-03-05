package cat.app.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.util.StringUtils;

public class Client {
	private static Client INSTANCE = new Client();
	private Client(){}
	public static Client getInstance(){
		return INSTANCE;
	}
	public XMPPTCPConnection conn;
	public static String SELF;
	public void setConnection(XMPPTCPConnection conn){
		this.conn = conn;
		SELF = conn.getUser();
	}
	public XMPPConnection getConnection(){
		return this.conn;
	}
	public void sendMsg(String to,String msg) {
		SenderTask task = new SenderTask(INSTANCE,to,msg);
		task.execute();
	}
}
