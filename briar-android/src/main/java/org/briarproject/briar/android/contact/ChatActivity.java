package org.briarproject.briar.android.contact;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_PROFILE;

public class ChatActivity extends BriarActivity {
	private LinearLayout layout;
	private ImageView sendButton;
	private EditText messageArea;
	private ScrollView scrollView;
	private ImageButton addImageButton;
	private ImageButton addFileButton;
	private Firebase reference;
	public static final String CONTACT_ID = "briar.CONTACT_ID";
	public static final String CONTACT_EMAIL = "briar.CONTACT_EMAIL";
	private DatabaseReference mRootRef;
	private RecyclerView mMessagesList;
	private final List<Message> messageList = new ArrayList<>();
	private LinearLayoutManager mLinearLayout;
	private MessageAdapter mAdapter;
	private ProgressDialog mProgressDialog;

	private static final int GALLERY_PICK = 1;
	private static final int FILE_PICK = 2;

	// Storage Firebase
	private StorageReference mImageStorage;
	private Toolbar toolbar;
	private TextView toolbarContactName;
	private TextView toolbarTitle;
	private SwipeRefreshLayout mRefreshLayout;
	private static final int  TOTAL_ITEMS_TO_LOAD = 10;
	private int mCurrentPage = 1;
	private int itemPos = 0;
	private String mLastKey = "";
	private String mPrevKey = "";

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		FirebaseApp.initializeApp(this);
		Firebase.setAndroidContext(this);

		layout = (LinearLayout) findViewById(R.id.layout1);

		sendButton = (ImageView)findViewById(R.id.sendButton);
		messageArea = (EditText)findViewById(R.id.messageArea);
		addImageButton = (ImageButton)findViewById(R.id.addImageButton);
		addFileButton = (ImageButton)findViewById(R.id.addFileButton);

		sendButton.setEnabled(false);

		mRootRef = FirebaseDatabase.getInstance().getReference();
		mImageStorage = FirebaseStorage.getInstance().getReference();

		mAdapter = new MessageAdapter(messageList, this);

		mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
		mLinearLayout = new LinearLayoutManager(this);
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

		mMessagesList.setHasFixedSize(true);
		mMessagesList.setLayoutManager(mLinearLayout);
		mMessagesList.setAdapter(mAdapter);

		mMessagesList.scrollToPosition(messageList.size() - 1);

		// Custom Toolbar
		toolbar = setUpCustomToolbar(true);
		if (toolbar != null) {
			toolbarTitle = toolbar.findViewById(R.id.contactName);
		}
		toolbarTitle.setText(UserDetails.chatWith);

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

		addFileButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String[] mimeTypes =
						{"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
								"application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
								"application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
								"text/plain",
								"application/pdf",
								"application/zip"};

				// ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file browser.
				Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

				// Choose only files from the mime types defined above
				intent.setType("*/*");
				intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
				intent.addCategory(Intent.CATEGORY_OPENABLE);

