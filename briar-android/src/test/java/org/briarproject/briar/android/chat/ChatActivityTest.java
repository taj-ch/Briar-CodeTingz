package org.briarproject.briar.android.chat;

import android.support.v7.widget.RecyclerView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.contact.ChatActivity;

import org.briarproject.briar.android.contact.Message;
import org.briarproject.briar.android.contact.UserDetails;
import org.briarproject.briar.android.util.UiUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;


/**
 * Created by Roy Saliba on 2018-03-04.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, application = TestBriarApplication.class,
		packageName = "org.briarproject.briar")
public class ChatActivityTest {

	private ChatActivity chatActivity;
	private LinearLayout layout;
	private ImageView sendButton;
	private EditText messageArea;
	private RecyclerView messagesList;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		chatActivity = Robolectric.setupActivity(ChatActivity.class);
		layout = (LinearLayout)chatActivity.findViewById(R.id.layout1);
		sendButton = (ImageView)chatActivity.findViewById(R.id.sendButton);
		messageArea = (EditText)chatActivity.findViewById(R.id.messageArea);
		messagesList = (RecyclerView)chatActivity.findViewById(R.id.messages_list);

		UserDetails.changeUsername("testing");
		UserDetails.changeChatWith("working");
		Message message1 = new Message("hello", "text", 1245415500, false);
		Message message2 = new Message("hi", "text", 1245415501, false);
		Message message3 = new Message("bye", "text", 1245415502, false);
		message1.setFrom("testing");
		message2.setFrom("working");
		message3.setFrom("testing");
		chatActivity.addtToMessagesList(message1);
		chatActivity.addtToMessagesList(message2);
		chatActivity.addtToMessagesList(message3);
		messagesList.getAdapter().notifyDataSetChanged();
	}

	@Test
	public void testSendButton() {
		assertEquals(sendButton.isEnabled(), false);
		messageArea.setText(" ");
		assertEquals(sendButton.isEnabled(), true);
	}

	@Test
	public void testLoadMessages() {
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(0);
		TextView messageTextView = v.itemView.findViewById(R.id.text);
		String messageText = messageTextView.getText().toString();
		assertEquals(3, messagesList.getAdapter().getItemCount());
		assertEquals("hello", messageText);
		assertEquals(null, messagesList.findViewHolderForAdapterPosition(3));
	}

	@Test
	public void testCorrectBubbles() {
		int MSG_OUT = 0;
		int MSG_IN = 1;
		assertEquals(0 , messagesList.getAdapter().getItemViewType(0));
		assertEquals(1 , messagesList.getAdapter().getItemViewType(1));
		assertEquals(0 , messagesList.getAdapter().getItemViewType(2));
	}

	@Test
	public void testCorrectTime() {
		String time = UiUtils.formatDate(messageArea.getContext(), 1245415500);
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(0);
		TextView timeTextView = v.itemView.findViewById(R.id.time);
		String timeText = timeTextView.getText().toString();
		assertEquals(time , timeText);
	}

	@Test
	public void testLinksClickable() {
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(0);
		TextView messageTextView = v.itemView.findViewById(R.id.text);

		assertTrue(messageTextView.getLinksClickable());
	}
}