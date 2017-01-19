package com.jnhyxx.html5.activity.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.clipimage.ClipImageLayout;
import com.johnz.kutils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ClipHeadImageActivity extends BaseActivity {

    public static final String KEY_CLIP_USER_IMAGE = "CLIP_USER_IMAGE";

    public static final String mFilePath = Environment.getExternalStorageDirectory() + "clipImage.jpg";


    @BindView(R.id.clipImageLayout)
    ClipImageLayout mClipImageLayout;
    @BindView(R.id.cancel)
    TextView mCancel;
    @BindView(R.id.complete)
    TextView mComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_head_image);
        ButterKnife.bind(this);


        Intent intent = getIntent();
        String bitmapPath = intent.getStringExtra(KEY_CLIP_USER_IMAGE);
        Log.d("UploadUserImage", "传入的地址" + bitmapPath);
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
        mClipImageLayout.setZoomImageViewImage(bitmap);
    }

    private void uploadUserHeadImage(final String bitmapToBase64) {
        API.User.updateUserHeadImage(bitmapToBase64).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            if (!TextUtils.isEmpty(resp.getData().toString())) {
                                LocalUser.getUser().getUserInfo().setUserPortrait(resp.getData().toString());
                            }
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .fire();
    }

    @OnClick({R.id.cancel, R.id.complete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
            case R.id.complete:
                Bitmap clipBitmap = mClipImageLayout.clip();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d(TAG, "图片大小" + clipBitmap.getAllocationByteCount());
                    String fileSize = ImageUtil.getFileSize(clipBitmap.getAllocationByteCount());
                    Log.d(TAG, "图片大小" + fileSize);
                }
                Bitmap comp = ImageUtil.getUtil().comp(clipBitmap);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d(TAG, "裁剪后图片大小" + ImageUtil.getFileSize(clipBitmap.getAllocationByteCount()));
                }
                String bitmapToBase64 = ImageUtil.bitmapToBase64(comp);
                uploadUserHeadImage(bitmapToBase64);
                break;
        }
    }
}
