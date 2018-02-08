package org.briarproject.briar.android.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.view.View.OnClickListener;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;

import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.briarproject.bramble.api.nullsafety.MethodsNotNullByDefault;
import org.briarproject.bramble.api.nullsafety.ParametersNotNullByDefault;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;

import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.forum.CreateForumActivity;
import org.briarproject.briar.android.fragment.BaseFragment;


import javax.annotation.Nullable;

import static android.support.design.widget.Snackbar.LENGTH_INDEFINITE;


@UiThread
@MethodsNotNullByDefault
@ParametersNotNullByDefault
public class ProfileFragment extends BaseFragment implements
		OnClickListener {
	private static int RESULT_LOAD_PROFILE_PIC = 1;

	private final static String TAG = ProfileFragment.class.getName();

	public static ProfileFragment newInstance() {

		ProfileFragment f = new ProfileFragment();

		return f;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View profileView = inflater.inflate(R.layout.fragment_profile,
				container, false);

		getActivity().setTitle(R.string.profile_button);

		Button profileButton = (Button) profileView.findViewById(R.id.action_create_profile);
		profileButton.setOnClickListener(this);
		return profileView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.action_create_profile:
				Intent i = new Intent(
						Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				break;
		}
	}



	@Override
	public void injectFragment(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	public String getUniqueTag() {
		return TAG;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_create_profile:
				Intent intent =
						new Intent(getContext(), CreateForumActivity.class);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

}
