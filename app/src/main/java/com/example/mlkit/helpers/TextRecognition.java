package com.example.mlkit.helpers;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;

import android.widget.ImageView;
import android.widget.TextView;

import com.example.mlkit.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextRecognition {
	private static final String TAG = "TextActivity" ;
//	private Bitmap mBitmap;
//	private ImageView mImageView;
//	private TextView mTextView;

	public void runTextRecognition(Bitmap mBitmap) {
		Log.i(TAG, "Bitmap received " + mBitmap.toString());

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

	private String processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
//		mTextView.setText(null);

		if (firebaseVisionText.getTextBlocks().size() == 0) {
			return "error_not_found" ;
		}

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
		return "The Total Value is : " + max;
	}
	}

