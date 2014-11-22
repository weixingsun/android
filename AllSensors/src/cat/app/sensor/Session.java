package cat.app.sensor;

import java.util.HashMap;
import java.util.Map;

public class Session {

	private Map<Object, Object> _objectContainer;

	private static Session session;

	// singleton
	private Session() {
		_objectContainer = new HashMap<Object, Object>();
	}

	public static Session getSession() {

		if (session == null) {
			session = new Session();
			return session;
		} else {
			return session;
		}
	}

	public void put(Object key, Object value) {

		_objectContainer.put(key, value);
	}

	public Object get(Object key) {

		return _objectContainer.get(key);
	}

	public void cleanUpSession() {
		_objectContainer.clear();
	}

	public void remove(Object key) {
		_objectContainer.remove(key);
	}
}
