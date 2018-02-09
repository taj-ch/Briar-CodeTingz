package org.briarproject.briar.android.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.profile.ProfileDb;

import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NONE;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.briar.android.util.UiUtils.setError;

public class AuthorNameFragment extends SetupFragment {

	private final static String TAG = AuthorNameFragment.class.getName();

	private TextInputLayout authorNameWrapper;
	private TextInputEditText authorNameInput;
	private Button nextButton;

	public static AuthorNameFragment newInstance() {
		return new AuthorNameFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(getString(R.string.setup_title));
		View v = inflater.inflate(R.layout.fragment_setup_author_name,
				container, false);
		authorNameWrapper = v.findViewById(R.id.nickname_entry_wrapper);
		authorNameInput = v.findViewById(R.id.nickname_entry);
		nextButton = v.findViewById(R.id.next);

		authorNameInput.addTextChangedListener(this);
		nextButton.setOnClickListener(this);

		return v;
	}

	@Override
	public String getUniqueTag() {
		return TAG;
	}

	@Override
	public void injectFragment(ActivityComponent component) {
		component.inject(this);
	}

	@Override
	protected String getHelpText() {
		return getString(R.string.setup_name_explanation);
	}

	@Override
	public void onTextChanged(CharSequence authorName, int i, int i1, int i2) {
		int authorNameLength = StringUtils.toUtf8(authorName.toString()).length;
		boolean error = authorNameLength > MAX_AUTHOR_NAME_LENGTH;
		setError(authorNameWrapper, getString(R.string.name_too_long), error);
		boolean enabled = authorNameLength > 0 && !error;
		authorNameInput
				.setImeOptions(enabled ? IME_ACTION_NEXT : IME_ACTION_NONE);
		authorNameInput.setOnEditorActionListener(enabled ? this : null);
		nextButton.setEnabled(enabled);
	}

	@Override
	public void onClick(View view) {
		setupController.setAuthorName(authorNameInput.getText().toString());

		// This will set the nickname so that the profile page can use it
		setAuthorNameProfileHelper(authorNameInput.getText().toString());
		ProfileDb profileDb = new ProfileDb(getActivity());
		profileDb.setProfileAuthorName(authorNameInput.getText().toString());
	}


	private void setAuthorNameProfileHelper(String author) {
		SharedPreferences sharedPref;
		SharedPreferences.Editor editor;

		// Use SharedPreferences to store and retrieve profile information in Key-Value Sets
		sharedPref = getActivity().getSharedPreferences(
				getString(R.string.profile_data_file), Context.MODE_PRIVATE);
		editor = sharedPref.edit();
		editor.putString(getString(R.string.profile_data_nickname_input), author);
		editor.commit();
	}

}
