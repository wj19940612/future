package com.jnhyxx.html5.activity.userinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.view.TitleBar;
import com.jnhyxx.html5.view.clipimage.ClipImageLayout;
import com.johnz.kutils.ImageUtil;

import java.io.ByteArrayOutputStream;

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
//        Bitmap bitmap = intent.getParcelableExtra(KEY_CLIP_USER_IMAGE);
        String bitmapPath = intent.getStringExtra(KEY_CLIP_USER_IMAGE);
        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath);
        mClipImageLayout.setZoomImageViewImage(bitmap);


        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap clipBitmap = mClipImageLayout.clip();
                Bitmap bitmap = ImageUtil.getUtil().compressImage(clipBitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] datas = baos.toByteArray();
//
//                Intent intent = new Intent(this, UserInfoActivity.class);
//                intent.putExtra("bitmap", datas);
//                startActivity(intent);


                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
                intent.putExtra(KEY_CLIP_USER_IMAGE, datas);
                setResult(RESULT_OK, intent);
                finish();

//                File file = ImageUtil.getUtil().saveBitmap(bitmap, mFilePath);
//                if (file.exists()) {
//
//                    Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//                    intent.putExtra(KEY_CLIP_USER_IMAGE, mFilePath);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                } else {
//                    ToastUtil.curt("文件创建失败");
//                }
            }
        });
    }
}
