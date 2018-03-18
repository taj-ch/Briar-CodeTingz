package org.briarproject.briar.android.login;

import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;

import org.briarproject.briar.R;
import org.briarproject.briar.android.chat.RxFirebaseAuth;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Mira on 2018-03-17.
 */

@RunWith(MockitoJUnitRunner.class)
public class SignInFragmentTest extends SignInFragment {

	@Mock
	private FirebaseAuth mockAuth;

	@Mock
	private Task<AuthResult> mockAuthTask;

	@Mock
	private Task<ProviderQueryResult> mockProviderQueryResultTask;

	@Mock
	private Task<Void> mockVoidTask;

	@Mock
	private AuthResult mockAuthResult;

	@Mock
	private ProviderQueryResult mockProviderQueryResult;

	@Mock
	private DataSnapshot mockDatabaseDataSnapshot;

	@Mock
	private AuthCredential mockCredentials;

	@Mock
	private FirebaseUser mockUser;

	@Captor
	private ArgumentCaptor<OnCompleteListener> testOnCompleteListener;

	@Captor
	private ArgumentCaptor<OnSuccessListener> testOnSuccessListener;

	@Captor
	private ArgumentCaptor<OnFailureListener> testOnFailureListener;

	private Void mockRes = null;



	@Before
	public void setUp(){
		setupTask(mockAuthTask);
		setupTask(mockProviderQueryResultTask);
		setupTask(mockVoidTask);

		//to be used with testOpenAccount()
		when(mockAuth.signInWithEmailAndPassword("email", "password")).thenReturn(mockAuthTask);

		//
		when(mockAuth.createUserWithEmailAndPassword("email", "password")).thenReturn(mockAuthTask);

		//when(mockAuth.getCurrentUser()).thenReturn(mockUser);
	}

	private <T> void setupTask(Task<T> task) {
		when(task.addOnCompleteListener(testOnCompleteListener.capture())).thenReturn(task);
		when(task.addOnSuccessListener(testOnSuccessListener.capture())).thenReturn(task);
		when(task.addOnFailureListener(testOnFailureListener.capture())).thenReturn(task);
	}
// THIS IS THE TEST THAT FAILS AND GIVING US AN ISSUE
	@Test
	public void testOpenAccountMethodCalled(){
		View view = Mockito.mock(View.class);
		SignInFragment signInFragment = Mockito.mock(SignInFragment.class);
		TextInputEditText authorNameInput = Mockito.mock(TextInputEditText.class);
		TextInputEditText passwordInput = Mockito.mock(TextInputEditText.class);
		Button signInButton = Mockito.mock(Button.class);

		view.setId(0x7f09005d);
		//when(R.id.btn_sign_in).thenReturn(5);
		when(view.getId()).thenReturn(0x7f09005d);
		when(authorNameInput.getText().toString()).thenReturn("email");
		when(passwordInput.getText().toString()).thenReturn("password");
		//when(passwordInput.getText().toString()).thenReturn(1);
		//view.setId(R.id.btn_sign_in);
		signInFragment.onClick(view);
		verify(signInFragment).openAccount("email", "password");
	}


	@Test
	public void testCredentialValidity_True() throws InterruptedException {

		TestSubscriber<AuthResult> testSubscriber = new TestSubscriber<>();
		RxFirebaseAuth.signInWithEmailAndPassword(mockAuth, "email", "password")
				.subscribeOn(Schedulers.immediate())
				.subscribe(testSubscriber);

		testOnSuccessListener.getValue().onSuccess(mockAuthResult);
		testOnCompleteListener.getValue().onComplete(mockAuthTask);

		verify(mockAuth).signInWithEmailAndPassword(eq("email"), eq("password"));

		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		testSubscriber.assertReceivedOnNext(Collections.singletonList(mockAuthResult));
		testSubscriber.assertCompleted();
		testSubscriber.unsubscribe();
	}

	@Test
	public void testCredentialValidity_False() throws InterruptedException {

		TestSubscriber<AuthResult> testSubscriber = new TestSubscriber<>();
		RxFirebaseAuth.signInWithEmailAndPassword(mockAuth, "email", "password")
				.subscribeOn(Schedulers.immediate())
				.subscribe(testSubscriber);

		Exception e = new Exception("something bad happened");
		testOnFailureListener.getValue().onFailure(e);

		verify(mockAuth).signInWithEmailAndPassword(eq("email"), eq("password"));

		testSubscriber.assertError(e);
		testSubscriber.assertNotCompleted();
		testSubscriber.unsubscribe();
	}

	@Test
	public void createUserWithEmailAndPassword() throws InterruptedException {

		TestSubscriber<AuthResult> testSubscriber = new TestSubscriber<>();
		RxFirebaseAuth.createUserWithEmailAndPassword(mockAuth, "email", "password")
				.subscribeOn(Schedulers.immediate())
				.subscribe(testSubscriber);

		testOnSuccessListener.getValue().onSuccess(mockAuthResult);
		testOnCompleteListener.getValue().onComplete(mockAuthTask);

		verify(mockAuth).createUserWithEmailAndPassword(eq("email"), eq("password"));

		testSubscriber.assertNoErrors();
		testSubscriber.assertValueCount(1);
		testSubscriber.assertReceivedOnNext(Collections.singletonList(mockAuthResult));
		testSubscriber.assertCompleted();
		testSubscriber.unsubscribe();
	}


}
