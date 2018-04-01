package org.briarproject.briar.android.chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
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
		chatActivity.addToMessagesList(message1);
		chatActivity.addToMessagesList(message2);
		chatActivity.addToMessagesList(message3);
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
		assertNull(messagesList.findViewHolderForAdapterPosition(3));
	}

	@Test
	public void testCorrectBubbles() {
		int MSG_OUT = 0;
		int MSG_IN = 1;
		assertEquals(MSG_OUT , messagesList.getAdapter().getItemViewType(0));
		assertEquals(MSG_IN , messagesList.getAdapter().getItemViewType(1));
		assertEquals(MSG_OUT , messagesList.getAdapter().getItemViewType(2));
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

	@Test
	public void testTextMessageDisplayed() {
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(0);

		assertEquals(View.VISIBLE,  v.itemView.findViewById(R.id.text).getVisibility());
		assertEquals(View.GONE, v.itemView.findViewById(R.id.image).getVisibility());
		assertEquals(View.GONE, v.itemView.findViewById(R.id.file).getVisibility());
	}

	@Test
	public void testImageMessageDisplayed() {
		Message message = new Message("./placeholder-thumbnail.jpg", "image", 1245415502, false);
		message.setFrom("testing");
		chatActivity.addToMessagesList(message);
		messagesList.getAdapter().notifyDataSetChanged();

		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(3);
		ImageView image = v.itemView.findViewById(R.id.image);

		assertEquals(View.GONE, v.itemView.findViewById(R.id.text).getVisibility());
		assertEquals(View.GONE,  v.itemView.findViewById(R.id.file).getVisibility());
		assertEquals(View.VISIBLE, image.getVisibility());

	}

	@Test
	public void testFileMessageDisplayed() {
		Message message = new Message("./placeholder.pdf", "file", "placeholder.pdf", 1245415502, false);
		message.setFrom("testing");
		chatActivity.addToMessagesList(message);
		messagesList.getAdapter().notifyDataSetChanged();
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(3);

		assertEquals(View.VISIBLE,  v.itemView.findViewById(R.id.file).getVisibility());
		assertEquals(View.GONE,  v.itemView.findViewById(R.id.text).getVisibility());
		assertEquals(View.GONE, v.itemView.findViewById(R.id.image).getVisibility());
	}
  
  @Test
	public void testMessageSeen(){
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(0);
		ImageView status = v.itemView.findViewById(R.id.status);
		assertNotNull(status);
	}

	@Test
	public void testMessageNotSeen(){
		RecyclerView.ViewHolder v = messagesList.findViewHolderForAdapterPosition(1);
		ImageView status = v.itemView.findViewById(R.id.status);
		assertNull(status);
	}

}