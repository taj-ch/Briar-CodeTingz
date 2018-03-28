package org.briarproject.briar.android.contact;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;


import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.database.Query;

import org.briarproject.briar.R;
import org.briarproject.briar.android.introduction.IntroductionActivity;
import org.briarproject.briar.android.profile.ProfileActivity;
import org.briarproject.briar.android.util.UiUtils;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import static org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK;
import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_INTRODUCTION;
import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_PROFILE;

public class ChatActivity extends BriarActivity {
	private LinearLayout layout;
	private ImageView sendButton;
	private EditText messageArea;
	private ScrollView scrollView;
	private ImageButton addImageButton;
	private ImageButton addLocationButton;
	private Firebase reference;
	public static final String CONTACT_ID = "briar.CONTACT_ID";
	public static final String CONTACT_EMAIL = "briar.CONTACT_EMAIL";
	private DatabaseReference mRootRef;
	private RecyclerView mMessagesList;
	private final List<Message> messageList = new ArrayList<>();
	private LinearLayoutManager mLinearLayout;
	private MessageAdapter mAdapter;
	private ProgressDialog mProgressDialog;
	private LocationRequest mLocationRequest;

	private long UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
	private long FASTEST_INTERVAL = 2000; /* 2 sec */

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

	private static final int GALLERY_PICK = 1;

	// Storage Firebase
	private StorageReference mImageStorage;
	private Toolbar toolbar;
	private TextView toolbarContactName;
	private TextView toolbarTitle;
	private SwipeRefreshLayout mRefreshLayout;
	private static final int TOTAL_ITEMS_TO_LOAD = 10;
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
		startLocationUpdates();

		layout = (LinearLayout) findViewById(R.id.layout1);

		sendButton = (ImageView) findViewById(R.id.sendButton);
		messageArea = (EditText) findViewById(R.id.messageArea);
		addImageButton = (ImageButton) findViewById(R.id.addImageButton);
		addLocationButton = (ImageButton) findViewById(R.id.addLocationButton);

		sendButton.setEnabled(false);

		mRootRef = FirebaseDatabase.getInstance().getReference();
		mImageStorage = FirebaseStorage.getInstance().getReference();

		mAdapter = new MessageAdapter(messageList, this);

		mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
		mLinearLayout = new LinearLayoutManager(this);
		mRefreshLayout =
				(SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);

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

				startActivityForResult(
						Intent.createChooser(galleryIntent, "SELECT IMAGE"),
						GALLERY_PICK);
			}
		});

		addLocationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				requestLocation();
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

		if (!TextUtils.isEmpty(message)) {

			String current_user_ref = "messages/" + UserDetails.username + "/" +
					UserDetails.chatWith;
			String chat_user_ref = "messages/" + UserDetails.chatWith + "/" +
					UserDetails.username;

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
			messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
			messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

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

		DatabaseReference messageRef =
				mRootRef.child("messages").child(UserDetails.username)
						.child(UserDetails.chatWith);
		Query messageQuery =
				messageRef.orderByKey().endAt(mLastKey).limitToLast(10);
		messageQuery.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Message message = dataSnapshot.getValue(Message.class);
				String messageKey = dataSnapshot.getKey();

				if (!mPrevKey.equals(messageKey)) {
					messageList.add(itemPos++, message);
				} else {
					mPrevKey = mLastKey;
				}
				if (itemPos == 1) {
					mLastKey = messageKey;
				}

				Log.d("TOTALKEYS",
						"Last Key : " + mLastKey + " | Prev Key : " + mPrevKey +
								" | Message Key : " + messageKey);
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

		DatabaseReference messageRef =
				mRootRef.child("messages").child(UserDetails.username)
						.child(UserDetails.chatWith);
		Query messageQuery =
				messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
		messageQuery.addChildEventListener(new ChildEventListener() {
			@Override
			public void onChildAdded(DataSnapshot dataSnapshot, String s) {
				Message message = dataSnapshot.getValue(Message.class);
				itemPos++;
				if (itemPos == 1) {

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

	// Trigger new location updates at interval
	protected void startLocationUpdates() {

		// Create the location request to start receiving updates
		mLocationRequest = new LocationRequest();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		// Create LocationSettingsRequest object using location request
		LocationSettingsRequest.Builder builder =
				new LocationSettingsRequest.Builder();
		builder.addLocationRequest(mLocationRequest);
		LocationSettingsRequest locationSettingsRequest = builder.build();

		// Check whether location settings are satisfied
		// https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
		SettingsClient settingsClient =
				LocationServices.getSettingsClient(this);
		settingsClient.checkLocationSettings(locationSettingsRequest);

		// new Google API SDK v11 uses getFusedLocationProviderClient(this)
		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED && ActivityCompat
				.checkSelfPermission(this,
						Manifest.permission.ACCESS_COARSE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED) {
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
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
				// No explanation needed, we can request the permission.
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
								// do work here
								onLocationChanged(
										locationResult.getLastLocation());
							}
						},
						Looper.myLooper());
	}

	public void onLocationChanged(Location location) {
		// New location has now been determined
		String msg = "Updated Location: " +
				Double.toString(location.getLatitude()) + "," +
				Double.toString(location.getLongitude());
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void requestLocation() {

		FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

		String current_user_ref =
				"messages/" + UserDetails.username + "/" + UserDetails.chatWith;
		String chat_user_ref =
				"messages/" + UserDetails.chatWith + "/" + UserDetails.username;


		if (ActivityCompat.checkSelfPermission(this,
				Manifest.permission.ACCESS_FINE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED && ActivityCompat
				.checkSelfPermission(this,
						Manifest.permission.ACCESS_COARSE_LOCATION) !=
				PackageManager.PERMISSION_GRANTED) {
			System.out.println("Inside Location Permission Check");
			if (ActivityCompat.shouldShowRequestPermissionRationale(this,
					Manifest.permission.ACCESS_FINE_LOCATION)) {

				// Show an explanation to the user *asynchronously* -- don't block
				// this thread waiting for the user's response! After the user
				// sees the explanation, try again to request the permission.
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
				// No explanation needed, we can request the permission.
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
						Double latitude = location.getLatitude();
						Double longitude = location.getLongitude();
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
						messageUserMap
								.put(chat_user_ref + "/" + push_id, messageMap);

						mRootRef.updateChildren(messageUserMap,
								new DatabaseReference.CompletionListener() {
									@Override
									public void onComplete(
											DatabaseError databaseError,
											DatabaseReference databaseReference) {
										if (databaseError != null) {
											Log.d("CHAT_LOG",
													databaseError.getMessage()
															.toString());
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
}
