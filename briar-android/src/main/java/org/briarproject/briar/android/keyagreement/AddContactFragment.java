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

import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.contact.UserDetails;
import org.briarproject.briar.android.fragment.BaseFragment;
import org.briarproject.briar.android.login.AuthorNameFragment;
import org.briarproject.briar.android.util.UiUtils;
import android.text.TextWatcher;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class AddContactFragment extends BaseFragment implements TextWatcher,
		OnEditorActionListener, View.OnClickListener {

	private final static String TAG = AddContactFragment.class.getName();


	private TextInputEditText emailInput;
	private Button addContactButton;

	private FirebaseAuth mAuth;


	public static AddContactFragment newInstance() {
		return new AddContactFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle("Add Email Contact");
		View v = inflater.inflate(R.layout.activity_add_contact_by_email,
				container, false);
		emailInput = v.findViewById(R.id.edit_email);
		addContactButton = v.findViewById(R.id.btn_add_by_email);

		emailInput.setOnClickListener(this);
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
