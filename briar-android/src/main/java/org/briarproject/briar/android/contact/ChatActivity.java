package org.briarproject.briar.android.contact;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.briarproject.briar.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends AppCompatActivity {
	private LinearLayout layout;
	private ImageView sendButton;
	private EditText messageArea;
	private ScrollView scrollView;
	private ImageButton addImageButton;
	private Firebase reference;
	private DatabaseReference mRootRef;
	private RecyclerView mMessagesList;
	private final List<Message> messageList = new ArrayList<>();
	private LinearLayoutManager mLinearLayout;
	private MessageAdapter mAdapter;

	private static final int GALLERY_PICK = 1;

	// Storage Firebase
	private StorageReference mImageStorage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		layout = (LinearLayout) findViewById(R.id.layout1);
		sendButton = (ImageView)findViewById(R.id.sendButton);
		messageArea = (EditText)findViewById(R.id.messageArea);
		scrollView = (ScrollView)findViewById(R.id.scrollView);
		addImageButton = (ImageButton)findViewById(R.id.addImageButton);

		sendButton.setEnabled(false);

		mRootRef = FirebaseDatabase.getInstance().getReference();
		mImageStorage = FirebaseStorage.getInstance().getReference();

		mAdapter = new MessageAdapter(messageList);

		mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
		mLinearLayout = new LinearLayoutManager(this);

		mMessagesList.setHasFixedSize(true);
		mMessagesList.setLayoutManager(mLinearLayout);
		mMessagesList.setAdapter(mAdapter);

		loadMessages();

		TextWatcher tw = new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				enableOrDisableSendButton();
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		};

		messageArea.addTextChangedListener(tw);


		FirebaseApp.initializeApp(this);
		Firebase.setAndroidContext(this);

		sendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});

		addImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent galleryIntent = new Intent();
				galleryIntent.setType("image/*");
				galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

				startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
			}
		});
	}

	private void enableOrDisableSendButton() {
		if (messageArea != null) {
			sendButton.setEnabled(true);
		}
	}
	
	private void sendMessage() {
		String message = messageArea.getText().toString();

		if(!TextUtils.isEmpty(message)){

			String current_user_ref = "messages/" + UserDetails.username + "/" + UserDetails.chatWith;
			String chat_user_ref = "messages/" + UserDetails.chatWith + "/" + UserDetails.username;

			DatabaseReference user_message_push = mRootRef.child("messages")
					.child(UserDetails.username).child(UserDetails.chatWith).push();

			String push_id = user_message_push.getKey();

			Map messageMap = new HashMap();
			messageMap.put("message", message);
			messageMap.put("seen", false);
			messageMap.put("type", "text");
			messageMap.put("time", ServerValue.TIMESTAMP);
			messageMap.put("from", UserDetails.username);

			Map messageUserMap = new HashMap();
			messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
			messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

			messageArea.setText("");

			mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
				@Override
				public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
					if(databaseError != null){
						Log.d("CHAT_LOG", databaseError.getMessage().toString());
					}
				}
			});
		}
	}

	private void loadMessages() {
		DatabaseReference messageRef = mRootRef.child("messages").child(UserDetails.username).child(UserDetails.chatWith);

		messageRef.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Message message = dataSnapshot.getValue(Message.class);

				messageList.add(message);
				mAdapter.notifyDataSetChanged();
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
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	@Override
	public void onActivityResult(int request, int result, Intent data) {
		super.onActivityResult(request, result, data);

		if (request == GALLERY_PICK && result == RESULT_OK) {
			Uri imageUri = data.getData();

			final String current_user_ref = "messages/" + UserDetails.username + "/" + UserDetails.chatWith;
			final String chat_user_ref = "messages/" + UserDetails.chatWith + "/" + UserDetails.username;

			DatabaseReference user_message_push = mRootRef.child("messages")
					.child(UserDetails.username).child(UserDetails.chatWith).push();

			final String push_id = user_message_push.getKey();

			StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

			filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
					if(task.isSuccessful()){
						String download_url = task.getResult().getDownloadUrl().toString();

						Map messageMap = new HashMap();
						messageMap.put("message", download_url);
						messageMap.put("seen", false);
						messageMap.put("type", "image");
						messageMap.put("time", ServerValue.TIMESTAMP);
						messageMap.put("from", UserDetails.username);

						Map messageUserMap = new HashMap();
						messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
						messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

						messageArea.setText("");

						mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
							@Override
							public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
								if(databaseError != null){
									Log.d("CHAT_LOG", databaseError.getMessage().toString());
								}
							}
						});
					}
				}
			});
		}
	}
}