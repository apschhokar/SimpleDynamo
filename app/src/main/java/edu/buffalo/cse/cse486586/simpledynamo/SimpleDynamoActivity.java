package edu.buffalo.cse.cse486586.simpledynamo;

import android.content.ContentResolver;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SimpleDynamoActivity extends Activity {
	static final String TAG = SimpleDynamoProvider.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_dynamo);
    
		TextView tv = (TextView) findViewById(R.id.textView1);
                tv.setMovementMethod(new ScrollingMovementMethod());



		final Button Ldump = (Button) findViewById(R.id.extract);
		Ldump.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Log.e(TAG, "send toh click hua Ldump ");

				ContentResolver mContentResolver = getContentResolver();
				Uri mUri = buildUri("content", "edu.buffalo.cse.cse486586.simpledynamo.provider");

				Cursor resultCursor = mContentResolver.query(mUri, null,
						"@", null, null);
				Log.e("Cursor ", DatabaseUtils.dumpCursorToString(resultCursor));

			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.simple_dynamo, menu);
		return true;
	}
	
	public void onStop() {
        super.onStop();
	    Log.v("Test", "onStop()");
	}


	private Uri buildUri(String scheme, String authority) {
		Uri.Builder uriBuilder = new Uri.Builder();
		uriBuilder.authority(authority);
		uriBuilder.scheme(scheme);
		return uriBuilder.build();
	}


}
