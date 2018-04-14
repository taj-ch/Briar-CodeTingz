package org.briarproject.briar.android.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.OpenableColumns;
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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.briarproject.bramble.api.contact.ContactId;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;


import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static java.util.logging.Level.WARNING;
import static org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK;
import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_INTRODUCTION;

import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_PROFILE;

public class ChatActivity extends BriarActivity {

    private static final Logger LOG =
            Logger.getLogger(ChatActivity.class.getName());

	private LinearLayout layout;
	private ImageView sendButton;
	private EditText messageArea;
	private ScrollView scrollView;
	private ImageButton addImageButton;
	private ImageButton addLocationButton;
	private ImageButton addFileButton;
	public static final String CONTACT_ID = "briar.CONTACT_ID";
	public static final String CONTACT_EMAIL = "briar.CONTACT_EMAIL";
	private DatabaseReference mRootRef;
	private RecyclerView mMessagesList;
	private final List<Message> messageList = new ArrayList<>();
	private LinearLayoutManager mLinearLayout;
	private MessageAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	private LocationRequest mLocationRequest;
	private AlertDialog dialog;
	private String displayDeleteMessage;

	private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
	private long FASTEST_INTERVAL = 2000; /* 2 sec */

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

	private static final int GALLERY_PICK = 1;
	private static final int FILE_PICK = 2;

	// Storage Firebase
	private StorageReference mImageStorage;
	private Toolbar toolbar;
	private TextView toolbarTitle;
	private SwipeRefreshLayout mRefreshLayout;
	private static final int TOTAL_ITEMS_TO_LOAD = 10;
	private int mCurrentPage = 1;
	private int itemPos = 0;
	private String mLastKey = "";
	private String mPrevKey = "";

    private ContactId contactId;

    // Fields that are accessed from background threads must be volatile
    @Inject
    volatile ContactManager contactManager;

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

		startLocationUpdates();

		layout = (LinearLayout) findViewById(R.id.layout1);

		sendButton = (ImageView) findViewById(R.id.sendButton);
		messageArea = (EditText) findViewById(R.id.messageArea);
		addImageButton = (ImageButton) findViewById(R.id.addImageButton);
		addLocationButton = (ImageButton) findViewById(R.id.addLocationButton);
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
		toolbarTitle.setText(UserDetails.chatWith.replace(",", "."));

        // Get contact id from contactListFragment
        Intent retrieveContactId = getIntent();
        int id = retrieveContactId.getIntExtra(CONTACT_ID, -1);
        contactId = new ContactId(id);

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

