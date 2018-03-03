package org.briarproject.briar.android.contact;

import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.briarproject.briar.R;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class ChatActivity extends AppCompatActivity {
	private static final String TAG = "";
	private LinearLayout layout;
	private RelativeLayout layout_2;
	private ImageView sendButton;
	private EditText messageArea;
	private ScrollView scrollView;
	private Firebase reference1, reference2;
	private int message = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		layout = (LinearLayout) findViewById(R.id.layout1);
		layout_2 = (RelativeLayout)findViewById(R.id.layout2);
		sendButton = (ImageView)findViewById(R.id.sendButton);
		messageArea = (EditText)findViewById(R.id.messageArea);
		scrollView = (ScrollView)findViewById(R.id.scrollView);

		Firebase.setAndroidContext(this);
		reference1 = new Firebase("https://briar-61651.firebaseio.com//messages/" + UserDetails.username + "_" + UserDetails.chatWith);
		reference2 = new Firebase("https://briar-61651.firebaseio.com//messages/" + UserDetails.chatWith + "_" + UserDetails.username);

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String messageText = messageArea.getText().toString();

				// Write a message to the database
				FirebaseDatabase database = FirebaseDatabase.getInstance();
				DatabaseReference myRef = database.getReference("/messages/" + UserDetails.username + "_" + UserDetails.chatWith + "_" + message);
				DatabaseReference destRef = database.getReference("messages/" + UserDetails.chatWith + "_" + UserDetails.username + "_" + message);


				if(!messageText.equals("")){
					myRef.setValue(messageText);
					destRef.setValue(messageText);
					messageArea.setText("");
					message++;
				}
			}
		});

		reference1.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Map map = dataSnapshot.getValue(Map.class);
				String message = map.get("message").toString();
				String userName = map.get("user").toString();

				if(userName.equals(UserDetails.username)){
					addMessageBox("You:-\n" + message, 1);
				}
				else{
					addMessageBox(UserDetails.chatWith + ":-\n" + message, 2);
				}
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {

			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(FirebaseError firebaseError) {

			}
		});
	}

	public void addMessageBox(String message, int type){
		TextView textView = new TextView(ChatActivity.this);
		textView.setText(message);

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		lp2.weight = 1.0f;

		if(type == 1) {
			lp2.gravity = Gravity.LEFT;
			textView.setBackgroundResource(R.drawable.bubble);
		}
		else{
			lp2.gravity = Gravity.RIGHT;
			textView.setBackgroundResource(R.drawable.bubble_white);
		}
		textView.setLayoutParams(lp2);
		layout.addView(textView);
		scrollView.fullScroll(View.FOCUS_DOWN);
	}
}

