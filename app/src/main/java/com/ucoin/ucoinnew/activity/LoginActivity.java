package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hbb20.CountryCodePicker;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.util.Util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;
    private CountryCodePicker ccp;

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
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText telephoneView = findViewById(R.id.activity_login_telephone);
                String telephone = telephoneView.getText().toString();
                EditText passwordView = findViewById(R.id.activity_login_password);
                String password = passwordView.getText().toString();
                int countryCode = Integer.valueOf(ccp.getSelectedCountryCode());

                if (telephone.equals("") || password.equals("")) {
                    new MaterialDialog.Builder(LoginActivity.this)
                            .title(R.string.dialog_tip_title)
                            .content("请填写相关信息")
                            .negativeText(R.string.dialog_positive)
                            .show();
                } else {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("mobile", telephone);
                        params.put("country_code", countryCode);
                        params.put("password", password);
                        Api.request("login", "POST", params, LoginActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Logger.e(String.valueOf(e));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String jsonStr = response.body().string();
                                Logger.i(jsonStr);
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
                                    } else if (token != null && ! String.valueOf(token).equals("")) {
                                        Util.setSP("userToken", String.valueOf(token));
                                        finish();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
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
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        ccp = findViewById(R.id.activity_login_ccp);
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
