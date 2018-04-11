package org.briarproject.briar.android.contact;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.briarproject.briar.R;
import org.briarproject.briar.android.util.UiUtils;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>{
	private List<Message> mMessageList;
	private DatabaseReference mUserDatabase;
	private final int MSG_OUT = 0;
	private final int MSG_IN = 1;
	private Context mContext;
	private String messageFocusKey = "";
	private String messageFocusText = "";
	private boolean messageValidForDelete = false;


	public MessageAdapter(List<Message> mMessageList, Context context) {
		this.mMessageList = mMessageList;
		this.mContext = context;
	}

	@Override
	public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case MSG_IN:
				View v1 = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.list_item_conversation_msg_in, parent,
								false);
				return new MessageViewHolder(v1);
			case MSG_OUT:
				View v2 = LayoutInflater.from(parent.getContext())
						.inflate(R.layout.list_item_conversation_msg_out, parent,
								false);
				return new MessageViewHolder(v2);
			default:
				throw new IllegalArgumentException("Unknown Message Type");
		}
	}

	public class MessageViewHolder extends RecyclerView.ViewHolder {
		public TextView messageText;
		public TextView timeText;
		public ImageView messageImage;
		public TextView fileText;
		public ImageView messageSeen;

		public MessageViewHolder(View view) {
			super(view);
			messageText = (TextView) view.findViewById(R.id.text);
			timeText = (TextView) view.findViewById(R.id.time);
			messageImage = (ImageView) view.findViewById(R.id.image);
			fileText = (TextView) view.findViewById(R.id.file);
			messageSeen = (ImageView) view.findViewById(R.id.status);
			messageText.setAutoLinkMask(15);
		}
	}

	@Override
	public void onBindViewHolder(MessageViewHolder viewHolder, int i) {
		Message c = mMessageList.get(i);
		String from_user = c.getFrom();
		String message_type = c.getType();

		mUserDatabase =
				FirebaseDatabase.getInstance().getReference().child("Users")
						.child(from_user);

		if (message_type.equals("text")) {
			viewHolder.messageText.setVisibility(View.VISIBLE);
			viewHolder.messageText.setText(c.getMessage());
			viewHolder.messageImage.setVisibility(View.GONE);
			viewHolder.fileText.setVisibility(View.GONE);
			viewHolder.messageText.setOnLongClickListener(new View.OnLongClickListener(){
				@Override
				public boolean onLongClick(View v) {
					System.out.println("this message has focus: " + c.getMessage());
					messageFocusText = c.getMessage();
					messageFocusKey = c.getId();
					if(c.getFrom().equals(UserDetails.username)){
						messageValidForDelete = true;
					} else {
						messageValidForDelete = false;
					}
					return true;
				}
			});
		} else if (message_type.equals("image")) {
			viewHolder.messageText.setVisibility(View.GONE);
			viewHolder.fileText.setVisibility(View.GONE);
			viewHolder.messageImage.setVisibility(View.VISIBLE);
			viewHolder.messageImage.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = (new Intent(mContext,
									FullScreenImageActivity.class));
							intent.putExtra("url", c.getMessage());
							mContext.startActivity(intent);
						}
					});
			Picasso.with(viewHolder.messageImage.getContext())
					.load(c.getMessage())
					.placeholder(R.drawable.placeholder_thumbnail)
					.fit()
					.centerCrop()
					.into(viewHolder.messageImage);
		} else {
			viewHolder.fileText.setVisibility(View.VISIBLE);
			viewHolder.messageImage.setVisibility(View.GONE);
			viewHolder.messageText.setVisibility(View.GONE);
			viewHolder.fileText.setText(c.getName());
			viewHolder.fileText.setMovementMethod(LinkMovementMethod.getInstance());
			viewHolder.fileText.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = (new Intent(mContext, WebviewFile.class));
							intent.putExtra("url",c.getMessage());
							intent.putExtra("file_name",c.getName());
							mContext.startActivity(intent);
						}
					});
		}
		if (c.getFrom().equals(UserDetails.username)) {
			if (c.isSeen()) {
				//display the white message checkmarks on an message_out
				int res = R.drawable.message_delivered_white;
				if (viewHolder.messageSeen != null) {
					viewHolder.messageSeen.setImageResource(res);
				}
			} else{
				int res = 0;
				viewHolder.messageSeen.setImageResource(res);
			}
			viewHolder.timeText.setText(
					UiUtils.formatDate(viewHolder.timeText.getContext(),
							c.getTime()));
		}
	}

	@Override
	public int getItemCount() {
		return mMessageList.size();
	}

	//Returns the view type of the item at position for the purposes of view recycling.
	@Override
	public int getItemViewType(int position) {
		Message c = mMessageList.get(position);
		String from_user = c.getFrom();
		String current_user = UserDetails.username;

		if (from_user.equals(current_user)) {
			return MSG_OUT;
		} else if (!from_user.equals(current_user)) {
			return MSG_IN;
		}
		return -1;
	}

	public String getMessageFocusKey(){
		return messageFocusKey;
	}

	public String getMessageFocusText() {
		return messageFocusText;
	}

	public boolean getmessageValidForDelete(){
		return messageValidForDelete;
	}
}
