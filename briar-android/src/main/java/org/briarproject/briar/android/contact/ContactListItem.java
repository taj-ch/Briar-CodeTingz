package org.briarproject.briar.android.contact;

import android.support.annotation.NonNull;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.briar.api.client.MessageTracker.GroupCount;

import java.util.Comparator;

import javax.annotation.concurrent.NotThreadSafe;

@NotThreadSafe
@NotNullByDefault
public class ContactListItem extends ContactItem {

	private boolean empty;
	private long timestamp;
	private int unread;
	private long date;
	private String lastMessage;

	public ContactListItem(Contact contact, boolean connected,
			GroupCount count) {
		super(contact, connected);
		this.empty = count.getMsgCount() == 0;
		this.unread = count.getUnreadCount();
		this.timestamp = count.getLatestMsgTime();
		this.date = 0;
		this.lastMessage = "Send a message!";
	}

	void addMessage(ConversationItem message) {
		empty = false;
		if (message.getTime() > timestamp) timestamp = message.getTime();
		if (!message.isRead())
			unread++;
	}

	boolean isEmpty() {
		return empty;
	}

	long getTimestamp() {
		return timestamp;
	}

	int getUnreadCount() {
		return unread;
	}

	long getDate() {
		return date;
	}

	String getLastMessage() {
		return lastMessage;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

}
