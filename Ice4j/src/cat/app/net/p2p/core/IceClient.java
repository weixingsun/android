package cat.app.net.p2p.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.List;

import net.majorkernelpanic.streaming.Session;
import net.majorkernelpanic.streaming.SessionBuilder;
import net.majorkernelpanic.streaming.audio.AudioQuality;
import net.majorkernelpanic.streaming.video.VideoQuality;

import org.ice4j.Transport;
import org.ice4j.TransportAddress;
import org.ice4j.ice.Agent;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.IceProcessingState;
import org.ice4j.ice.LocalCandidate;
import org.ice4j.ice.NominationStrategy;
import org.ice4j.ice.RemoteCandidate;
import org.ice4j.ice.harvest.StunCandidateHarvester;
import org.ice4j.ice.harvest.TurnCandidateHarvester;
import org.ice4j.security.LongTermCredential;

import cat.app.net.p2p.cloud.DB;
import cat.app.net.p2p.util.SdpUtils;
import cat.app.net.p2p.util.StringUtils;

import android.util.Log;

public class IceClient {

	private static final String tag = IceClient.class.getSimpleName();
	private int port;
	private String streamName;
	private Agent agent;
	public String localSdp;
	public String remoteSdp;

	public DatagramSocket socket = null;
	public SocketAddress remoteAddress;
	//private String[] turnServers = new String[] { "stun.jitsi.net:3478"};
    //private String[] stunServers = new String[] { "stun.jitsi.net:3478" };
	//private String[] stunServers = new String[] { "stun.stunprotocol.org:3478" };
    //private String[] stunServers = new String[]{"stun.jitsi.org:3478","stun6.jitsi.net:3478"};
	//private String username = "guest";
	//private String password = "anonymouspower!!";
	private String[] turnServers = new String[] { "numb.viagenie.ca:3478"};
	private String[] stunServers = new String[] { "numb.viagenie.ca:3478"};
    private String username = "weixingsun";
    private String password = "ws206771";
	private IceProcessingListener listener;
	public IceClient(int port, String streamName) {
		this.port = port;
		this.streamName = streamName;
		this.listener = new IceProcessingListener();
	}

	public void init() throws Throwable {
		agent = createAgent(port, streamName);
		agent.setNominationStrategy(NominationStrategy.NOMINATE_HIGHEST_PRIO);
		agent.addStateChangeListener(listener);
		agent.setControlling(false);
		agent.setTa(10000);
		localSdp = SdpUtils.createSDPDescription(agent);
		//uploadLocalSdpMySQL();
		//Log.i(tag,"=================== feed the following to the remote agent ===================");
		//Log.i(tag,localSdp);
		//Log.i(tag,"=========================================================================\n");
		/*Session b = SessionBuilder.getInstance()
        //.setCallback(this)
        //.setSurfaceView(mSurfaceView)
        .setPreviewOrientation(90)
        //.setContext(getApplicationContext())
        .setAudioEncoder(SessionBuilder.AUDIO_NONE)
        .setAudioQuality(new AudioQuality(16000, 32000))
        .setVideoEncoder(SessionBuilder.VIDEO_H264)
        .setVideoQuality(new VideoQuality(320,240,20,500000))
        .build();
		b.getAudioTrack();*/
	}
	
