package com.jnhyxx.html5.fragment.dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.domain.local.LocalUser;
import com.jnhyxx.html5.net.API;
import com.jnhyxx.html5.net.Callback1;
import com.jnhyxx.html5.net.Resp;
import com.jnhyxx.html5.utils.ToastUtil;
import com.johnz.kutils.ImageUtil;
import com.johnz.kutils.net.ApiIndeterminate;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/12/19.
 * 上传用户头像
 */

public class UploadUserImageDialogFragment extends DialogFragment implements ApiIndeterminate {
    private static final String TAG = "UploadUserImageDialogFr";

    /**
     * 打开相机的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_CAMERA = 379;
    /**
     * 打开图册的请求码
     */
    private static final int REQ_CODE_TAKE_PHONE_FROM_PHONES = 600;
    /**
     * 打开裁剪界面的请求码
     */
    private static final int REQ_CODE_CROP_IMAGE = 204;


    @BindView(R.id.takePhoneFromCamera)
    TextView mTakePhoneFromCamera;
    @BindView(R.id.takePhoneFromPhone)
    TextView mTakePhoneFromPhone;
    @BindView(R.id.takePhoneCancel)
    TextView mTakePhoneCancel;
    private Unbinder mBind;
    private File mFile;

    private int widthPixels;

    public UploadUserImageDialogFragment() {

    }

    private OnUserImageListener mOnUserImageListener;


    public interface OnUserImageListener {
        /**
         * @param headImageUrl   头像地址
         * @param bitmapToBase64 所上传的头像转为base64字符串
         */
        void getUserImage(String headImageUrl, String bitmapToBase64);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserImageListener) {
            mOnUserImageListener = (OnUserImageListener) context;
        } else {
            throw new RuntimeException(context.toString() +
                    " must implement UploadUserImageDialogFragment.OnUserImageListener");
        }
    }

    public static UploadUserImageDialogFragment newInstance() {
        Bundle args = new Bundle();
        UploadUserImageDialogFragment fragment = new UploadUserImageDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AlertDialogStyle);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        widthPixels = getDisplayWith();
        Log.d(TAG, "宽度 " + widthPixels);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_upload_user_image, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.takePhoneFromCamera, R.id.takePhoneFromPhone, R.id.takePhoneCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoneFromCamera:
                Intent openCameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                mFile = new File(Environment
                        .getExternalStorageDirectory(), "image.jpg");
                Uri mMBitmapUri = Uri.fromFile(mFile);
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMBitmapUri);
                startActivityForResult(openCameraIntent, REQ_CODE_TAKE_PHONE_FROM_CAMERA);
                break;
            case R.id.takePhoneFromPhone:
                Intent openAlbumIntent = new Intent(
                        Intent.ACTION_GET_CONTENT);
                openAlbumIntent.setType("image/*");
                startActivityForResult(openAlbumIntent, REQ_CODE_TAKE_PHONE_FROM_PHONES);
                break;
            case R.id.takePhoneCancel:
                this.dismiss();
                break;
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, UploadUserImageDialogFragment.class.getSimpleName());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_TAKE_PHONE_FROM_CAMERA:
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        Uri mMBitmapUri = Uri.fromFile(mFile);
                        String s = ImageUtil.FormetFileSize(mFile);
                        Log.d(TAG, "文件的大小 " + s);
                        if (mMBitmapUri != null) {
                            cropImage(mMBitmapUri);
                            Bitmap bitmap = BitmapFactory.decodeFile(mMBitmapUri.getPath());
                            Log.d(TAG, "拍照的原图大小" + bitmap.getAllocationByteCount());
                        }
                    } else {
                        ToastUtil.curt("sd卡不可使用");
                    }
                    break;

                case REQ_CODE_CROP_IMAGE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        if (!TextUtils.isEmpty(uri.getPath())) {
                            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                            Log.d(TAG, "裁剪的图片大小 " + bitmap.getAllocationByteCount());
                            String bitmapToBase64 = ImageUtil.bitmapToBase64(bitmap);
                            uploadUserHeadImage(bitmapToBase64);

                            Bitmap comp = ImageUtil.getUtil().comp(bitmap);
                            Log.d(TAG, "裁剪后的图片大小 " + comp.getAllocationByteCount());
                        }
                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_PHONES:
                    Uri photosUri = data.getData();
                    if (photosUri != null) {
                        Log.d(TAG, "相册的地址 " + photosUri.getPath());
                        Bitmap bitmap = BitmapFactory.decodeFile(photosUri.getPath());
                        Log.d(TAG, "相片中获取的原图大小" + bitmap.getAllocationByteCount());
                        cropImage(photosUri);
                    }
                    break;
            }
        }

    }

    private void uploadUserHeadImage(final String bitmapToBase64) {
        API.User.updateUserHeadImage(bitmapToBase64).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback1<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (!TextUtils.isEmpty(resp.getData().toString())) {
                            LocalUser.getUser().getUserInfo().setUserPortrait(resp.getData().toString());
                        }
                        mOnUserImageListener.getUserImage(resp.getData().toString(), bitmapToBase64);
                        dismissAllowingStateLoss();
                    }
                })
                .fireSync();
    }

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", widthPixels);//图片输出大小
        intent.putExtra("outputY", widthPixels);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
    }

    public int getDisplayWith() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (displayMetrics.widthPixels > 0) {
            return (int) (0.85 * (displayMetrics.widthPixels));
        }
        return 400;
    }

    @Override
    public void onShow(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onShow(tag);
        }
    }

    @Override
    public void onDismiss(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onDismiss(tag);
        }
    }
}
