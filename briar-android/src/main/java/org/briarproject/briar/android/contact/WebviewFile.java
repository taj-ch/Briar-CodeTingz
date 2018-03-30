package org.briarproject.briar.android.contact;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.briarproject.briar.R;

public class WebviewFile extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_webview_file);

		final WebView webview;

		final String URL;

		webview = (WebView) findViewById(R.id.webview1);
		webview.setWebViewClient(new AppWebViewClients());
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setUseWideViewPort(true);
		webview.loadUrl("http://docs.google.com/gview?embedded=true&url="
				+ URL);


	}

	public class AppWebViewClients extends WebViewClient {



		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

		}

	}
