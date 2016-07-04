package com.jnhyxx.html5.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.FileSystem;
import com.johnz.kutils.ImageUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SaveImageActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_IMAGE_URL = "imageUrl";

    private Button mSaveImage;
    private Bitmap mBitmap;

    private String mFileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_image);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        initView();

        processIntent(getIntent());
    }

    private void processIntent(Intent intent) {
        String imageUrl = intent.getStringExtra(EXTRA_IMAGE_URL);
        if (!TextUtils.isEmpty(imageUrl)) {
            new DownloadImageTask(this).execute(imageUrl);
        }
        mFileName = getFileName(imageUrl);
    }

    private String getFileName(String imageUrl) {
        int lastEqualSignIndex = imageUrl.lastIndexOf("=");
        if (lastEqualSignIndex != -1) {
            return imageUrl.substring(lastEqualSignIndex + 1, imageUrl.length()) + ".jpg";
        }

        // For some urls which have the extensions like '.jpg', '.jpeg', '.png'
        if (imageUrl.indexOf(".jpg") > -1
                || imageUrl.indexOf(".jpeg") > -1
                || imageUrl.indexOf(".png") > -1) {
            int lastLeftSlashIndex = imageUrl.lastIndexOf("/");
            return imageUrl.substring(lastLeftSlashIndex + 1, imageUrl.length());
        }

        return "QR" + System.currentTimeMillis() + ".jpg";
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<Context> mWeakReference;

        public DownloadImageTask(Context context) {
            mWeakReference = new WeakReference<>(context);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = null;
            try {
                bitmap = Picasso.with(mWeakReference.get()).load(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;

        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            mBitmap = bitmap;
            if (mBitmap != null) {
                mSaveImage.setEnabled(true);
            }
        }
    }

    private void initView() {
        mSaveImage = (Button) findViewById(R.id.saveImage);
        mSaveImage.setEnabled(false);

        mSaveImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.saveImage:
                saveImage();
                break;
        }
    }

    private void saveImage() {
        if (FileSystem.isStoragePermissionGranted(this, FileSystem.REQ_CODE_ASK_PERMISSION)) {
            File file = ImageUtil.getUtil().saveGalleryBitmap(this, mBitmap, mFileName);
            if (file != null && file.exists()) {
                ToastUtil.show(getString(R.string.save_qrcode_to, file.getAbsolutePath()));
                finish();
            } else {
                ToastUtil.show(R.string.save_qrcode_failure);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FileSystem.REQ_CODE_ASK_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.show(R.string.permission_granted_success);
                } else {
                    ToastUtil.show(R.string.save_qrcode_failure);
                }
            }
        }
    }
}
