package com.ucoin.ucoinnew.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.hbb20.CountryCodePicker;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.util.UiUtil;
import com.ucoin.ucoinnew.util.Util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class LoginActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;
    private CountryCodePicker mCcp;
    private AwesomeValidation mAwesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        initTitleBar();
        initView();
        initClick();
    }

    private void initClick() {
        final Button submitView = findViewById(R.id.activity_login_submit);
        TextInputLayout telephoneView = findViewById(R.id.activity_login_telephone);
        TextInputLayout passwordView = findViewById(R.id.activity_login_password);
        mAwesomeValidation.addValidation(LoginActivity.this, telephoneView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_login_validation_telephone);
        mAwesomeValidation.addValidation(LoginActivity.this, passwordView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_login_validation_password);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    TextInputEditText telephoneContentView = findViewById(R.id.activity_login_telephone_content);
                    TextInputEditText passwordContentView = findViewById(R.id.activity_login_password_content);
                    String telephone = telephoneContentView.getText().toString();
                    String password = passwordContentView.getText().toString();
                    int countryCode = Integer.valueOf(mCcp.getSelectedCountryCode());
                    try {
                        UiUtil.showLoading(LoginActivity.this);
                        JSONObject params = new JSONObject();
                        params.put("mobile", telephone);
                        params.put("country_code", countryCode);
                        params.put("password", password);
                        Api.request("login", "POST", params, false,LoginActivity.this, new Callback() {
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
                                    LoginActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(LoginActivity.this)
                                                .title(R.string.dialog_tip_title)
                                                .content(R.string.dialog_unknow_err_tip)
                                                .positiveText(R.string.dialog_positive)
                                                .show();
                                        }
                                    });
                                } else {
                                    try {
                                        JSONObject data = new JSONObject(jsonStr);
                                        final Object msg = data.opt("message");
                                        final Object token = data.opt("token");
                                        if (msg != null) {
                                            LoginActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MaterialDialog.Builder(LoginActivity.this)
                                                        .title(R.string.dialog_tip_title).content(String.valueOf(msg))
                                                        .content(String.valueOf(msg))
                                                        .positiveText(R.string.dialog_positive)
                                                        .show();
                                                }
                                            });
                                        } else if (!TextUtils.isEmpty(String.valueOf(token))) {
                                            Util.setSP("userToken", String.valueOf(token));
                                            finish();
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

        final Button registerView = findViewById(R.id.activity_login_go_to_register);
        registerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        mCcp = findViewById(R.id.activity_login_ccp);
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
    }

    private void initTitleBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mTitleBar = findViewById(R.id.title_bar);
        View leftCustomLayout = mTitleBar.getLeftCustomView();
        leftCustomLayout.findViewById(R.id.title_bar_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
