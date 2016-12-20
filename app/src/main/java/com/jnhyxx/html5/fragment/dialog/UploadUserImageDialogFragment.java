package com.jnhyxx.html5.fragment.dialog;

import android.app.Dialog;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jnhyxx.html5.R;
import com.jnhyxx.html5.utils.ToastUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2016/12/19.
 * 上传用户头像
 */

public class UploadUserImageDialogFragment extends DialogFragment {
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
    // TODO: 2016/12/19 测试图片
    @BindView(R.id.test)
    ImageView mTest;

    private Unbinder mBind;
    private File mFile;
    private Uri mUri;
    private FileInputStream mFileInputStream;

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
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_MinWidth);
//        setupDialog(this.getDialog(), STYLE_NO_TITLE);
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Dialog dialog = getDialog();
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        }

        String filePath = SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis()) + ".jpg";
        mFile = new File(Environment.getExternalStorageDirectory(), filePath);
    }


    @OnClick({R.id.takePhoneFromCamera, R.id.takePhoneFromPhone, R.id.takePhoneCancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.takePhoneFromCamera:
                Intent openCameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                mUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "image.jpg"));
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
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

                        String format = SimpleDateFormat.getInstance().format(System.currentTimeMillis());
                        String format1 = SimpleDateFormat.getDateTimeInstance().format(System.currentTimeMillis());
                        Log.d(TAG, "文件名  " + format + " " + format1);

                        Log.d(TAG, "uri " + mUri);
                        cropImage(mUri);
                    } else {
                        ToastUtil.curt("sd卡不可使用");
                    }
                    break;

                case REQ_CODE_CROP_IMAGE:
                    Uri uri = data.getData();
                    FileInputStream fileInputStream = null;
                    if (uri != null) {
                        try {
                            fileInputStream = new FileInputStream(uri.getPath());
                            Bitmap bitmap = BitmapFactory.decodeStream(fileInputStream);
                            mTest.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } finally {
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    break;
                case REQ_CODE_TAKE_PHONE_FROM_PHONES:
                    Bitmap bitmap = data.getParcelableExtra("data");
                    Uri data1 = data.getData();
                    if (data1 != null) {
                        Log.d(TAG, "相册的地址 " + data1.getPath());
                    }
                    if (bitmap != null) {
                        mTest.setImageBitmap(bitmap);
                    } else {
                        Uri phoneUri = data.getData();
                        FileInputStream phoneFileInputStream = null;
                        if (phoneUri != null) {
                            try {
                                phoneFileInputStream = new FileInputStream(phoneUri.getPath());
                                bitmap = BitmapFactory.decodeStream(phoneFileInputStream);
                                mTest.setImageBitmap(bitmap);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } finally {
                                if (phoneFileInputStream != null) {
                                    try {
                                        phoneFileInputStream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }

    }

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");// crop=true 有这句才能出来最后的裁剪页面.
        intent.putExtra("aspectX", 1);// 这两项为裁剪框的比例.
        intent.putExtra("aspectY", 1);// x:y=1:1
        intent.putExtra("outputX", 300);//图片输出大小
        intent.putExtra("outputY", 500);
        intent.putExtra("output", uri);
        intent.putExtra("outputFormat", "JPEG");// 返回格式
        startActivityForResult(intent, REQ_CODE_CROP_IMAGE);
    }
}
