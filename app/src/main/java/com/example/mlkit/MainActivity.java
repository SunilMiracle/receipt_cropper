package com.example.mlkit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

//import androidx.appcompat.app.AppCompatActivity;
import android.support.v7.app.AppCompatActivity;


import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import com.example.mlkit.helpers.TextRecognition;

import java.net.URI;

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
      Uri imgResultURI = CropImage.getCaptureImageOutputUri(this);

      if (resultCode == RESULT_OK) {
        ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
        Toast.makeText(
                this, "Receipt Cropping successful, Extracting total value " , Toast.LENGTH_LONG)
            .show();


      } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
        Toast.makeText(this, "Cropping failed: " + result.getError(), Toast.LENGTH_LONG).show();
      }

      Log.i(TAG, "Croppped image Uri: " + imgResultURI.toString());

      Bitmap croppedImage = BitmapFactory.decodeFile(imgResultURI.toString());

      Log.i(TAG, "Croppped image: " + croppedImage);

      // perform text recogtion on cropped image
      TextRecognition textRecog = new TextRecognition();
      textRecog.runTextRecognition(croppedImage);

    }
  }
}
