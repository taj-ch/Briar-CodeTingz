package org.briarproject.briar.android.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.contact.UserDetails;
import org.briarproject.briar.android.controller.handler.UiResultHandler;
import org.briarproject.briar.android.profile.ProfileDb;
import org.briarproject.briar.android.util.UiUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NONE;
import static org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK;

public class SignInFragment extends SetupFragment {

	private final static String TAG = SignInFragment.class.getName();

	private TextInputLayout authorNameWrapper;
	private TextInputEditText authorNameInput;
	private TextInputLayout passwordWrapper;
	private TextInputEditText passwordInput;
	private Button signInButton;
	private Button createAccountButton;

	private FirebaseAuth mAuth;


	public static SignInFragment newInstance() {
		return new SignInFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(getString(R.string.setup_title));
		View v = inflater.inflate(R.layout.activity_email_password_login,
				container, false);
		authorNameWrapper = v.findViewById(R.id.email_layout);
		authorNameInput = v.findViewById(R.id.edit_email);
		passwordWrapper = v.findViewById(R.id.password_layout);
		passwordInput = v.findViewById(R.id.edit_password);
		signInButton = v.findViewById(R.id.btn_sign_in);
		createAccountButton = v.findViewById(R.id.btn_create_account);

		signInButton.setOnClickListener(this);
		createAccountButton.setOnClickListener(this);

		FirebaseApp.initializeApp(this.getContext());
		mAuth = FirebaseAuth.getInstance();

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
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.btn_sign_in:
				String email = authorNameInput.getText().toString();
				String password = passwordInput.getText().toString();
				signInButton.setVisibility(INVISIBLE);
				openAccount(email, password);
			case R.id.btn_create_account:
				onCreateAccountClick();
		}

	}


	public void openAccount(String email, String password) {
		String firstPartOfEmail = email.split("\\@")[0];
		UserDetails.changeUsername(firstPartOfEmail);
		Log.d("ChangeUsername:", firstPartOfEmail);

		//authenticate user
		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						//if sign in fails, display a message to the user. If sign in succeeds
						//the mAuth state listener will be notified and logic to handle the
						//signed in user can be handled in the listener.
						if (task.isSuccessful()){
							//sign in successful

							// This will set the nickname so that the profile page can use it
							ProfileDb profileDb = new ProfileDb(getActivity());
							profileDb.setProfileAuthorName(email);
							String tkn = FirebaseInstanceId.getInstance().getToken();
							Log.d("TOKEN_REFRESH_login", tkn);

							setupController.setAuthorName(authorNameInput.getText().toString());
							setupController.setPassword(passwordInput.getText().toString());
							if (!setupController.needToShowDozeFragment()) {
								signInButton.setVisibility(INVISIBLE);
							}
							setupController.setPassword(password);

							setupController.showDozeOrCreateAccount();

						} else {
							//there was an error
							tryAgain();
						}
					}
				});

	}

	private void tryAgain() {
		UiUtils.setError(passwordWrapper, getString(R.string.try_again), true);
		signInButton.setVisibility(VISIBLE);

	}

	public void onCreateAccountClick() {
		//go to the Create Account page (AuthorNameFragment.java)
		showNextFragment(AuthorNameFragment.newInstance());

	}
}
