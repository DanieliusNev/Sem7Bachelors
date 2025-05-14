package com.example.bachelorsrealwear.presentation.ui.pages;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.presentation.ui.viewModel.PhotoViewModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PhotoCaptureActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private Uri photoUri;
    private String currentPhotoPath;
    private PhotoViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CAMERA_FLOW", "onCreate: Activity started");

        setContentView(R.layout.activity_photo_capture);

        viewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        Button takePhotoButton = findViewById(R.id.btn_take_photo);
        LinearLayout container = findViewById(R.id.photo_preview_container);

        takePhotoButton.setOnClickListener(view -> {
            Log.d("CAMERA_FLOW", "Take Photo button clicked");

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("CAMERA_FLOW", "Camera permission not granted. Requesting...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            } else {
                Log.d("CAMERA_FLOW", "Camera permission already granted. Opening camera...");
                openCamera();
            }
        });

        viewModel.getPhotoUris().observe(this, uris -> {
            container.removeAllViews();
            for (Uri uri : uris) {
                LinearLayout wrapper = new LinearLayout(this);
                wrapper.setOrientation(LinearLayout.VERTICAL);
                wrapper.setPadding(8, 8, 8, 8);

                ImageView img = new ImageView(this);
                img.setLayoutParams(new LinearLayout.LayoutParams(200, 200));
                img.setImageURI(uri);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                Button deleteBtn = new Button(this);
                deleteBtn.setText("Delete");
                deleteBtn.setOnClickListener(v -> viewModel.removePhoto(uri));

                wrapper.addView(img);
                wrapper.addView(deleteBtn);

                container.addView(wrapper);
            }

            takePhotoButton.setEnabled(uris.size() < 6);
        });
        Button backBtn = findViewById(R.id.backCheck);
        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(this, ChecklistPageActivity.class);
            backIntent.putExtra("template_index", getIntent().getIntExtra("template_index", 0));
            backIntent.putExtra("page_index", 5); // Page 6 index
            startActivity(backIntent);
            finish();
        });

    }

    private void openCamera() {
        Log.d("CAMERA_FLOW", "openCamera() called");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) == null) {
            Log.e("CAMERA_FLOW", "No camera app available!");
            return;
        }

        try {
            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(
                        this,
                        getPackageName() + ".provider",
                        photoFile
                );
                currentPhotoPath = photoFile.getAbsolutePath();

                Log.d("CAMERA_FLOW", "Photo file created at: " + currentPhotoPath);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Log.e("CAMERA_FLOW", "Photo file creation returned null");
            }
        } catch (Exception e) {
            Log.e("CAMERA_FLOW", "Exception during camera intent setup", e);
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
        }
    }

    private File createImageFile() throws IOException {
        Log.d("CAMERA_FLOW", "createImageFile() called");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) storageDir.mkdirs();
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d("CAMERA_FLOW", "Temporary file path: " + image.getAbsolutePath());
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("CAMERA_FLOW", "onActivityResult: req=" + requestCode + " result=" + resultCode);
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (photoUri != null) {
                Log.d("CAMERA_FLOW", "Adding photo to ViewModel: " + photoUri);
                viewModel.addPhoto(photoUri);
            } else if (data != null && data.getExtras() != null) {
                Log.d("CAMERA_FLOW", "Fallback: no URI but thumbnail available");
                // Optional: Handle thumbnail case if needed
            } else {
                Log.e("CAMERA_FLOW", "No photoUri or data in result");
            }
        }
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.d("CAMERA_FLOW", "onRequestPermissionsResult: code=" + requestCode);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CAMERA_FLOW", "Camera permission granted");
                openCamera();
            } else {
                Log.w("CAMERA_FLOW", "Camera permission denied");
            }
        }
    }
}
