package com.example.proyectogrado;

/**
 import com.google.android.gms.maps.GoogleMap;
 import com.google.android.gms.maps.MapFragment;

 import android.os.Bundle;
 import android.support.v4.app.FragmentActivity;

 public class MapActivity extends FragmentActivity{

 private GoogleMap mMap;

 public void onCreate(Bundle savedInstanceState) {
 super.onCreate(savedInstanceState);
 setContentView(R.layout.map_activity);

 mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
 }


 }*/
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.webkit.SslErrorHandler;
import android.net.http.*;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MapActivity extends Activity {
	final Activity activity = this;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getWindow().requestFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.map_activity);

		WebView webView = (WebView) findViewById(R.id.webView1);
		// webView.setWebViewClient(new WebViewClient());
		// webView.addView(webView.getZoomControls());
		webView.getSettings().setJavaScriptEnabled(true);

		webView.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				activity.setTitle("Loading...");
				activity.setProgress(progress * 100);

				if (progress == 100)
					activity.setTitle("Done");
			}
		});

		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// Handle the error
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
		});

		// webView.loadUrl("http://developer.android.com");
		webView.loadUrl("https://www.google.com/maps/d/viewer?mid=zfzELMV3_ra0.kEkVaBI-ynsI");

	}
}
