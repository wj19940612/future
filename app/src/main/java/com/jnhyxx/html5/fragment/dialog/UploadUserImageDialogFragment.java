package com.jnhyxx.html5.fragment.dialog;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.activity.BaseActivity;
import com.jnhyxx.html5.activity.userinfo.ClipHeadImageActivity;
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
    /**
     * 打开自定义裁剪页面的请求码
     */
    public static final int REQ_CLIP_HEAD_IMAGE_PAGE = 144;


    @BindView(R.id.takePhoneFromCamera)
    TextView mTakePhoneFromCamera;
    @BindView(R.id.takePhoneFromGallery)
    TextView mTakePhoneFromPhone;
    @BindView(R.id.takePhoneCancel)
    TextView mTakePhoneCancel;
    private Unbinder mBind;
    private File mFile;


    public UploadUserImageDialogFragment() {

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

    @OnClick({R.id.takePhoneFromCamera, R.id.takePhoneFromGallery, R.id.takePhoneCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoneFromCamera:
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    Intent openCameraIntent = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    mFile = new File(Environment
                            .getExternalStorageDirectory(), "image.jpg");
                    // 指定照片保存路径（SD卡），image.jpg为一个临时文件，防止拿到
                    Uri mMBitmapUri = Uri.fromFile(mFile);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMBitmapUri);
                    startActivityForResult(openCameraIntent, REQ_CODE_TAKE_PHONE_FROM_CAMERA);
                }
                break;
            case R.id.takePhoneFromGallery:
                if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
                    Intent openAlbumIntent = new Intent(
                            Intent.ACTION_PICK);
                    openAlbumIntent.setType("image/*");
                    startActivityForResult(openAlbumIntent, REQ_CODE_TAKE_PHONE_FROM_PHONES);
                } else {
                    ToastUtil.curt(R.string.sd_is_not_useful);
                }
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
                    if (mFile != null) {
                        Uri mMBitmapUri = Uri.fromFile(mFile);
                        if (mMBitmapUri != null) {
                            if (!TextUtils.isEmpty(mMBitmapUri.getPath())) {
                                openClipImagePage(mMBitmapUri.getPath());
                            }
                        }
                    }
                    break;

                case REQ_CODE_CROP_IMAGE:
                    Uri uri = data.getData();
                    if (uri != null) {
                        if (!TextUtils.isEmpty(uri.getPath())) {
                            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
                            String bitmapToBase64 = ImageUtil.bitmapToBase64(bitmap);
                            Bitmap comp = ImageUtil.getUtil().comp(bitmap);
//                            Log.d(TAG, "裁剪后的图片大小 " + comp.getAllocationByteCount());
                        }
                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_PHONES:
                    String galleryBitmapPath = getGalleryBitmapPath(data);
                    if (!TextUtils.isEmpty(galleryBitmapPath)) {
                        openClipImagePage(galleryBitmapPath);
                    }
                    break;
            }
        }

    }

    private String getGalleryBitmapPath(Intent data) {
        Uri photosUri = data.getData();
        if (photosUri != null) {
            if (!TextUtils.isEmpty(photosUri.getPath()) && photosUri.getPath().endsWith("jpg")) {
                return photosUri.getPath();
            } else {
                ContentResolver contentResolver = getActivity().getContentResolver();
                Cursor cursor = contentResolver.query(photosUri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    //最后根据索引值获取图片路径
                    String path = cursor.getString(column_index);
                    if (!TextUtils.isEmpty(path)) {
                        return path;
                    }
                    cursor.close();
                }
            }
        }
        return null;
    }

    private void openClipImagePage(String imaUri) {
        Intent intent = new Intent(getActivity(), ClipHeadImageActivity.class);
        intent.putExtra(ClipHeadImageActivity.KEY_CLIP_USER_IMAGE, imaUri);
        getActivity().startActivityForResult(intent, REQ_CLIP_HEAD_IMAGE_PAGE);
        dismiss();
    }

    //调用系统裁剪，有问题，有些手机不支持裁剪后获取图片
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 600);//图片输出大小
        intent.putExtra("outputY", 600);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 返回格式
        startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
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
