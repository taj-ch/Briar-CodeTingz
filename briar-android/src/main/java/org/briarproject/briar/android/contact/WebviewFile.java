package org.briarproject.briar.android.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import org.briarproject.briar.R;

public class WebviewFile extends AppCompatActivity {

	String URL = "";
	String FILE_NAME="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		final WebView webview;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_file);
		Button download = (Button)findViewById(R.id.file_download_button);
		TextView filename = (TextView) findViewById(R.id.filenameTextView);
		Intent callingActivityIntent = getIntent();
		Bundle b = callingActivityIntent.getExtras();
		if (b != null) {
			URL = (String) b.get("url");
			FILE_NAME=(String) b.get("file_name");
		}
		filename.setText(FILE_NAME);
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
