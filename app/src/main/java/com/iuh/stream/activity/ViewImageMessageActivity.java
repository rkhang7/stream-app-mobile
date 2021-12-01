package com.iuh.stream.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.iuh.stream.R;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.utils.MyConstant;

import java.io.File;
import java.util.List;

public class ViewImageMessageActivity extends AppCompatActivity {
    private ImageButton backBtn, downloadBtn;
    private TextView nameTv;
    private PhotoView imageMessageIv;
    private String imageUrl;
    private RelativeLayout toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_image_message);

        addControls();
        addEvents();
    }

    private void addEvents() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageMessageIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(toolbar.getVisibility() == View.INVISIBLE){
                    toolbar.setVisibility(View.VISIBLE);
                }
                else if(toolbar.getVisibility() == View.VISIBLE){
                    toolbar.setVisibility(View.INVISIBLE);
                }
            }
        });

        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionListener permissionlistener = new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        String generateName = "IMG_" + System.currentTimeMillis();
                        downloadImageNew(generateName, imageUrl);
                    }

                    @Override
                    public void onPermissionDenied(List<String> deniedPermissions) {
                        Toast.makeText(getApplicationContext(), "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                    }


                };
                TedPermission.create()
                        .setPermissionListener(permissionlistener)
                        .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                        .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();

            }
        });
    }

    private void addControls() {
        backBtn = findViewById(R.id.back_btn);
        downloadBtn = findViewById(R.id.download_btn);
        nameTv = findViewById(R.id.name_tv);
        imageMessageIv = findViewById(R.id.image_message_iv);
        toolbar = findViewById(R.id.toolbar);

        imageUrl = getIntent().getStringExtra(MyConstant.CONTENT_KEY);
        Glide.with(this).load(imageUrl).into(imageMessageIv);
    }

    private void downloadImageNew(String filename, String downloadUrlOfImage){
        try{
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            CustomAlert.showToast(this, CustomAlert.INFO, "Đã lưu thành công");
        }catch (Exception e){
            CustomAlert.showToast(this, CustomAlert.WARNING, getString(R.string.error_notification));
        }
    }
}