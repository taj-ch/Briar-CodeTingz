package org.briarproject.briar.android.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.briarproject.briar.R;
public class FullScreenImageActivity extends AppCompatActivity {

	String URL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fullscreenimage);
		ImageView fullScreenImageView = (ImageView) findViewById(R.id.imageFullView);
		Button download = (Button)findViewById(R.id.image_download_button);
		Intent callingActivityIntent= getIntent();
		Bundle b = callingActivityIntent.getExtras();
		
		if(b! = null){
			 URL =(String) b.get("url");
		}

		if(callingActivityIntent != null){
			//Picasso.with(this).load(URL).into(fullScreenImageView);
			Glide.with(this)
					.load(URL)
					.into(fullScreenImageView);
		}
		
		download.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Sender_Name = getIntent().getStringExtra("name");
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(URL));
				startActivity(i);
			}
		});

	}
}
