package cat.app.xmpp;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.os.AsyncTask;

public class SenderTask extends AsyncTask<String, Void, String> {

    private static final String tag = SenderTask.class.getSimpleName();
	byte[] buf = new byte[1024];
	Client client;
	private String to;
	private String text;
	//private File file;
	public SenderTask(Client client, String to, String text) {
		try {
			this.client = client;
			this.text = text;
			this.to = to;
		} catch (Throwable e) {
		}
	}

	@Override
	protected String doInBackground(String... params) {
		Message msg = new Message(to, Message.Type.chat);
		msg.setBody(this.text);
		this.sendXMPPMsg(msg);
		return null;
	}
	public void sendXMPPMsg(Packet msg) {
	 if (client.getConnection() != null) {
		try {
			client.getConnection().sendPacket(msg);
		} catch (NotConnectedException e) {
			e.printStackTrace();
		}
	 }
	 //TimeUnit.SECONDS.sleep(10);
	}
	
}
