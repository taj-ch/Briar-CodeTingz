package org.briarproject.briar.android.keyagreement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;

import org.briarproject.bramble.api.system.Clock;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.contact.UserDetails;
import org.briarproject.briar.android.fragment.BaseFragment;
import org.briarproject.briar.android.login.AuthorNameFragment;
import org.briarproject.briar.android.util.UiUtils;
import org.briarproject.briar.api.test.TestDataCreator;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.inject.Inject;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.util.logging.Level.INFO;
import static org.briarproject.briar.test.TestData.AUTHOR_NAMES;
import static org.briarproject.briar.test.TestData.SPECIFIC_AUTHOR_NAMES;

public class AddContactFragment extends BaseFragment implements TextWatcher,
		OnEditorActionListener, View.OnClickListener {

	private final static String TAG = AddContactFragment.class.getName();
	private Clock clock;

	private TextInputEditText emailInput;
	private Button addContactButton;

	private FirebaseAuth mAuth;


	public static AddContactFragment newInstance() {
		return new AddContactFragment();
	}

	@Inject
	TestDataCreator testDataCreator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle("Add Email Contact");
		View v = inflater.inflate(R.layout.activity_add_contact_by_email,
				container, false);
		emailInput = v.findViewById(R.id.edit_email);
		addContactButton = v.findViewById(R.id.btn_add_by_email);
		
		addContactButton.setOnClickListener(this);

		FirebaseApp.initializeApp(this.getContext());
		mAuth = FirebaseAuth.getInstance();

		return v;
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
	public void onClick(View view) {
		String email = emailInput.getText().toString();
		addContactButton.setVisibility(INVISIBLE);
		String emailToUserName= "";

		// Get username from email(i.e, ignore everything after @ inclusive from email)
		String tempEmail = email.replaceAll("\\s","");
		Pattern pattern = Pattern.compile("([^@]+)");
		Matcher matcher = pattern.matcher(tempEmail);
		if (matcher.find()) {
			emailToUserName = matcher.group(1);
		}

		testDataCreator.createNewContact(emailToUserName);
		getActivity().finish();


	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// noop
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
		// noop
	}

	@Override
	public boolean onEditorAction(TextView textView, int actionId,
			KeyEvent keyEvent) {
		onClick(textView);
		return true;
	}

	@Override
	public void afterTextChanged(Editable editable) {
		// noop
	}

}
