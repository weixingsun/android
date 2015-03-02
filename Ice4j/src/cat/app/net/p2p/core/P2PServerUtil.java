package cat.app.net.p2p.core;

import java.util.ArrayList;
import java.util.List;

public class P2PServerUtil {

	public static List<P2PServerInfo> servers = new ArrayList<P2PServerInfo>();
	static{
		servers.add(new P2PServerInfo("numb.viagenie.ca", 3478, "weixingsun", "ws206771"));
		//servers.add(new P2PServerInfo("stun.jitsi.net", 3478, "guest", "anonymouspower!!"));
		//servers.add(new P2PServerInfo("180.160.188.246", 3478, "u1", "p1"));	//china shanghai
		//servers.add(new P2PServerInfo("sip-communicator.net",3478,"130.79.90.150",3478,"guest", "anonymouspower!!"));
		//ipv6.sip-communicator.net(stun)		//2001:660:4701:1001:230:5ff:fe1a:805f(turn)
	}
	
}
