package org.briarproject.briar.android.login;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.iid.FirebaseInstanceId;

import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.contact.UserDetails;
import org.briarproject.briar.android.util.UiUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInFragment extends SetupFragment {

	private final static String TAG = SignInFragment.class.getName();

	private TextInputLayout authorNameWrapper;
	private TextInputEditText authorNameInput;
	private TextInputLayout passwordWrapper;
	private TextInputEditText passwordInput;
	private Button signInButton;
	private Button createAccountButton;
	private Button resetAccountPassword;

	private FirebaseAuth mAuth;

	private String email;

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
		resetAccountPassword = v.findViewById(R.id.btn_reset_password);
		signInButton.setOnClickListener(this);
		createAccountButton.setOnClickListener(this);
		resetAccountPassword.setOnClickListener(this);

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
				email = authorNameInput.getText().toString();
				String password = passwordInput.getText().toString();
				if(email != null && !email.isEmpty() && password !=null && !password.isEmpty()){
					signInButton.setClickable(false);
					openAccount(email, password);
				} else{
					Toast.makeText(getActivity(), "Fields can't be blank!", Toast.LENGTH_LONG)
							.show();
				}
				break;
			case R.id.btn_create_account:
				onCreateAccountClick();
				break;
			case R.id.btn_reset_password:
				email = authorNameInput.getText().toString();
				if(email==null || email.isEmpty()) {
					Toast.makeText(getActivity(),
							"Fill in the email above", Toast.LENGTH_LONG)
							.show();
				} else{
					userExists(email);
				}
				break;
			default:
				break;
		}

	}


	public void openAccount(String email, String password) {
		String firstPartOfEmail = email.split("\\@")[0];
		String dbSafeEmail = email.replaceAll("\\.", ",");
		UserDetails.changeUsername(dbSafeEmail);
		Log.d("ChangeUsername:", dbSafeEmail);

		//authenticate user
		mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						//if sign in fails, display a message to the user. If sign in succeeds
						//the mAuth state listener will be notified and logic to handle the
						//signed in user can be handled in the listener.
						if (task.isSuccessful()){
							String tkn = FirebaseInstanceId.getInstance().getToken();
							Log.d("TOKEN_REFRESH_login", tkn);

							setupController.setAuthorName(authorNameInput.getText().toString());
							setupController.setPassword(passwordInput.getText().toString());
							if (!setupController.needToShowDozeFragment()) {
								signInButton.setClickable(false);
							}
							setupController.setPassword(password);

							setupController.showDozeOrCreateAccount();

						} else {
							tryAgain();
						}
					}
				});

	}

	private void tryAgain() {
		UiUtils.setError(passwordWrapper, getString(R.string.try_again), true);
		signInButton.setClickable(true);

	}
	
	public void onCreateAccountClick() {
		//go to the Create Account page (AuthorNameFragment.java)
		showNextFragment(AuthorNameFragment.newInstance());

	}

	// Send a reset email link to user
	private void resetPassword(String emailAddress){
		mAuth.sendPasswordResetEmail(emailAddress)
				.addOnCompleteListener(new OnCompleteListener<Void>() {
					@Override
					public void onComplete(@NonNull Task<Void> task) {
						if (task.isSuccessful()) {
							Log.d(TAG, "Email sent.");
							Toast.makeText(getActivity(),
									"Email Reset Password sent", Toast.LENGTH_LONG)
									.show();
						}
					}
				});
	}

	// Check if user exists in Firebase and then send reset link
	private void userExists(String email) {
		if(!isEmailValid(email)){
			UiUtils.setError(authorNameWrapper, "Enter a valid Email", true);
		} else {
			//checks database for existing user
			mAuth.fetchProvidersForEmail(email).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
				@Override
				public void onComplete(@NonNull Task<ProviderQueryResult> task) {
					if (task.getResult().getProviders().size() <= 0) {
						UiUtils.setError(authorNameWrapper,
								"This email is not associated to a user",
								true);
					} else {
							resetPassword(email);
					}
				}

			});
		}
	}

	//checks email pattern
	private boolean isEmailValid(String email) {
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}
}
