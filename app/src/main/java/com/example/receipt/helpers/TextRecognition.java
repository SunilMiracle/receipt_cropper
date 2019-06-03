package com.example.receipt.helpers;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.receipt.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextRecognition  extends AppCompatActivity {
	private static final String TAG = "TextActivity" ;

	//	private Bitmap mBitmap;
	//	private ImageView mImageView;
	//	private TextView mTextView;
	private String total = null;

	public String getTotal() {
		return total;
	}

	public void runTextRecognition(Bitmap mBitmap) {

		FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);

		FirebaseVisionTextRecognizer texdDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
		texdDetector.processImage(image).
				addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
			@Override
			public void onSuccess(FirebaseVisionText texts) {
				total = processTextRecognitionResult(texts);
			}
		}).addOnFailureListener(new OnFailureListener() {
			@Override
			public void onFailure(@NonNull Exception e) {
				e.printStackTrace();
			}
		});
	}

	private String processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {

		if (firebaseVisionText.getTextBlocks().size() == 0) {
			Log.i(TAG, "error_not_found" );
		}

		String resultText = firebaseVisionText.getText();
//		Log.i(TAG, "onSuccess: Here is the text from the receipt "+ resultText);

		String regex="([0-9]+[.][0-9]+)";

		Pattern pattern=Pattern.compile(regex);
		Matcher matcher=pattern.matcher(resultText);

		float max =0;

		while(matcher.find())
		{
			String floatValues = matcher.group();
			float finalFloatValues = Float.parseFloat(floatValues);
			if(finalFloatValues>max){
				max = finalFloatValues;
			}
		}

		Log.i(TAG, "The Total Value is : "+ max);
		return String.valueOf(max);

	}
}

