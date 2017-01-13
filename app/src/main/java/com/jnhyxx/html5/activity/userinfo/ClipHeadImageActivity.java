package com.jnhyxx.html5.activity.userinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.clipimage.ClipImageLayout;
import com.johnz.kutils.ImageUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ClipHeadImageActivity extends BaseActivity {

    public static final String KEY_CLIP_USER_IMAGE = "CLIP_USER_IMAGE";

    public static final String mFilePath = Environment.getExternalStorageDirectory() + "clipImage.jpg";

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.clipImageLayout)
    ClipImageLayout mClipImageLayout;

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


        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap clipBitmap = mClipImageLayout.clip();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Log.d(TAG, "图片大小" + clipBitmap.getAllocationByteCount());
                    String fileSize = ImageUtil.getFileSize(clipBitmap.getAllocationByteCount());
                    Log.d(TAG, "图片大小" + fileSize);
                }
                Bitmap comp = ImageUtil.getUtil().comp(clipBitmap);
                Log.d(TAG, "裁剪后图片大小" + ImageUtil.getFileSize(clipBitmap.getAllocationByteCount()));
                String bitmapToBase64 = ImageUtil.bitmapToBase64(comp);
                uploadUserHeadImage(bitmapToBase64);
            }
        });
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

}
