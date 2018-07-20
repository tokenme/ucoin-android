package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.app.TakePhotoImpl;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.InvokeParam;
import com.jph.takephoto.model.TContextWrap;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.jph.takephoto.permission.InvokeListener;
import com.jph.takephoto.permission.PermissionManager;
import com.jph.takephoto.permission.TakePhotoInvocationHandler;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class CreateCoinActivity extends BaseActivity implements TakePhoto.TakeResultListener,InvokeListener {

    private ArrayList<TImage> mUploadImages = new ArrayList<>();
    private TakePhoto mTakePhoto;
    private InvokeParam mInvokeParam;
    private String mUploadImageUrl = "";
    private AwesomeValidation mAwesomeValidation;
    private int mUploadedImagesNum = 0;
    final private int mImageLimit = 1;

    private Toolbar mToolbar;

    @Override
    public void takeCancel() {

    }

    @Override
    public void takeFail(TResult result, String msg) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        getTakePhoto().onSaveInstanceState(outState);
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getTakePhoto().onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.TPermissionType type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionsResult(CreateCoinActivity.this, type, mInvokeParam,CreateCoinActivity.this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(CreateCoinActivity.this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            CreateCoinActivity.this.mInvokeParam = invokeParam;
        }
        return type;
    }

    @SuppressLint("ResourceType")
    @Override
    public void takeSuccess(TResult result) {
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
                imgDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new MaterialDialog.Builder(CreateCoinActivity.this)
                                .autoDismiss(false)
                                .canceledOnTouchOutside(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        ll.removeView(v);
                                        mUploadImageUrl = "";
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .title(R.string.activity_make_task_remove_upload_pic_dialog_title)
                                .positiveText(R.string.dialog_positive)
                                .negativeText(R.string.dialog_negative)
                                .show();
                    }
                });
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

    public TakePhoto getTakePhoto() {
        if (mTakePhoto == null){
            mTakePhoto = (TakePhoto) TakePhotoInvocationHandler.of(this).bind(new TakePhotoImpl(this,this));
        }
        return mTakePhoto;
    }

    private void uploadLogo(final File file) {
        JSONObject params = new JSONObject();
        mUploadedImagesNum = 1;
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
                                mUploadedImagesNum = 0;
                            }
                        });
                    } else {
                        try {
                            JSONObject data = new JSONObject(jsonStr);
                            final String qiniuKey = data.optString("key");
                            final String qiniuUpToken = data.optString("uptoken");
                            final String qiniuLink = data.optString("link");
                            if ( ! TextUtils.isEmpty(qiniuKey) && ! TextUtils.isEmpty(qiniuUpToken) && ! TextUtils.isEmpty(qiniuLink)) {
                                Configuration qiniuConfig = new Configuration.Builder().build();
                                UploadManager uploadManager = new UploadManager(qiniuConfig);
                                uploadManager.put(file, qiniuKey, qiniuUpToken, new UpCompletionHandler() {
                                    @Override
                                    public void complete(String key, ResponseInfo info, JSONObject res) {
                                        if(info.isOK()) {
                                            Logger.i("qiniu Upload Success");
                                            mUploadImageUrl = qiniuLink;
                                        } else {
                                            Logger.i("qiniu Upload Fail");
                                        }
                                        Logger.i("qiniu" + key + ",\r\n " + info + ",\r\n " + res);
                                        mUploadedImagesNum = 0;
                                    }
                                }, null);
                            }
                        } catch (JSONException e) {
                            mUploadedImagesNum = 0;
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
        TextInputLayout nameView = findViewById(R.id.activity_create_coin_name);
        TextInputLayout symbolView = findViewById(R.id.activity_create_coin_symbol);
        TextInputLayout totalSupplyView = findViewById(R.id.activity_create_coin_total_supply);
        TextInputLayout decimalsView = findViewById(R.id.activity_create_coin_decimals);
        mAwesomeValidation.addValidation(CreateCoinActivity.this, nameView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_name);
        mAwesomeValidation.addValidation(CreateCoinActivity.this, symbolView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_symbol);
        mAwesomeValidation.addValidation(CreateCoinActivity.this, totalSupplyView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_total_supply);
        mAwesomeValidation.addValidation(CreateCoinActivity.this, decimalsView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_decimals);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    if (mUploadedImagesNum > 0) {
                        CreateCoinActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MaterialDialog.Builder(CreateCoinActivity.this)
                                        .title(R.string.dialog_tip_title)
                                        .content(R.string.dialog_tip_uploading_image)
                                        .positiveText(R.string.dialog_positive)
                                        .show();
                            }
                        });
                        return;
                    }
                    TextInputEditText nameContentView = findViewById(R.id.activity_create_coin_name_content);
                    TextInputEditText symbolContentView = findViewById(R.id.activity_create_coin_symbol_content);
                    TextInputEditText totalSupplyContentView = findViewById(R.id.activity_create_coin_total_supply_content);
                    TextInputEditText decimalsContentView = findViewById(R.id.activity_create_coin_decimals_content);
                    String name = nameContentView.getText().toString();
                    String symbol = symbolContentView.getText().toString();
                    String totalSupply = totalSupplyContentView.getText().toString();
                    String decimals = decimalsContentView.getText().toString();

                    try {
                        UiUtil.showLoading(CreateCoinActivity.this);
                        JSONObject params = new JSONObject();
                        params.put("name", name);
                        params.put("symbol", symbol);
                        params.put("total_supply", Integer.valueOf(totalSupply));
                        params.put("decimals", Integer.valueOf(decimals));
                        params.put("logo", mUploadImageUrl);
                        Api.request("createCoin", "POST", params, false,CreateCoinActivity.this, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                UiUtil.hideLoading();
                                Logger.e(String.valueOf(e));
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                UiUtil.hideLoading();
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
                                        final String msg = data.optString("message");
                                        if (!TextUtils.isEmpty(msg)) {
                                            CreateCoinActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MaterialDialog.Builder(CreateCoinActivity.this)
                                                        .title(R.string.dialog_tip_title)
                                                        .content(msg)
                                                        .positiveText(R.string.dialog_positive)
                                                        .show();
                                                }
                                            });
                                        } else {
                                            setResult(Activity.RESULT_OK);
                                            finish();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } catch (IOException e) {
                        UiUtil.hideLoading();
                        e.printStackTrace();
                    } catch (JSONException e) {
                        UiUtil.hideLoading();
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initView() {
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
    }

    private void initTitleBar() {
        mToolbar = findViewById(R.id.view_toolbar);
        TextView textView = mToolbar.findViewById(R.id.view_toolbar_title);
        textView.setText("造币");
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
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
                                        String uploadImagePath = "/ucoin/temp/" + System.currentTimeMillis() + ".jpg";
                                        File file = new File(Environment.getExternalStorageDirectory(), uploadImagePath);
                                        if ( ! file.getParentFile().exists()) {
                                            file.getParentFile().mkdirs();
                                        }
                                        Uri imageUri = Uri.fromFile(file);
                                        takePhoto.onPickFromCapture(imageUri);
                                        break;
                                    case 1:
                                        takePhoto.onPickMultiple(mImageLimit - mUploadImages.size());
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
