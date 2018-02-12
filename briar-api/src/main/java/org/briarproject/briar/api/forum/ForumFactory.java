package org.briarproject.briar.api.forum;

import org.briarproject.bramble.api.nullsafety.NotNullByDefault;

@NotNullByDefault
public interface ForumFactory {

	/**
	 * Creates a forum with the given name and description
	 */
	Forum createForum(String name, String desc);

	/**
	 * Creates a forum with the given name, description, and salt.
	 */
	Forum createForum(String name, String desc, byte[] salt);



}
