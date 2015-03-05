package cat.app.xmpp;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.harmony.javax.security.sasl.Sasl;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.util.Base64;

public class SALSGTalkOauthMechanism extends SASLMechanism  {
    private static final Logger log = Logger.getLogger(SALSGTalkOauthMechanism.class.getName());
    public static final String NAME = "X-OAUTH2";


    /**
     * Constructor.
     */
    public SALSGTalkOauthMechanism(SASLAuthentication saslAuthentication) {
            super(saslAuthentication);
            log.info("Creating SASL mechanism for GTalk (X-OAUTH2)");
    }

    @Override
    public void authenticate(String username, String host, String accessToken,String dummy) throws IOException, NotConnectedException {
        this.hostname = host;
        log.info("Authenticating to host "+host+" with key "+username);

        String[] mechanisms = { "X-OAUTH2" };
        Hashtable<String, String> props = new Hashtable<String, String>();
        this.sc = Sasl.createSaslClient(mechanisms, null, "xmpp", host, props, this);
        getSASLAuthentication().send(new AuthMechanism(getName(), Base64.encodeBytes(('\0'+username+'\0'+accessToken).getBytes())));
    }

    @Override
    protected String getName() {
            return NAME;
    }

     }