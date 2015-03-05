/*package cat.app.xmpp;

import java.util.Properties;
import java.util.logging.Logger;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;

import revevol.applications.cloudpulse.client.Probe;
import revevol.applications.cloudpulse.client.ProbeClient;
import revevol.applications.cloudpulse.client.ProbeClientUtils;

import com.google.api.client.auth.oauth2.Credential;

public class GTalkProbe implements Probe {
    private static final Logger log = Logger.getLogger(GTalkProbe.class.getName());
    public static final String PROBE_GTALK_IDENTIFIER = "gtalkprobe";

    @Override
    public void run(ProbeClient client, Properties properties) throws Exception {
        log.info("Start running GTalkProbe.");
        long startTimestamp = new Date().getTime();
        Exception exception = null;
        MessageReboundResult result = new MessageReboundResult();
        Connection conn1 = null;

        try {
            Credential credential = ProbeClientUtils.getOAuth2Credentials(properties);

            ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
            config.setCompressionEnabled(true);
            config.setSASLAuthenticationEnabled(true);

            conn1 = new XMPPConnection(config);

            SASLAuthentication.registerSASLMechanism("X-OAUTH2", SALSGTalkOauthMechanism.class);
            SASLAuthentication.supportSASLMechanism("X-OAUTH2", 0);
            conn1.connect();

            log.info("Logging in");
            conn1.login(ProbeClientUtils.getOAuthConsumerId(properties), credential.getAccessToken());

            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus("My status");
            conn1.sendPacket(presence);

            ChatManager chatmanager = conn1.getChatManager();

            String destination = "destination@gmail.com";
            log.info("Sending chat message to " + destination);
            String message = "PING : " + UUID.randomUUID().toString();

            Chat chat = chatmanager.createChat(destination, new MessageListener(//TODO : here put your stuff));

            Message msg = new Message(destination, Message.Type.chat);

            msg.setBody(message);
            chat.sendMessage(msg);

            //Here you are
    }

}*/