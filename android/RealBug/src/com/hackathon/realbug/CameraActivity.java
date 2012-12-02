package com.hackathon.realbug;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.realbug.R;


public class CameraActivity extends SherlockActivity {
 
	private Bitmap mImageBitmap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);
		
		Button btnSend = (Button)findViewById(R.id.btn_send);
		Button btnCancel = (Button)findViewById(R.id.btn_cancel);
		TextView txtDescription = (TextView)findViewById(R.id.description);
		ImageView imgViewCapture = (ImageView)findViewById(R.id.captureView);
		
		Intent intent = getIntent();
		mImageBitmap = (Bitmap)intent.getExtras().get("data");
		imgViewCapture.setImageBitmap(mImageBitmap);
		imgViewCapture.setVisibility(View.VISIBLE);
		
	}
	
	public void btnSend(View view){

		
	}
	
	public void btnCancel(View view){
		
	}
}
