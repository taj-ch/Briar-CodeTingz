package org.briarproject.briar.sharing;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.client.ClientHelper;
import org.briarproject.bramble.api.data.BdfList;
import org.briarproject.bramble.api.nullsafety.NotNullByDefault;
import org.briarproject.briar.api.forum.Forum;
import org.briarproject.briar.api.forum.ForumFactory;

import javax.annotation.concurrent.Immutable;
import javax.inject.Inject;

@Immutable
@NotNullByDefault
class ForumMessageParserImpl extends MessageParserImpl<Forum> {

	private final ForumFactory forumFactory;

	@Inject
	ForumMessageParserImpl(ClientHelper clientHelper,
			ForumFactory forumFactory) {
		super(clientHelper);
		this.forumFactory = forumFactory;
	}

	@Override
	protected Forum createShareable(BdfList descriptor)
			throws FormatException {
		String name = descriptor.getString(0);
		String description = descriptor.getString(1);
		byte[] salt = descriptor.getRaw(2);
		return forumFactory.createForum(name,description, salt);
	}

}
