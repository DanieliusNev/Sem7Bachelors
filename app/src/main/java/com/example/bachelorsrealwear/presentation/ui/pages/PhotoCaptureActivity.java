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
        Button finishButton = findViewById(R.id.nextStep);
        Button backBtn = findViewById(R.id.backCheck);
        LinearLayout container = findViewById(R.id.photo_preview_container);

        takePhotoButton.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_CODE);
            } else {
                openCamera();
            }
        });

        viewModel.getPhotoUris().observe(this, uris -> {
            container.removeAllViews();

            for (int i = 0; i < uris.size(); i++) {
                Uri uri = uris.get(i);

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

        finishButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, PdfSaveActivity.class);
            intent.putExtra("template_index", getIntent().getIntExtra("template_index", 0));
            startActivity(intent);
            finish();
        });

        backBtn.setOnClickListener(v -> {
            Intent backIntent = new Intent(this, ChecklistPageActivity.class);
            backIntent.putExtra("template_index", getIntent().getIntExtra("template_index", 0));
            backIntent.putExtra("page_index", 5);
            startActivity(backIntent);
            finish();
        });
    }

    private void openCamera() {
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

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        } catch (Exception e) {
            Log.e("CAMERA_FLOW", "Exception during camera intent setup", e);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) storageDir.mkdirs();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK && photoUri != null) {
            viewModel.addPhoto(photoUri);
        }
    }

    @SuppressWarnings("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                Log.w("CAMERA_FLOW", "Camera permission denied");
            }
        }
    }
}
