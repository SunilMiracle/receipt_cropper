package com.example.receipt;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

//import com.example.receipt.helpers.TextRecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

  private static String TAG = "MainActivity";
  private String total = null;
  TextView totalView;


  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    totalView = findViewById(R.id.totalTV);
  }

  /** Start pick image activity with chooser. */
  public void onSelectImageClick(View view) {
    CropImage
            .activity()
            .setGuidelines(CropImageView.Guidelines.ON)
            .setCropShape(CropImageView.CropShape.RECTANGLE)
            .setCropMenuCropButtonTitle("Done")
            .start(this);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    // handle result of CropImageActivity
    if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
      CropImage.ActivityResult result = CropImage.getActivityResult(data);
      Log.i(TAG, result.getOriginalUri().toString());
      Uri imgResultURI = result.getUri();


      Bitmap original = null;
      try {
        original = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgResultURI);
      } catch (IOException e) {
        e.printStackTrace();
      }

//      Bitmap original = Bitmap.createBitmap(result.getOriginalUri().toString())
//      saveCaptureScreen( original);


      if (resultCode == RESULT_OK) {
        ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
        Toast.makeText(
                this, "Receipt Cropping successful, Extracting total value " , Toast.LENGTH_LONG)
            .show();

      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
      }

      Log.i(TAG, "Croppped image Rect: " + result.getCropRect().toString() );

//      Bitmap croppedImage = BitmapFactory.decodeFile(result.getUri().toString());

//      Bitmap croppedImage = result.getUri();

//      Log.i(TAG, "Croppped image: " + croppedImage);

//      String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReceiptAnalysis/" +  "recpt2.jpg";
//      BitmapFactory.decodeFile(mPath)

      // perform text recogtion on cropped image
//      TextRecognition textRecog = new TextRecognition();
      runTextRecognition(original);

      while (total != null){
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

    }
  }

  public void runTextRecognition(Bitmap mBitmap) {

    FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mBitmap);

    FirebaseVisionTextRecognizer texdDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
    texdDetector.processImage(image).
            addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
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

  private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {

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

    Toast.makeText(
            this, "Total: " + max , Toast.LENGTH_LONG)
            .show();

    Log.i(TAG, "The Total Value is : "+ max);


    totalView.setText("Total: " + String.valueOf(max));
    totalView.setVisibility(View.VISIBLE);

  }

  private void saveCaptureScreen(Bitmap capturedImage){
    Log.i(TAG, "Saving captured image to file" + Environment.getExternalStorageDirectory().getAbsolutePath());
    Date now = new Date();
    android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

    String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/ReceiptAnalysis";
    File imageFile = new File(mPath + "/" + now + ".jpg");
    FileOutputStream outputStream = null;

    try {
      outputStream = new FileOutputStream(imageFile);
      int quality = 100;
      capturedImage.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
