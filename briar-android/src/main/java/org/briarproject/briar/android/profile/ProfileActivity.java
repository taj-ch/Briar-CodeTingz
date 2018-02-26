package org.briarproject.briar.android.profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import org.briarproject.briar.R;

import org.briarproject.briar.android.activity.ActivityComponent;
import org.briarproject.briar.android.activity.BriarActivity;
import org.briarproject.briar.android.fragment.BaseFragment;

public class ProfileActivity extends BriarActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile);
	}

	@Override
	public void injectActivity(ActivityComponent component) {
		component.inject(this);
	}
}