				startActivityForResult(
						Intent.createChooser(galleryIntent, "SELECT IMAGE"),
						GALLERY_PICK);
			}
		});

		addLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getLocation();
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
		//updating the database with the sent message
		if (!TextUtils.isEmpty(message)) {

			String current_user_ref = "messages/" + UserDetails.username + "/" +
					UserDetails.chatWith;
			String chat_user_ref = "messages/" + UserDetails.chatWith + "/" +
					UserDetails.username;
			String CURRENT_USER_REF = "messages/" + UserDetails.username + "/" + UserDetails.chatWith;
			String CHAT_USER_REF = "messages/" + UserDetails.chatWith + "/" + UserDetails.username;

			DatabaseReference user_message_push = mRootRef.child("messages")
					.child(UserDetails.username).child(UserDetails.chatWith)
					.push();

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

			mRootRef.updateChildren(messageUserMap,
					new DatabaseReference.CompletionListener() {
						@Override
						public void onComplete(DatabaseError databaseError,
								DatabaseReference databaseReference) {
							if (databaseError != null) {
								Log.d("CHAT_LOG",
										databaseError.getMessage().toString());
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
				if (itemPos == 1) {
					mLastKey = messageKey;
				}

				if(dataSnapshot.child("from").getValue().equals(UserDetails.chatWith)){
					dataSnapshot.child("seen").getRef().setValue(true);
				}
				Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);
				mAdapter.notifyDataSetChanged();
				mRefreshLayout.setRefreshing(false);
				mLinearLayout.scrollToPositionWithOffset(10, 0);
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				String key = dataSnapshot.getKey();
				if(dataSnapshot.child("from").getValue().equals(UserDetails.chatWith)) {
					DatabaseReference ref = mRootRef.child("messages")
							.child(UserDetails.chatWith)
							.child(UserDetails.username).child(key);
					ref.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnap) {
							if (dataSnap.hasChild("message")) {
								dataSnap.child("seen").getRef().setValue(true);
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
				}
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

		DatabaseReference messageRef =	mRootRef.child("messages").child(UserDetails.username).child(UserDetails.chatWith);
		Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
		
		messageQuery.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Message message = dataSnapshot.getValue(Message.class);
				message.setId(dataSnapshot.getKey());
				itemPos++;

				if(itemPos == 1){
					String messageKey = dataSnapshot.getKey();
					mLastKey = messageKey;
					mPrevKey = messageKey;
				}
				if(dataSnapshot.child("from").getValue().equals(UserDetails.chatWith)){
					dataSnapshot.child("seen").getRef().setValue(true);
				}
				messageList.add(message);
				mAdapter.notifyDataSetChanged();
				mMessagesList.scrollToPosition(messageList.size() - 1);
				mRefreshLayout.setRefreshing(false);
			}

			@Override
			public void onChildChanged(DataSnapshot dataSnapshot, String s) {
				String key = dataSnapshot.getKey();

				// if incoming message
				if(dataSnapshot.child("from").getValue().equals(UserDetails.chatWith)) {
					DatabaseReference ref = mRootRef.child("messages")
							.child(UserDetails.chatWith)
							.child(UserDetails.username).child(key);
					ref.addValueEventListener(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnap) {
							// database message is set to seen
							if(dataSnap.hasChild("message")) {
								dataSnap.child("seen").getRef().setValue(true);
							}
						}

						@Override
						public void onCancelled(DatabaseError databaseError) {

						}
					});
				}

				// if outgoing message
				if(dataSnapshot.child("from").getValue().equals(UserDetails.username) &&
						dataSnapshot.child("seen").getValue().toString().equals("true")) {
					for (int i = 0; i < messageList.size(); i++) {
						Message m = messageList.get(i);
						if (m.getId().equals(key)) {
							//physical message is set to seen
							m.setSeen(true);
							mAdapter.notifyDataSetChanged();
						}
					}
				}

			}

			@Override
			public void onChildRemoved(DataSnapshot dataSnapshot) {
				String key = dataSnapshot.getKey();
				for(int i=0 ; i < messageList.size() ; i++){
					if(messageList.get(i).getId().equals(key)){
						messageList.remove(i);
					}
				}
				mAdapter.notifyDataSetChanged();
			}

			@Override
			public void onChildMoved(DataSnapshot dataSnapshot, String s) {

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});
	}

	protected void startLocationUpdates() {

		mLocationRequest = new LocationRequest();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
		LocationSettingsRequest.Builder builder =
				new LocationSettingsRequest.Builder();
		builder.addLocationRequest(mLocationRequest);
		LocationSettingsRequest locationSettingsRequest = builder.build();

		SettingsClient settingsClient =	LocationServices.getSettingsClient(this);
		settingsClient.checkLocationSettings(locationSettingsRequest);

		// Check if App-Level Location Permission is enabled
		if (ActivityCompat.checkSelfPermission(this,	Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			if (ActivityCompat.shouldShowRequestPermissionRationale(this,	Manifest.permission.ACCESS_FINE_LOCATION)) {

				new AlertDialog.Builder(this)
						.setTitle("Permission Required")
						.setMessage("Briar needs your permission to use your location")
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//Prompt the user once explanation has been shown
								ActivityCompat.requestPermissions(ChatActivity.this,
										new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
										MY_PERMISSIONS_REQUEST_LOCATION);
							}
						})
						.create()
						.show();
			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION);
			}

			return;
		}

		getFusedLocationProviderClient(this)
				.requestLocationUpdates(mLocationRequest,
						new LocationCallback() {
							@Override
							public void onLocationResult(
									LocationResult locationResult) {
								onLocationChanged(
										locationResult.getLastLocation());
							}
						},
						Looper.myLooper());
	}

	public void onLocationChanged(Location location) {
		String msg = "Updated Location: " +
				Double.toString(location.getLatitude()) + "," +
				Double.toString(location.getLongitude());
		Log.d("LOCATION", msg);

	}

	private void getLocation() {

		FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

		String current_user_ref =
				"messages/" + UserDetails.username + "/" + UserDetails.chatWith;
		String chat_user_ref =
				"messages/" + UserDetails.chatWith + "/" + UserDetails.username;


		if (ActivityCompat.checkSelfPermission(this,	Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
						Manifest.permission.ACCESS_COARSE_LOCATION) !=	PackageManager.PERMISSION_GRANTED) {

			System.out.println("Inside Location Permission Check");
			if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

				new AlertDialog.Builder(this)
						.setTitle("Permission Required")
						.setMessage("Briar needs your permission to use your location")
						.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogInterface, int i) {
								//Prompt the user once explanation has been shown
								ActivityCompat.requestPermissions(ChatActivity.this,
										new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
										MY_PERMISSIONS_REQUEST_LOCATION);
							}
						})
						.create()
						.show();
			} else {
				ActivityCompat.requestPermissions(this,
						new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
						MY_PERMISSIONS_REQUEST_LOCATION);
			}

			return;
		}

		locationClient.getLastLocation()
				.addOnSuccessListener(this, new OnSuccessListener<Location>() {

					@Override
					public void onSuccess(Location location) {
						DatabaseReference user_message_push =
								mRootRef.child("messages")
										.child(UserDetails.username)
										.child(UserDetails.chatWith).push();
						String message = "https://www.google.ca/maps/?q=" +
								Double.toString(location.getLatitude()) + "," +
								Double.toString(location.getLongitude());

						String push_id = user_message_push.getKey();

						Map messageMap = new HashMap();
						messageMap.put("message", message);
						messageMap.put("seen", false);
						messageMap.put("type", "text");
						messageMap.put("time", ServerValue.TIMESTAMP);
						messageMap.put("from", UserDetails.username);

						Map messageUserMap = new HashMap();
						messageUserMap.put(current_user_ref + "/" + push_id,
								messageMap);
						messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

						mRootRef.updateChildren(messageUserMap,
								new DatabaseReference.CompletionListener() {
									@Override
									public void onComplete(
											DatabaseError databaseError,
											DatabaseReference databaseReference) {
										if (databaseError != null) {
											Log.d("CHAT_LOG",
													databaseError.getMessage().toString());
										}
									}
								});

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
			case R.id.action_delete_message:
				if(!(mAdapter.getMessageFocusText().equals(""))) {
					if (mAdapter.getmessageValidForDelete()) {
						displayDeleteMessage = "Are you sure you want to delete: " + mAdapter.getMessageFocusText();
						AlertDialog.Builder builder;
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
							builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
						} else {
							builder = new AlertDialog.Builder(this);
						}
						builder.setTitle("Delete entry")
								.setMessage(displayDeleteMessage)
								.setPositiveButton(android.R.string.yes,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
												onMessageDelete();
											}
										})
								.setNegativeButton(android.R.string.no,
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int which) {
											}
										})
								.setIcon(android.R.drawable.ic_dialog_alert);
						dialog = builder.create();
						dialog.show();
					}
				} else {
					displayDeleteMessage =
							"To delete, hold on a specific" +
									" message you sent then press the delete button.";
					AlertDialog.Builder builder;
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
						builder = new AlertDialog.Builder(
								this, android.R.style.Theme_Material_Dialog_Alert);
					} else {
						builder = new AlertDialog.Builder(this);
					}
					builder.setTitle("Delete entry")
							.setMessage(displayDeleteMessage)
							.setPositiveButton(android.R.string.yes,
									new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
								}
							})
							.setIcon(android.R.drawable.ic_dialog_alert);
					dialog = builder.create();
					dialog.show();
				}
				return true;
			case R.id.action_view_profile:
				Intent profileIntent = new Intent(this, ProfileActivity.class);
				profileIntent.putExtra(CONTACT_EMAIL, UserDetails.chatWithEmail);
				startActivityForResult(profileIntent, REQUEST_PROFILE);
				return true;
            case R.id.action_social_remove_person:
                askToRemoveContact();
                return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void onMessageDelete(){
		String key = mAdapter.getMessageFocusKey();
		DatabaseReference messageRef1 =	mRootRef.child("messages")
				.child(UserDetails.username).child(UserDetails.chatWith);
		messageRef1.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				if(dataSnapshot.child(key).exists()) {
					messageRef1.child(key).removeValue();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		DatabaseReference messageRef2 =	mRootRef.child("messages")
				.child(UserDetails.chatWith).child(UserDetails.username);
		messageRef2.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String key = mAdapter.getMessageFocusKey();
				if(dataSnapshot.child(key).exists()) {
					messageRef2.child(key).removeValue();
				}
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

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

	@Override
	public void onStart(){
		super.onStart();
	}

	//Getters for testing purposes
	public AlertDialog getDialog(){
		return dialog;
	}

	public String getDisplayDeleteMessage(){
		return displayDeleteMessage;
	}
    private void askToRemoveContact() {
        DialogInterface.OnClickListener okListener =
                (dialog, which) -> removeContact();
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ChatActivity.this,
                        R.style.BriarDialogTheme);
        builder.setTitle(getString(R.string.dialog_title_delete_contact));
        builder.setMessage(getString(R.string.dialog_message_delete_contact));
        builder.setNegativeButton(R.string.delete, okListener);
        builder.setPositiveButton(R.string.cancel, null);
        dialog = builder.create();
        dialog.show();
    }

    private void removeContact() {
        runOnDbThread(() -> {
            try {
                contactManager.removeContact(contactId);
            } catch (DbException e) {
                if (LOG.isLoggable(WARNING)) LOG.log(WARNING, e.toString(), e);
            } finally {
                finishAfterContactRemoved();
            }
        });
    }

    private void finishAfterContactRemoved() {
        runOnUiThreadUnlessDestroyed(() -> {
            String deleted = getString(R.string.contact_deleted_toast);
            Toast.makeText(ChatActivity.this, deleted, LENGTH_SHORT)
                    .show();
            supportFinishAfterTransition();
        });
    }
}
