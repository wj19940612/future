package com.jnhyxx.html5.activity.userinfo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.jnhyxx.html5.utils.ToastUtil;
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
                String bitmapToBase64 = ImageUtil.bitmapToBase64(clipBitmap);
                uploadUserHeadImage(bitmapToBase64);


//                Bitmap bitmap = ImageUtil.getUtil().compressImage(clipBitmap);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] datas = baos.toByteArray();
//                Intent intent = new Intent(getActivity(), UserInfoActivity.class);
//                intent.putExtra(KEY_CLIP_USER_IMAGE, datas);
//                setResult(RESULT_OK);
//                finish();
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
                        } else {
                            ToastUtil.curt("头像上传失败");
                        }
                    }
                })
                .fire();
    }

}
