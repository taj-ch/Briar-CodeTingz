package org.briarproject.briar.android.contact;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.briarproject.bramble.api.identity.Author;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.Theme;
import org.briarproject.briar.android.contact.BaseContactListAdapter.OnContactClickListener;

import javax.annotation.Nullable;

import im.delight.android.identicons.IdenticonDrawable;

@UiThread
@NotNullByDefault
public class ContactItemViewHolder<I extends ContactItem>
		extends RecyclerView.ViewHolder {

	protected final ViewGroup layout;
	protected final ImageView avatar;
	protected final TextView name;
	@Nullable
	protected final ImageView bulb;
	private int contactConnected;
	private int contactDisconnected;

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

		Author author = item.getContact().getAuthor();

		avatar.setImageDrawable(
				new IdenticonDrawable(author.getId().getBytes()));
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

}
