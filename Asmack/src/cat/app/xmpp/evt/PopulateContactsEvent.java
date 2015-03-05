package cat.app.xmpp.evt;

import java.util.ArrayList;
import java.util.List;

import cat.app.xmpp.acct.Contact;

public class PopulateContactsEvent {
	private ArrayList<Contact> contacts = new ArrayList<Contact>();
	public PopulateContactsEvent(ArrayList<Contact> contacts) {
		this.setContacts(contacts);
	}
	public ArrayList<Contact> getContacts() {
		return contacts;
	}
	public void setContacts(ArrayList<Contact> contacts) {
		this.contacts = contacts;
	}
	
}
