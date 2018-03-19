package org.briarproject.briar.android.contact;

import org.briarproject.briar.android.keyagreement.AddContactFragment;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;


/**
 * Created by Mira on 2018-03-18.
 */

public class TestAddContact extends AddContactFragment{

	private AddContactFragment addContactFragment;

	@Before
	public void setUp() throws Exception{

	}

	@Test
	public void testIsEmailValidCorrect() {
		boolean result;

		result = isEmailValid("bob@hotmail.com");
		assertEquals(true, result);

		result = isEmailValid("bob@bob.bob");
		assertEquals(true, result);
	}

	@Test
	public void testIsEmailValidFalse() {
		boolean result;

		result = isEmailValid("bob");
		assertEquals(false, result);

		result = isEmailValid("bob@hotmail");
		assertEquals(false, result);

		result = isEmailValid("bob.hotmail");
		assertEquals(false, result);

		result = isEmailValid("bob@hotmail.1234567");
		assertEquals(false, result);
	}


}
