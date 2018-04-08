package org.briarproject.briar.android.contact;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;

import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.Theme;
import org.briarproject.briar.android.contact.BaseContactListAdapter.OnContactClickListener;
import org.briarproject.briar.android.profile.ProfileActivity;
import org.briarproject.briar.android.profile.ProfileFragment;

import java.util.logging.Logger;

import javax.annotation.Nullable;

import im.delight.android.identicons.IdenticonDrawable;

import static java.util.logging.Level.INFO;
import static java.util.logging.Level.WARNING;
import static org.briarproject.briar.android.activity.RequestCodes.REQUEST_PROFILE;
import static org.briarproject.briar.android.util.UiUtils.formatDate;

@UiThread
@NotNullByDefault
public class ContactItemViewHolder<I extends ContactItem>
		extends RecyclerView.ViewHolder {
	public static final String CONTACT_EMAIL = "briar.CONTACT_EMAIL";

	// Display log messages
	private final static String TAG = ContactItemViewHolder.class.getName();
	private final static Logger LOG = Logger.getLogger(TAG);

	protected final ViewGroup layout;
	protected final ImageView avatar;
	protected final TextView name;

	@Nullable
	protected final ImageView bulb;
	private int contactConnected;
	private int contactDisconnected;

	private Author author;
	private String authorName;

	// Reference to profile image in Firebase
	private StorageReference profileImageStorageRef;

	public ContactItemViewHolder(View v) {
		super(v);

		layout = (ViewGroup) v;
		avatar = v.findViewById(R.id.avatarView);
		name = v.findViewById(R.id.nameView);
		// this can be null as not all layouts that use this ViewHolder have it
		bulb = v.findViewById(R.id.bulbView);

	}

	protected void bind(I item, @Nullable OnContactClickListener<I> listener) {
		contactConnected = Theme.getAttributeDrawableInt(bulb.getContext(), R.attr.contact_connected);
		contactDisconnected = Theme.getAttributeDrawableInt(bulb.getContext(), R.attr.contact_disconnected);

		author = item.getContact().getAuthor();
		authorName = author.getName();

		// Set listener on avatar circle to open profile intent when clicked
		avatar.setOnClickListener(v -> {
			Intent profileIntent = new Intent(layout.getContext(), ProfileActivity.class);
			profileIntent.putExtra(CONTACT_EMAIL, UserDetails.chatWithEmail);
			layout.getContext().startActivity(profileIntent);
		});

		// Read the contacts profile image from storage if they have one
		try {
			profileImageStorageRef = FirebaseStorage.getInstance().getReference()
					.child("profile/images/"+authorName+"/profile.jpg");
			readProfileImage();
		} catch(Exception e) {
			Log.w(TAG, "Error reading profile image from firebase \n" + e.toString(), e);
		}

		String contactName = author.getName();

		name.setText(contactName);

		if (bulb != null) {
			// online/offline
			if (item.isConnected()) {
				bulb.setImageResource(contactConnected);
			} else {
				bulb.setImageResource(contactDisconnected);
			}
		}

		layout.setOnClickListener(v -> {
			if (listener != null) listener.onItemClick(avatar, item);
		});


	}

	// Retrieve the users profile image from firebase and replace briar avatar
	private void readProfileImage(){

		final long ONE_MEGABYTE = 1024 * 1024;
		profileImageStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
			@Override
			public void onSuccess(byte[] bytes) {
				Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
				if(bitmap != null) {
					avatar.setImageBitmap(bitmap);
				}else{
					avatar.setImageDrawable(
							new IdenticonDrawable(author.getId().getBytes()));
				}
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception exception) {
				// Handle any errors
				int errorCode = ((StorageException) exception).getErrorCode();
				String errorMessage = exception.getMessage();
				if (LOG.isLoggable(WARNING)) {
					LOG.log(WARNING, errorMessage, errorCode);
				}
				if (LOG.isLoggable(INFO)) {
					LOG.info("Error getting profile image from Firebase");
				}
				avatar.setImageDrawable(
						new IdenticonDrawable(author.getId().getBytes()));
			}
		});
	}
}
