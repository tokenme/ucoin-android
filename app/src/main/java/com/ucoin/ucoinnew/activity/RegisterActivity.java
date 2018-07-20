package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.hbb20.CountryCodePicker;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.util.UiUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class RegisterActivity extends BaseActivity {

    private CountryCodePicker mCcp;
    private boolean mIsSendingVerifyCode = false;
    private AwesomeValidation mAwesomeValidation;
    private int mCounter = 60;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    private void init() {
        initTitleBar();
        initView();
        initClick();
    }

    private void initClick() {
        final Button submitView = findViewById(R.id.activity_register_submit);
        TextInputLayout telephoneView = findViewById(R.id.activity_register_telephone);
        TextInputLayout verifyCodeView = findViewById(R.id.activity_register_verify_code);
        TextInputLayout passwordView = findViewById(R.id.activity_register_password);
        TextInputLayout rePasswordView = findViewById(R.id.activity_register_repassword);
        mAwesomeValidation.addValidation(RegisterActivity.this, telephoneView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_register_validation_telephone);
        mAwesomeValidation.addValidation(RegisterActivity.this, verifyCodeView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_register_validation_verify_code);
        mAwesomeValidation.addValidation(RegisterActivity.this, passwordView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_register_validation_password);
        mAwesomeValidation.addValidation(RegisterActivity.this, rePasswordView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_register_validation_repassword);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    TextInputEditText telephoneContentView = findViewById(R.id.activity_register_telephone_content);
                    TextInputEditText verifyCodeContentView = findViewById(R.id.activity_register_verify_code_content);
                    TextInputEditText passwordContentView = findViewById(R.id.activity_register_password_content);
                    TextInputEditText rePasswordContentView = findViewById(R.id.activity_register_repassword_content);
                    String telephone = telephoneContentView.getText().toString();
                    String verifyCode = verifyCodeContentView.getText().toString();
                    String password = passwordContentView.getText().toString();
                    String repassword = rePasswordContentView.getText().toString();
                    int countryCode = Integer.valueOf(mCcp.getSelectedCountryCode());

                    try {
                        UiUtil.showLoading(RegisterActivity.this);
                        JSONObject params = new JSONObject();
                        params.put("mobile", telephone);
                        params.put("country_code", countryCode);
                        params.put("passwd", password);
                        params.put("repasswd", repassword);
                        params.put("verify_code", verifyCode);
                        Api.request("register", "POST", params, false,RegisterActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                UiUtil.hideLoading();
                                Logger.e(String.valueOf(e));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                UiUtil.hideLoading();
                                String jsonStr = response.body().string();
                                if (jsonStr.isEmpty()) {
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(RegisterActivity.this)
                                                    .title(R.string.dialog_tip_title)
                                                    .content(R.string.dialog_unknow_err_tip)
                                                    .positiveText(R.string.dialog_positive)
                                                    .show();
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject data = new JSONObject(jsonStr);
                                        final String msg = data.optString("message");
                                        if (!TextUtils.isEmpty(msg)) {
                                            if (msg.equals("ok")) {
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                RegisterActivity.this.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new MaterialDialog.Builder(RegisterActivity.this)
                                                                .title(R.string.dialog_tip_title)
                                                                .content(String.valueOf(msg))
                                                                .positiveText(R.string.dialog_positive)
                                                                .show();
                                                    }
                                                });
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        final Button sendVerifyCodeView = findViewById(R.id.activity_register_send_verify_code);
        sendVerifyCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsSendingVerifyCode) {
                    return;
                }
                TextInputEditText telephoneView = findViewById(R.id.activity_register_telephone_content);
                String telephone = telephoneView.getText().toString();
                if ( ! TextUtils.isEmpty(telephone)) {
                    sendVerifyCodeView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorGeneralBg));
                    mIsSendingVerifyCode = true;
                    final Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void run() {
                                    if (mCounter <= 0) {
                                        mCounter = 60;
                                        sendVerifyCodeView.setText(R.string.activity_register_send_verify_code);
                                        sendVerifyCodeView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.icon));
                                        mIsSendingVerifyCode = false;
                                        timer.cancel();
                                        return;
                                    }
                                    sendVerifyCodeView.setText("重新发送(" + mCounter + "秒)");
                                    mCounter --;
                                }
                            });
                        }
                    }, 1000, 1000);

                    JSONObject params = new JSONObject();
                    try {
                        int countryCode = Integer.valueOf(mCcp.getSelectedCountryCode());
                        params.put("mobile", telephone);
                        params.put("country", countryCode);
                        Api.request("sendVerificationCode", "POST", params, false,RegisterActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String jsonStr = response.body().string();
                                Logger.i(jsonStr);
                                if (jsonStr.isEmpty()) {
                                    RegisterActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(RegisterActivity.this)
                                                .title(R.string.dialog_tip_title)
                                                .content(R.string.dialog_unknow_err_tip)
                                                .positiveText(R.string.dialog_positive)
                                                .show();
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject data = new JSONObject(jsonStr);
                                        final String msg = data.optString("message");
                                        if (!TextUtils.isEmpty(msg) && !msg.equals("ok")) {
                                            RegisterActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MaterialDialog.Builder(RegisterActivity.this)
                                                            .title(R.string.dialog_tip_title)
                                                            .content(msg)
                                                            .positiveText(R.string.dialog_positive)
                                                            .show();
                                                }
                                            });
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    new MaterialDialog.Builder(RegisterActivity.this)
                            .title(R.string.dialog_tip_title)
                            .content("请填写手机号")
                            .negativeText(R.string.dialog_positive)
                            .show();
                }
            }
        });
    }

    private void initView() {
        mCcp = findViewById(R.id.activity_login_ccp);
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
    }

    private void initTitleBar() {
        mToolbar = findViewById(R.id.view_toolbar);
        TextView textView = mToolbar.findViewById(R.id.view_toolbar_title);
        textView.setText("注册");
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }
}
