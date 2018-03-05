package org.briarproject.briar.android.chat;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import org.briarproject.briar.R;
import org.briarproject.briar.android.TestBriarApplication;
import org.briarproject.briar.android.contact.ChatActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;


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
	private ScrollView scrollView;


	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		chatActivity = Robolectric.setupActivity(ChatActivity.class);
		layout = (LinearLayout)chatActivity.findViewById(R.id.layout1);
		sendButton = (ImageView)chatActivity.findViewById(R.id.sendButton);
		messageArea = (EditText)chatActivity.findViewById(R.id.messageArea);
		scrollView = (ScrollView)chatActivity.findViewById(R.id.scrollView);
	}

	@Test
	public void testSendButton() {
		assertEquals(sendButton.isEnabled(), false);
		messageArea.setText(" ");
		assertEquals(sendButton.isEnabled(), true);
	}

}