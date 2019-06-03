package com.example.receipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.receipt.helpers.MyHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextActivity extends BaseActivity implements View.OnClickListener {
	private static final String TAG = "TextActivity" ;
	private Bitmap mBitmap;
	private ImageView mImageView;
	private TextView mTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);

		mTextView = findViewById(R.id.text_view);
		mImageView = findViewById(R.id.image_view);
		findViewById(R.id.btn_device).setOnClickListener(this);
		findViewById(R.id.btn_cloud).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_device:
				if (mBitmap != null) {
					runTextRecognition();
				}
				break;
/*			case R.id.btn_cloud:
				if (mBitmap != null) {
					runCloudTextRecognition();
				}
				break;*/
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case RC_STORAGE_PERMS1:
				case RC_STORAGE_PERMS2:
					checkStoragePermission(requestCode);
					break;
				case RC_SELECT_PICTURE:
					Uri dataUri = data.getData();
					String path = MyHelper.getPath(this, dataUri);
					if (path == null) {
						mBitmap = MyHelper.resizeImage(imageFile, this, dataUri, mImageView);
					} else {
						mBitmap = MyHelper.resizeImage(imageFile, path, mImageView);
					}
					if (mBitmap != null) {
						mTextView.setText(null);
						mImageView.setImageBitmap(mBitmap);
					}
					break;
				case RC_TAKE_PICTURE:
					mBitmap = MyHelper.resizeImage(imageFile, imageFile.getPath(), mImageView);
					if (mBitmap != null) {
						mTextView.setText(null);
						mImageView.setImageBitmap(mBitmap);
					}
					break;
			}
		}
	}

	private void runTextRecognition() {
		FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);

		FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
		detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
			@Override
			public void onSuccess(FirebaseVisionText texts) {
				processTextRecognitionResult(texts);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				e.printStackTrace();
			}
		});
	}

/*	private void runCloudTextRecognition() {
		MyHelper.showDialog(this);

		FirebaseVisionCloudTextRecognizerOptions options = new FirebaseVisionCloudTextRecognizerOptions.Builder()
				.setLanguageHints(Arrays.asList("en", "hi"))
				.setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
				.build();

		FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);
		FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getCloudTextRecognizer(options);

		detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
			@Override
			public void onSuccess(FirebaseVisionText texts) {
				MyHelper.dismissDialog();
				processTextRecognitionResult(texts);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				MyHelper.dismissDialog();
				e.printStackTrace();
			}
		});
	}*/


	private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
		mTextView.setText(null);
		if (firebaseVisionText.getTextBlocks().size() == 0) {
			mTextView.setText(R.string.error_not_found);
			return;
		}
		/*for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
			mTextView.append(block.getText());
			*//*
			 * In case you want to extract each line
			for (FirebaseVisionText.Line line: block.getLines()) {
				for (FirebaseVisionText.Element element: line.getElements()) {
					mTextView.append(element.getText() + " ");
				}
			}
			*//*
		}*/

		String resultText = firebaseVisionText.getText();
		Log.d(TAG, "onSuccess: Here is the text from the receipt "+resultText);

		String regex="([0-9]+[.][0-9]+)";
		String input= resultText;

		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(input);

		float max =0;

		while(matcher.find())
		{
			String floatValues = matcher.group();
			float finalFloatValues = Float.parseFloat(floatValues);
			if(finalFloatValues>max){
				max = finalFloatValues;
			}
		}

		Log.d(TAG, "The Total Value is : "+max);
		Intent intent = getIntent();
		mTextView.setText("Total: " + max);

	}
	}

