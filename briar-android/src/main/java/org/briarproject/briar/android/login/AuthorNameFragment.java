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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import org.briarproject.bramble.util.StringUtils;
import org.briarproject.briar.R;
import org.briarproject.briar.android.activity.ActivityComponent;

import org.briarproject.briar.android.activity.BaseActivity;
import org.briarproject.briar.android.util.UiUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NEXT;
import static android.view.inputmethod.EditorInfo.IME_ACTION_NONE;
import static org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK;
import static org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH;
import static org.briarproject.briar.android.util.UiUtils.setError;

public class AuthorNameFragment extends SetupFragment {

	private final static String TAG = AuthorNameFragment.class.getName();

	private TextInputLayout authorNameWrapper;
	private TextInputEditText authorNameInput;
	private TextInputLayout passwordWrapper;
	private TextInputLayout passwordConfirmWrapper;
	private TextInputEditText passwordInput;
	private TextInputEditText passwordConfirm;
	private StrengthMeter strengthMeter;
	private Button signUpButton;
	private Button logInButton;

	private FirebaseAuth mAuth;


	public static AuthorNameFragment newInstance() {
		return new AuthorNameFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle(getString(R.string.setup_title));
		View v = inflater.inflate(R.layout.activity_email_password_create,
				container, false);
		authorNameWrapper = v.findViewById(R.id.email_entry_wrapper);
		authorNameInput = v.findViewById(R.id.email_entry);
		passwordWrapper = v.findViewById(R.id.password_entry_wrapper);
		passwordConfirmWrapper = v.findViewById(R.id.password_confirm_wrapper);
		passwordInput = v.findViewById(R.id.password_entry);
		passwordConfirm = v.findViewById(R.id.password_confirm);
		signUpButton = v.findViewById(R.id.next);
		logInButton = v.findViewById(R.id.btn_log_in);
		strengthMeter = v.findViewById(R.id.strength_meter);

		authorNameInput.addTextChangedListener(this);
		passwordInput.addTextChangedListener(this);
		passwordConfirm.addTextChangedListener(this);
		signUpButton.setOnClickListener(this);
		logInButton.setOnClickListener(this);
		FirebaseApp.initializeApp(this.getContext());
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser currentUser = mAuth.getCurrentUser();

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
		String email = authorNameInput.getText().toString();
		boolean error = isEmailValid(email);
		boolean enabled = error;
		UiUtils.setError(authorNameWrapper, "Not a valid email",
				email.length() > 0 && !error);

		authorNameInput
				.setImeOptions(enabled ? IME_ACTION_NEXT : IME_ACTION_NONE);
		authorNameInput.setOnEditorActionListener(enabled ? this : null);

		String password1 = passwordInput.getText().toString();
		String password2 = passwordConfirm.getText().toString();
		boolean passwordsMatch = password2.equals(password1);

		strengthMeter
				.setVisibility(password1.length() > 0 ? VISIBLE : INVISIBLE);
		float strength = setupController.estimatePasswordStrength(password1);
		strengthMeter.setStrength(strength);
		boolean strongEnough = strength >= QUITE_WEAK;

		UiUtils.setError(passwordWrapper,
				getString(R.string.password_too_weak),
				password1.length() > 0 && !strongEnough);
		UiUtils.setError(passwordConfirmWrapper,
				getString(R.string.passwords_do_not_match),
				password2.length() > 0 && !passwordsMatch);

		boolean enabled2 = strongEnough && passwordsMatch;
		boolean enableButton = enabled && enabled2;
		signUpButton.setEnabled(enableButton);
		passwordConfirm.setOnEditorActionListener(enabled2 ? this : null);

	}

	@Override
	public void onClick(View view) {
		switch(view.getId()) {
			case R.id.next:
				String email = authorNameInput.getText().toString();
				String password = passwordInput.getText().toString();
				createAccount(email, password);
				FirebaseUser currentUser = mAuth.getCurrentUser();
				setupController
						.setAuthorName(authorNameInput.getText().toString());

				setupController.setPassword(passwordInput.getText().toString());
				if (!setupController.needToShowDozeFragment()) {
					signUpButton.setVisibility(INVISIBLE);
				}
				setupController.setPassword(password);
				setupController.showDozeOrCreateAccount();
			case R.id.btn_log_in:
				onLogInClick();
		}
	}

	public static boolean isEmailValid(String email) {
		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public void createAccount(String email, String password) {
		mAuth.createUserWithEmailAndPassword(email, password);

		String tkn = FirebaseInstanceId.getInstance().getToken();
		Log.d("TOKEN_REFRESH_create", tkn);
	}

	public void onLogInClick() {
		//go to the Log In page (SignInFragment.java)
		showNextFragment(SignInFragment.newInstance());
	}
}