	/**
	 * Reads an SDP description from the standard input.In production
	 * environment that we can exchange SDP with peer through signaling
	 * server(SIP server)
	 */
	public boolean initConnection(String remoteSdp) {
		try {
			this.remoteSdp = remoteSdp;
			SdpUtils.parseSDP(agent, remoteSdp);
	        startConnect();
	        socket = getDatagramSocket();
	        remoteAddress = getRemotePeerSocketAddress();
	        return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}
	public DatagramSocket getDatagramSocket() throws Throwable {
		LocalCandidate localCandidate = agent.getSelectedLocalCandidate(streamName);
		IceMediaStream stream = agent.getStream(streamName);
		List<Component> components = stream.getComponents();
		for (Component c : components) {
			Log.i(tag, c.toString());
		}
		Log.i(tag, localCandidate.toString());
		LocalCandidate candidate = (LocalCandidate) localCandidate;
		return candidate.getDatagramSocket();
	}

	public SocketAddress getRemotePeerSocketAddress() {
		RemoteCandidate remoteCandidate = agent.getSelectedRemoteCandidate(streamName);
		Log.i(tag,"Remote candinate transport address:" + remoteCandidate.getTransportAddress());
		Log.i(tag,"Remote candinate host address:"     + remoteCandidate.getHostAddress());
		Log.i(tag,"Remote candinate mapped address:"   + remoteCandidate.getMappedAddress());
		Log.i(tag,"Remote candinate relayed address:"  + remoteCandidate.getRelayedAddress());
		Log.i(tag,"Remote candinate reflexive address:"+ remoteCandidate.getReflexiveAddress());
		return remoteCandidate.getTransportAddress();
	}

	public void startConnect() throws InterruptedException {

		if (StringUtils.isBlank(remoteSdp)) {
			throw new NullPointerException(
					"Please exchange sdp information with peer before start connect! ");
		}

		agent.startConnectivityEstablishment();
		// agent.runInStunKeepAliveThread();
		synchronized (listener) {
			listener.wait();
		}
	}

	private Agent createAgent(int rtpPort, String streamName) throws Throwable {
		return createAgent(rtpPort, streamName, false);
	}

	private Agent createAgent(int rtpPort, String streamName,
			boolean isTrickling) throws Throwable {

		long startTime = System.currentTimeMillis();
		Agent agent = new Agent();
		agent.setTrickling(isTrickling);
		// STUN
		for (String server : stunServers) {
			String[] pair = server.split(":");
			agent.addCandidateHarvester(new StunCandidateHarvester(
					new TransportAddress(pair[0], Integer.parseInt(pair[1]),
							Transport.UDP)));
		}

		// TURN
		LongTermCredential longTermCredential = new LongTermCredential(username, password);

		for (String server : turnServers) {
			String[] pair = server.split(":");
			agent.addCandidateHarvester(new TurnCandidateHarvester(
					new TransportAddress(pair[0], Integer.parseInt(pair[1]),
							Transport.UDP), longTermCredential));
		}
		// STREAMS
		createStream(rtpPort, streamName, agent);
		long endTime = System.currentTimeMillis();
		long total = endTime - startTime;
		Log.i(tag, "Total harvesting time: " + total + "ms.");
		return agent;
	}

	private IceMediaStream createStream(int rtpPort, String streamName,
			Agent agent) throws Throwable {
		long startTime = System.currentTimeMillis();
		IceMediaStream stream = agent.createMediaStream(streamName);
		// rtp
		Component component = agent.createComponent(stream, Transport.UDP,
				rtpPort, rtpPort, rtpPort + 100);
		long endTime = System.currentTimeMillis();
		Log.i(tag, "Component Name:" + component.getName());
		Log.i(tag, "RTP Component created in " + (endTime - startTime) + " ms");
		return stream;
	}

	/**
	 * Receive notify event when ice processing state has changed.
	 */
	public static final class IceProcessingListener implements
			PropertyChangeListener {
		private long startTime = System.currentTimeMillis();
		public void propertyChange(PropertyChangeEvent event) {
			Object state = event.getNewValue();
			Log.i(tag, "Agent entered the " + state + " state.");
			if (state == IceProcessingState.COMPLETED) {
				long processingEndTime = System.currentTimeMillis();
				Log.i(tag, "Total ICE processing time: "
						+ (processingEndTime - startTime) + "ms");
				Agent agent = (Agent) event.getSource();
				List<IceMediaStream> streams = agent.getStreams();

				for (IceMediaStream stream : streams) {
					Log.i(tag, "Stream name: " + stream.getName());
					List<Component> components = stream.getComponents();
					for (Component c : components) {
						Log.i(tag, "------------------------------------------");
						Log.i(tag, "Component of stream:" + c.getName()
								+ ",selected of pair:" + c.getSelectedPair());
						Log.i(tag, "------------------------------------------");
					}
				}

				Log.i(tag, "Printing the completed check lists:");
				for (IceMediaStream stream : streams) {
					Log.i(tag, "Check list for  stream: " + stream.getName());
					Log.i(tag, "nominated check list:" + stream.getCheckList());
				}
				synchronized (this) {
					this.notifyAll();
				}
			} else if (state == IceProcessingState.TERMINATED) {
				Log.i(tag, "ice processing TERMINATED");
			} else if (state == IceProcessingState.FAILED) {
				Log.i(tag, "ice processing FAILED");
				((Agent) event.getSource()).free();
			}
		}
	}

}
