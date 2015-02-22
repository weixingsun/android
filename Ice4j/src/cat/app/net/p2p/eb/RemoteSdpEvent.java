package cat.app.net.p2p.eb;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RemoteSdpEvent {

	public RemoteSdpEvent(List<String> hosts,List<String> sdps) {
	}
	public RemoteSdpEvent(Map<String,String> hosts) {
		this.remoteHosts = hosts;
	}
	public Map<String,String> remoteHosts;

	public Collection<String> getSdps() {
		return remoteHosts.values();
	}
	public Set<String> getHosts() {
		return remoteHosts.keySet();
	}
	
}