				startActivityForResult(intent, FILE_PICK);
			}
		});
		
		mRefreshLayout.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						mCurrentPage++;
						itemPos = 0;
						loadMoreMessages();
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

			String CURRENT_USER_REF = "messages/" + UserDetails.username + "/" + UserDetails.chatWith;
			String CHAT_USER_REF = "messages/" + UserDetails.chatWith + "/" + UserDetails.username;

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
			messageUserMap.put(CURRENT_USER_REF + "/" + push_id, messageMap);
			messageUserMap.put(CHAT_USER_REF + "/" + push_id, messageMap);

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
	private void loadMoreMessages() {

		DatabaseReference messageRef = mRootRef.child("messages").child(UserDetails.username).child(UserDetails.chatWith);
		Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
		messageQuery.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Message message = dataSnapshot.getValue(Message.class);
				String messageKey = dataSnapshot.getKey();

				if(!mPrevKey.equals(messageKey)){
					messageList.add(itemPos++, message);
				} else {
					mPrevKey = mLastKey;
				}
				if(itemPos == 1) {
					mLastKey = messageKey;
				}

				Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);
				mAdapter.notifyDataSetChanged();
				mRefreshLayout.setRefreshing(false);
				mLinearLayout.scrollToPositionWithOffset(10, 0);
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


	private void loadMessages() {

		DatabaseReference messageRef = mRootRef.child("messages").child(UserDetails.username).child(UserDetails.chatWith);
		Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
		messageQuery.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Message message = dataSnapshot.getValue(Message.class);
				itemPos++;
				if(itemPos == 1){

					String messageKey = dataSnapshot.getKey();

					mLastKey = messageKey;
					mPrevKey = messageKey;

				}
				messageList.add(message);
				mAdapter.notifyDataSetChanged();
				mMessagesList.scrollToPosition(messageList.size() - 1);
				mRefreshLayout.setRefreshing(false);
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
			mProgressDialog = new ProgressDialog(ChatActivity.this);
			mProgressDialog.setTitle("Uploading Image...");
			mProgressDialog.setMessage("Please wait while we upload and process the image.");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();

			Uri imageUri = data.getData();

			final String CURRENT_USER_REF = "messages/" + UserDetails.username + "/" + UserDetails.chatWith;
			final String CHAT_USER_REF = "messages/" + UserDetails.chatWith + "/" + UserDetails.username;

			DatabaseReference user_message_push = mRootRef.child("messages")
					.child(UserDetails.username).child(UserDetails.chatWith).push();

			final String PUSH_ID = user_message_push.getKey();

			StorageReference filepath = mImageStorage.child("message_images").child(PUSH_ID + ".jpg");

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
						messageUserMap.put(CURRENT_USER_REF + "/" + PUSH_ID, messageMap);
						messageUserMap.put(CHAT_USER_REF + "/" + PUSH_ID, messageMap);

						messageArea.setText("");

						mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
							@Override
							public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
								mProgressDialog.dismiss();
								if(databaseError != null){
									Log.d("CHAT_LOG", databaseError.getMessage().toString());
								}
							}
						});
					}
				}
			});
		} else if (request == FILE_PICK && result == RESULT_OK) {
			mProgressDialog = new ProgressDialog(ChatActivity.this);
			mProgressDialog.setTitle("Uploading File...");
			mProgressDialog.setMessage("Please wait while we upload and process the file.");
			mProgressDialog.setCanceledOnTouchOutside(false);
			mProgressDialog.show();

			Uri fileUri = data.getData();
			final MimeTypeMap MIME = MimeTypeMap.getSingleton();
			String mimeType = getMimeType(fileUri);
			String extension = MIME.getExtensionFromMimeType(mimeType);

			String name = getFileName(fileUri);

			final String CURRENT_USER_REF = "messages/" + UserDetails.username + "/" + UserDetails.chatWith;
			final String CHAT_USER_REF = "messages/" + UserDetails.chatWith + "/" + UserDetails.username;

			DatabaseReference user_message_push = mRootRef.child("messages")
					.child(UserDetails.username).child(UserDetails.chatWith).push();

			final String PUSH_ID = user_message_push.getKey();

			StorageReference filepath = mImageStorage.child("message_files").child(PUSH_ID + "." + extension);

			filepath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
				@Override
				public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
					if(task.isSuccessful()){
						String download_url = task.getResult().getDownloadUrl().toString();

						Map messageMap = new HashMap();
						messageMap.put("message", download_url);
						messageMap.put("seen", false);
						messageMap.put("type", "file");
						messageMap.put("name", name);
						messageMap.put("time", ServerValue.TIMESTAMP);
						messageMap.put("from", UserDetails.username);

						Map messageUserMap = new HashMap();
						messageUserMap.put(CURRENT_USER_REF + "/" + PUSH_ID, messageMap);
						messageUserMap.put(CHAT_USER_REF + "/" + PUSH_ID, messageMap);

						messageArea.setText("");

						mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener() {
							@Override
							public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
								mProgressDialog.dismiss();
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
	
	//For testing purposes
	public void addToMessagesList(Message message) {
		messageList.add(message);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.conversation_actions, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			case R.id.action_view_profile:
				Intent profileIntent = new Intent(this, ProfileActivity.class);
				profileIntent.putExtra(CONTACT_EMAIL, UserDetails.chatWithEmail);
				startActivityForResult(profileIntent, REQUEST_PROFILE);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private String getMimeType(Uri uri) {
		String mimeType = null;
		if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
			ContentResolver cr = getApplicationContext().getContentResolver();
			mimeType = cr.getType(uri);
		} else {
			String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
					.toString());
			mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
					fileExtension.toLowerCase());
		}
		return mimeType;
	}

	private String getFileName(Uri uri) {
		String result = null;
		if (uri.getScheme().equals("content")) {
			Cursor cursor = getContentResolver().query(uri, null, null, null, null);
			try {
				if (cursor != null && cursor.moveToFirst()) {
					result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
				}
			} finally {
				cursor.close();
			}
		}
		if (result == null) {
			result = uri.getPath();
			int cut = result.lastIndexOf('/');
			if (cut != -1) {
				result = result.substring(cut + 1);
			}
		}
		return result;
	}
}
