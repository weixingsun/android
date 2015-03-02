package cat.app.net.p2p.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.ice4j.ice.Agent;
import org.ice4j.ice.Component;
import org.ice4j.ice.IceMediaStream;
import org.ice4j.ice.IceProcessingState;

import cat.app.net.p2p.eb.StatusEvent;
import de.greenrobot.event.EventBus;

import android.util.Log;


/**
 * Receive notify event when ice processing state has changed.
 */
public class IceProcessingListener implements
		PropertyChangeListener {
	private static final String tag = IceProcessingListener.class.getSimpleName();
	private long startTime = System.currentTimeMillis();
	public void propertyChange(PropertyChangeEvent event) {
		Object state = event.getNewValue();
		Log.i(tag, "Agent entered the " + state + " state.");
		EventBus.getDefault().post(new StatusEvent("running"));
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
			EventBus.getDefault().post(new StatusEvent("terminated"));
		} else if (state == IceProcessingState.FAILED) {
			Log.i(tag, "ice processing FAILED");
			((Agent) event.getSource()).free();
			EventBus.getDefault().post(new StatusEvent("failed"));
		}
	}
}