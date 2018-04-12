package org.briarproject.briar.android;

@SuppressWarnings("PMD")
class AndroidEagerSingletons {

	static void initEagerSingletons(AndroidComponent c) {
		c.inject(new AppModule.EagerSingletons());
	}
}
