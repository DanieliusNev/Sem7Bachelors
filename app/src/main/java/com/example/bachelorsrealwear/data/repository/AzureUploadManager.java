package com.example.bachelorsrealwear.data.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.example.bachelorsrealwear.domain.repository.CloudUploadRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AzureUploadManager implements CloudUploadRepository {

    private static final String CLIENT_ID = "Fake";
    private static final String CLIENT_SECRET = "Fake";
    private static final String TENANT_AUTHORITY = "https://login.microsoftonline.com/Fake";
    private static final String SHAREPOINT_URL = "https://2y7v5x.sharepoint.com/";
    private static final String UPLOAD_FOLDER = "sites/Fake/Shared Documents/Uploads";

    private final Context context;
    private final Queue<File> uploadQueue = new LinkedList<>();
    private boolean isUploading = false;
    private Retrofit retrofit;
    private String accessToken;

    public AzureUploadManager(Context context) {
        this.context = context.getApplicationContext();
        initializeRetrofit();
    }

    private void initializeRetrofit() {
        try {
            accessToken = authenticate();
            retrofit = new Retrofit.Builder()
                    .baseUrl(SHAREPOINT_URL)
                    .client(new OkHttpClient.Builder()
                            .addInterceptor(chain -> {
                                Request request = chain.request().newBuilder()
                                        .header("Authorization", "Bearer " + accessToken)
                                        .build();
                                return chain.proceed(request);
                            })
                            .build())
                    .build();
        } catch (Exception e) {
            showToast("Auth failed: " + e.getMessage());
        }
    }

    @Override
    public void uploadPdf(File file, String fileName) {
        if (isOnline()) {
            new Thread(() -> performUpload(file, fileName)).start();
        } else {
            uploadQueue.add(file);
            showToast("No internet. Queued upload for later.");
            retryUploadWhenOnline();
        }
    }

    private void performUpload(File file, String fileName) {
        try {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/pdf"), file);
            MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", fileName, requestBody);
            RequestBody fileNameBody = RequestBody.create(MediaType.parse("text/plain"), fileName);
            RequestBody folderPathBody = RequestBody.create(MediaType.parse("text/plain"), UPLOAD_FOLDER);

            SharePointApiService service = retrofit.create(SharePointApiService.class);
            Call<ResponseBody> call = service.uploadFile(fileNameBody, folderPathBody, filePart);
            Response<ResponseBody> response = call.execute();

            if (response.isSuccessful()) {
                showToast("Uploaded to Azure: " + fileName);
            } else {
                showToast("Upload failed for: " + fileName);
            }
        } catch (Exception e) {
            showToast("Upload error: " + e.getMessage());
        }
    }

    private String authenticate() throws Exception {
        AuthenticationContext context = null;
        AuthenticationResult result;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            context = new AuthenticationContext(TENANT_AUTHORITY, false, executorService);
            ClientCredential credential = new ClientCredential(CLIENT_ID, CLIENT_SECRET);
            Future<AuthenticationResult> future = context.acquireToken(SHAREPOINT_URL, credential, null);
            result = future.get(30, TimeUnit.SECONDS);
            return result.getAccessToken();
        } finally {
            executorService.shutdown();
        }
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }

    private void retryUploadWhenOnline() {
        if (isUploading) return;
        isUploading = true;

        new Thread(() -> {
            while (!uploadQueue.isEmpty()) {
                if (isOnline()) {
                    File file = uploadQueue.poll();
                    if (file != null) {
                        performUpload(file, file.getName());
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            isUploading = false;
        }).start();
    }

    private void showToast(String message) {
        new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
