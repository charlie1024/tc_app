package com.timestamp.calculator;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class HelpActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView tv = (TextView) findViewById(R.id.abText);
		tv.setText(R.string.help);

		Button btSendMail = (Button) findViewById(R.id.btSendMail);
		btSendMail.setText(R.string.seeDetails);
		btSendMail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Uri URI = Uri.parse("http://developer.android.com/reference/java/text/SimpleDateFormat.html");
				Intent intent = new Intent(Intent.ACTION_VIEW, URI);
				startActivity(intent);
			}
		});
	
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.about, menu);
        return true;
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		
			case R.id.menu_back_main:
				finish();
				break;
				
			default:
				break;
				
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	
}
