package com.example.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.example.mlkit.helpers.TextRecognition;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

  private  static String TAG = "MainActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
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

      Log.i(TAG, "Croppped image Rect: " + result.getCropRect().toString());

//      Bitmap croppedImage = BitmapFactory.decodeFile(result.getUri().toString());

//      Bitmap croppedImage = result.getUri();

//      Log.i(TAG, "Croppped image: " + croppedImage);

//      String mPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReceiptAnalysis/" +  "recpt2.jpg";
//      BitmapFactory.decodeFile(mPath)

      // perform text recogtion on cropped image
      TextRecognition textRecog = new TextRecognition();
      textRecog.runTextRecognition(original);

      Toast.makeText(
              this, textRecog.total , Toast.LENGTH_LONG)
              .show();

    }
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
