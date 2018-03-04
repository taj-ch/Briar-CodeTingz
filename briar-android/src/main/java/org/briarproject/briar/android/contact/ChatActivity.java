package org.briarproject.briar.android.contact;

import android.graphics.Color;
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
	private LinearLayout layout;
	private ImageView sendButton;
	private EditText messageArea;
	private ScrollView scrollView;
	private Firebase reference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		layout = (LinearLayout) findViewById(R.id.layout1);
		sendButton = (ImageView)findViewById(R.id.sendButton);
		messageArea = (EditText)findViewById(R.id.messageArea);
		scrollView = (ScrollView)findViewById(R.id.scrollView);

		Firebase.setAndroidContext(this);
		reference = new Firebase("https://briar-61651.firebaseio.com//messages/" + "From: " + UserDetails.username + " To: " + UserDetails.chatWith);

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String messageText = messageArea.getText().toString();
				Long tsLong = System.currentTimeMillis()/1000;
				String ts = tsLong.toString();

				// Write a message to the database
				FirebaseDatabase database = FirebaseDatabase.getInstance();
				DatabaseReference myRef = database.getReference("/messages/" + UserDetails.username + "_" + UserDetails.chatWith + "/" + "From: " + UserDetails.username + " To: " + UserDetails.chatWith);
				//DatabaseReference destRef = database.getReference("messages/" + "To: " + UserDetails.chatWith + " From:  " + UserDetails.username + " " + ts);

				if(!messageText.equals("")){
					myRef.setValue(messageText);
					//destRef.setValue(messageText);
					addMessageBox("You:-\n" + messageText, 1);
					messageArea.setText("");
				}
			}
		});

		// Read from the database
		FirebaseDatabase database = FirebaseDatabase.getInstance();
		DatabaseReference myRef = database.getReference("/messages/" + UserDetails.username + "_" + UserDetails.chatWith + "/" + "From: " + UserDetails.chatWith + " To: " + UserDetails.username);

		myRef.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
				// This method is called once with the initial value and again
				// whenever data at this location is updated.
				String value = dataSnapshot.getValue(String.class);
				addMessageBox(UserDetails.chatWith + ":-\n" + value, 2);

			}


			@Override
			public void onCancelled(DatabaseError error) {
				// Failed to read value
			}
		});

		reference.addChildEventListener(new ChildEventListener() {
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
			textView.setBackgroundResource(R.drawable.msg_in);
		}
		else{
			lp2.gravity = Gravity.RIGHT;
			textView.setTextColor(Color.WHITE);
			textView.setBackgroundResource(R.drawable.msg_out);
		}
		textView.setLayoutParams(lp2);
		layout.addView(textView);
		scrollView.fullScroll(View.FOCUS_DOWN);
	}
}

