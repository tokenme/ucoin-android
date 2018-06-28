package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;
    private boolean isSendingVerifyCode = false;
    private int counter = 60;

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
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText telephoneView = findViewById(R.id.activity_register_telephone);
                String telephone = telephoneView.getText().toString();
                EditText verifyCodeView = findViewById(R.id.activity_register_verify_code);
                String verifyCode = verifyCodeView.getText().toString();
                EditText passwordView = findViewById(R.id.activity_register_password);
                String password = passwordView.getText().toString();
                EditText repasswordView = findViewById(R.id.activity_register_repassword);
                String repassword = repasswordView.getText().toString();

                if (telephone.equals("") || verifyCode.equals("") || password.equals("") || repassword.equals("")) {
                    new MaterialDialog.Builder(RegisterActivity.this)
                        .title(R.string.dialog_tip_title)
                        .content("请填写相关信息")
                        .negativeText(R.string.dialog_positive)
                        .show();
                } else {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("mobile", telephone);
                        params.put("country_code", 86);
                        params.put("passwd", password);
                        params.put("repasswd", repassword);
                        params.put("verify_code", verifyCode);
                        Api.request("register", "POST", params, RegisterActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Logger.e(String.valueOf(e));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String jsonStr = response.body().string();
                                try {
                                    JSONObject data = new JSONObject(jsonStr);
                                    final Object msg = data.opt("message");
                                    if (msg != null && String.valueOf(msg).equals("ok")) {
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

        final Button sendVerifyCodeView = findViewById(R.id.activity_register_send_verify_code);
        sendVerifyCodeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSendingVerifyCode) {
                    return;
                }
                EditText telephoneView = findViewById(R.id.activity_register_telephone);
                String telephone = telephoneView.getText().toString();
                if (telephone != null && ! telephone.equals("")) {
                    sendVerifyCodeView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.colorGeneralBg));
                    isSendingVerifyCode = true;
                    final Timer timer = new Timer();
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @RequiresApi(api = Build.VERSION_CODES.M)
                                @Override
                                public void run() {
                                    if (counter <= 0) {
                                        counter = 60;
                                        sendVerifyCodeView.setText(R.string.activity_register_send_verify_code);
                                        sendVerifyCodeView.setBackgroundColor(ContextCompat.getColor(RegisterActivity.this, R.color.icon));
                                        isSendingVerifyCode = false;
                                        timer.cancel();
                                        return;
                                    }
                                    sendVerifyCodeView.setText("重新发送(" + counter + "秒)");
                                    counter --;
                                }
                            });
                        }
                    }, 1000, 1000);

                    JSONObject params = new JSONObject();
                    try {
                        params.put("mobile", telephone);
                        params.put("country", 86);
                        Api.request("sendVerificationCode", "POST", params, RegisterActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {

                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                Logger.i(String.valueOf(response));
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
