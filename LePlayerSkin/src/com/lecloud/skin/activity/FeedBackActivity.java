package com.lecloud.skin.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.lecloud.sdk.api.feedback.IFeedBackListener;
import com.lecloud.sdk.api.feedback.LeFeedBack;
import com.lecloud.sdk.api.feedback.LeLogSubmit;
import com.lecloud.sdk.api.feedback.OnLogSubmitResultListener;
import com.lecloud.sdk.http.logutils.LeLog;
import com.lecloud.sdk.utils.NetworkUtils;
import com.lecloud.skin.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by heyuekuai on 16/1/7.
 */
public class FeedBackActivity extends Activity implements IFeedBackListener {
    String feedContent;
    String phone;
    private ViewGroup feedCheckGroup;
    private EditText editContent;
    private EditText editPhone;
    private View feedCancel;
    private View feedBtnOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.letv_feed_back_activity_layout);
        initReportView();
    }

    private void initReportView() {
        feedCheckGroup = (ViewGroup) findViewById(R.id.check_group);
        editContent = (EditText) findViewById(R.id.edit_feed_content);
        editPhone = (EditText) findViewById(R.id.edit_phone);
        feedCancel = findViewById(R.id.btn_back);
        feedCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        feedBtnOk = findViewById(R.id.btn_submit);


        feedBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    editPhone.setHint(FeedBackActivity.this.getResources().getString(R.string.leave_contact_hint));
                    return;
                }
                if (!NetworkUtils.hasConnect(FeedBackActivity.this)) {
                    Toast.makeText(FeedBackActivity.this, FeedBackActivity.this.getResources().getString(R.string.no_network_hint), Toast.LENGTH_LONG).show();
                    return;
                }
                LeLogSubmit.submitLog(phone, feedContent, new OnLogSubmitResultListener() {
                    @Override
                    public void onError(String s, int i, String s1) {
                        LeLog.ePrint("FeedBackActivity", "onError上传日志失败s=" + s + "i=" + i + "s1==" + s1);
                    }

                    @Override
                    public void onCompleted(String s) {
                        LeLog.ePrint("FeedBackActivity", "onCompleted,,,s=" + s);
                    }

                    @Override
                    public void onCancelled(String s) {
                        LeLog.ePrint("FeedBackActivity", "onCancelled,,,s=" + s);
                    }

                    @Override
                    public void onGetHelpNumber(String s, String s1) {
                        LeLog.ePrint("FeedBackActivity", "任务号s1==" + s1);
                    }
                });
                LeFeedBack.postFeedBack(FeedBackActivity.this, FeedBackActivity.this.getResources().getString(R.string.submit_error_info),
                        FeedBackActivity.this.getResources().getString(R.string.submitting), getSubmitParameter(), FeedBackActivity.this);
            }
        });
    }

    private JSONObject getSubmitParameter() {
        feedContent = getFeedCheckText().trim() + "\n" + editContent.getText().toString().trim();
        phone = editPhone.getText().toString().trim();
        JSONObject param = new JSONObject();
        Bundle bundle = getIntent().getExtras().getBundle("params");
        try {
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    param.put(key, bundle.get(key));
                }
            }
            param.put("ec", 1);
            param.put("usercontact", phone);
            param.put("userfeedback", feedContent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return param;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private String getFeedCheckText() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < feedCheckGroup.getChildCount(); i++) {
            if (feedCheckGroup.getChildAt(i) instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) feedCheckGroup.getChildAt(i);
                for (int j = 0; j < group.getChildCount(); j++) {
                    if (group.getChildAt(j) instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) group.getChildAt(j);
                        if (checkBox.isChecked()) {
                            sb.append("[");
                            sb.append(checkBox.getText().toString().trim());
                            sb.append("]");
                        }
                    }
                }
            }
            if (feedCheckGroup.getChildAt(i) instanceof CheckBox) {
                CheckBox checkBox = (CheckBox) feedCheckGroup.getChildAt(i);
                if (checkBox.isChecked()) {
                    sb.append(checkBox.getText().toString().trim());
                }
            }
        }

        return sb.toString();
    }

    @Override
    public void feedBackSuccess() {
        Toast.makeText(FeedBackActivity.this, getResources().getString(R.string.feedback_submit_success), Toast.LENGTH_SHORT).show();
        FeedBackActivity.this.finish();
    }

    @Override
    public void feedBackFailure() {
        Toast.makeText(FeedBackActivity.this, getResources().getString(R.string.feedback_submit_failed), Toast.LENGTH_SHORT).show();
    }
}
