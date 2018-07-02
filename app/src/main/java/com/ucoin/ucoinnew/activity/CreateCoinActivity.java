package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.orhanobut.logger.Logger;
import com.qiniu.android.common.AutoZone;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.util.UiUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateCoinActivity extends TakePhotoActivity {

    private CommonTitleBar mTitleBar;
    private ArrayList<TImage> mUploadImages = new ArrayList<>();
    private String mUploadImagePath = "";
    private String mUploadImageUrl = "";
    final private int sImageLimit = 1;

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @SuppressLint("ResourceType")
    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        ArrayList<TImage> tmpImages = new ArrayList<>();
        tmpImages = result.getImages();
        if (tmpImages.size() > 0) {
            final android.support.v7.widget.GridLayout ll = findViewById(R.id.activity_create_coin_upload_logo);
            int uploadImageWidth = findViewById(R.id.activity_create_coin_take_photo).getWidth();
            for (final TImage image : tmpImages) {
                mUploadImages.add(image);
                SimpleDraweeView imgDraweeView = new SimpleDraweeView(CreateCoinActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                layoutParams.width = uploadImageWidth;
                layoutParams.height = layoutParams.width;
                imgDraweeView.setLayoutParams(layoutParams);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                imgDraweeView.getHierarchy().setRoundingParams(roundingParams);
                File file = new File(image.getOriginalPath());
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(file))
                        .setResizeOptions(new ResizeOptions(uploadImageWidth, uploadImageWidth))
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        .setOldController(imgDraweeView.getController())
                        .setImageRequest(request)
                        .build();
                imgDraweeView.setController(controller);
                UiUtil.setMargins(imgDraweeView, 5, 0, 5, 10);
                ll.removeAllViews();
                ll.addView(imgDraweeView);
                uploadLogo(file);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coin);
        init();
    }

    private void init() {
        initTitleBar();
        initTakePhoto();
        initView();
        initClick();
    }

    private void uploadLogo(final File file) {
        JSONObject params = new JSONObject();
        try {
            Api.request("uploadCoinLogo", "POST", params, false,CreateCoinActivity.this, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.e(String.valueOf(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Logger.i(jsonStr);
                    if (TextUtils.isEmpty(jsonStr)) {
                        CreateCoinActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MaterialDialog.Builder(CreateCoinActivity.this)
                                    .title(R.string.dialog_tip_title)
                                    .content(R.string.dialog_unknow_err_tip)
                                    .positiveText(R.string.dialog_positive)
                                    .show();
                            }
                        });
                    } else {
                        try {
                            JSONObject data = new JSONObject(jsonStr);
                            final Object qiniuKey = data.opt("key");
                            final Object qiniuUpToken = data.opt("uptoken");
                            final Object qiniuLink = data.opt("link");
                            if ( ! TextUtils.isEmpty(String.valueOf(qiniuKey)) && ! TextUtils.isEmpty(String.valueOf(qiniuUpToken)) && ! TextUtils.isEmpty(String.valueOf(qiniuLink))) {
                                Configuration qiniuConfig = new Configuration.Builder().build();
                                UploadManager uploadManager = new UploadManager(qiniuConfig);
                                uploadManager.put(file, String.valueOf(qiniuKey), String.valueOf(qiniuUpToken), new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject res) {
                                        if(info.isOK()) {
                                            Logger.i("qiniu Upload Success");
                                            mUploadImageUrl = String.valueOf(qiniuLink);
                                        } else {
                                            Logger.i("qiniu Upload Fail");
                                        }
                                        Logger.i("qiniu" + key + ",\r\n " + info + ",\r\n " + res);
                                    }
                                }, null);
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

    private void initClick() {
        final Button submitView = findViewById(R.id.activity_create_coin_submit);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameView = findViewById(R.id.activity_create_coin_name);
                String name = nameView.getText().toString();
                EditText symbolView = findViewById(R.id.activity_create_coin_symbol);
                String symbol = symbolView.getText().toString();
                EditText totalSupplyView = findViewById(R.id.activity_create_coin_total_supply);
                String totalSupply = totalSupplyView.getText().toString();
                EditText decimalsView = findViewById(R.id.activity_create_coin_decimals);
                String decimals = decimalsView.getText().toString();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(symbol) || TextUtils.isEmpty(totalSupply) || TextUtils.isEmpty(decimals)) {
                    new MaterialDialog.Builder(CreateCoinActivity.this)
                        .title(R.string.dialog_tip_title)
                        .content("请填写相关信息")
                        .negativeText(R.string.dialog_positive)
                        .show();
                } else {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("name", name);
                        params.put("symbol", symbol);
                        params.put("total_supply", Integer.valueOf(totalSupply));
                        params.put("decimals", Integer.valueOf(decimals));
                        params.put("logo", mUploadImageUrl);
                        Api.request("createCoin", "POST", params, false,CreateCoinActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                Logger.e(String.valueOf(e));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String jsonStr = response.body().string();
                                Logger.i(jsonStr);
                                if (jsonStr.isEmpty()) {
                                    CreateCoinActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(CreateCoinActivity.this)
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
                                        if (msg != null) {
                                            CreateCoinActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MaterialDialog.Builder(CreateCoinActivity.this)
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

    private void initView() {
    }

    private void initTitleBar() {
        mTitleBar = findViewById(R.id.title_bar);
        View leftCustomLayout = mTitleBar.getLeftCustomView();
        leftCustomLayout.findViewById(R.id.title_bar_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initTakePhoto() {
        View view = findViewById(R.id.activity_create_coin_take_photo);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] stringItems = {"拍照", "相册"};
                new MaterialDialog.Builder(CreateCoinActivity.this)
                        .autoDismiss(false)
                        .canceledOnTouchOutside(false)
                        .title(R.string.activity_make_task_choose_pic_dialog_title)
                        .items(stringItems)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                TakePhoto takePhoto = getTakePhoto();
                                CompressConfig compressConfig = new CompressConfig.Builder()
                                        .setMaxSize(800 * 800)
                                        .setMaxPixel(800)
                                        .create();
                                takePhoto.onEnableCompress(compressConfig,true);
                                switch (which) {
                                    case 0:
                                        mUploadImagePath = "/ucoin/temp/" + System.currentTimeMillis() + ".jpg";
                                        File file = new File(Environment.getExternalStorageDirectory(), mUploadImagePath);
                                        if ( ! file.getParentFile().exists()) {
                                            file.getParentFile().mkdirs();
                                        }
                                        Uri imageUri = Uri.fromFile(file);
                                        takePhoto.onPickFromCapture(imageUri);
                                        break;
                                    case 1:
                                        takePhoto.onPickMultiple(sImageLimit - mUploadImages.size());
                                        break;
                                }
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });
    }
}
