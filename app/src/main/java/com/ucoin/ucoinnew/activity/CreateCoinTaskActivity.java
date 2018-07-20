package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
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
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.StringUtils;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.util.DTUtil;
import com.ucoin.ucoinnew.util.UiUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class CreateCoinTaskActivity extends BaseActivity implements TakePhoto.TakeResultListener,InvokeListener {

    private ArrayList<TImage> mUploadImages = new ArrayList<>();
    private HashMap<String, String> mUploadImageMaps = new HashMap<>();
    private TakePhoto mTakePhoto;
    private InvokeParam mInvokeParam;
    private String mCoinAddress = "";
    private String mCoinName = "";
    private String mCoinLogo = "";
    private AwesomeValidation mAwesomeValidation;
    private int mUploadedImagesNum = 0;
    final private int mImageLimit = 3;

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
        PermissionManager.handlePermissionsResult(CreateCoinTaskActivity.this, type, mInvokeParam,CreateCoinTaskActivity.this);
    }

    @Override
    public PermissionManager.TPermissionType invoke(InvokeParam invokeParam) {
        PermissionManager.TPermissionType type = PermissionManager.checkPermission(TContextWrap.of(CreateCoinTaskActivity.this),invokeParam.getMethod());
        if(PermissionManager.TPermissionType.WAIT.equals(type)){
            CreateCoinTaskActivity.this.mInvokeParam = invokeParam;
        }
        return type;
    }

    @SuppressLint("ResourceType")
    @Override
    public void takeSuccess(TResult result) {
        ArrayList<TImage> tmpImages = new ArrayList<>();
        tmpImages = result.getImages();
        if (tmpImages.size() > 0) {
            final android.support.v7.widget.GridLayout ll = findViewById(R.id.activity_create_coin_task_upload_logo);
            int uploadImageWidth = findViewById(R.id.activity_create_coin_task_take_photo).getWidth();
            ArrayList<File> files = new ArrayList<>();
            for (final TImage image : tmpImages) {
                mUploadImages.add(image);
                SimpleDraweeView imgDraweeView = new SimpleDraweeView(CreateCoinTaskActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                layoutParams.width = uploadImageWidth;
                layoutParams.height = layoutParams.width;
                imgDraweeView.setLayoutParams(layoutParams);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                imgDraweeView.getHierarchy().setRoundingParams(roundingParams);
                imgDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new MaterialDialog.Builder(CreateCoinTaskActivity.this)
                                .autoDismiss(false)
                                .canceledOnTouchOutside(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(MaterialDialog dialog, DialogAction which) {
                                        ll.removeView(v);
                                        File imgFile = new File(image.getOriginalPath());
                                        mUploadImages.remove(image);
                                        mUploadImageMaps.remove(imgFile.getName());
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
                ll.addView(imgDraweeView);
                files.add(file);
            }
            uploadImages(files);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_coin_task);
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

    private void uploadImages(final List<File> files) {
        JSONObject params = new JSONObject();
        mUploadedImagesNum = files.size();
        try {
            params.put("token", mCoinAddress);
            params.put("amount", mUploadImages.size());
            Api.request("uploadCoinTaskImages", "POST", params, false,CreateCoinTaskActivity.this, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.e(String.valueOf(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();

                    Logger.i(jsonStr);
                    if (TextUtils.isEmpty(jsonStr)) {
                        CreateCoinTaskActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MaterialDialog.Builder(CreateCoinTaskActivity.this)
                                    .title(R.string.dialog_tip_title)
                                    .content(R.string.dialog_unknow_err_tip)
                                    .positiveText(R.string.dialog_positive)
                                    .show();
                                mUploadedImagesNum = 0;
                            }
                        });
                    } else {
                        try {
                            JSONArray data = new JSONArray(jsonStr);
                            for (int i = 0; i < data.length(); i ++) {
                                final File file = files.get(i);
                                JSONObject d = data.getJSONObject(i);
                                final String qiniuKey = d.optString("key");
                                final String qiniuUpToken = d.optString("uptoken");
                                final String qiniuLink = d.optString("link");
                                if ( ! TextUtils.isEmpty(qiniuKey) && ! TextUtils.isEmpty(qiniuUpToken) && ! TextUtils.isEmpty(qiniuLink)) {
                                    Configuration qiniuConfig = new Configuration.Builder().build();
                                    UploadManager uploadManager = new UploadManager(qiniuConfig);
                                    uploadManager.put(file, String.valueOf(qiniuKey), String.valueOf(qiniuUpToken), new UpCompletionHandler() {
                                        @Override
                                        public void complete(String key, ResponseInfo info, JSONObject res) {
                                            if(info.isOK()) {
                                                Logger.i("qiniu Upload Success");
                                                mUploadImageMaps.put(file.getName(), qiniuLink);
                                            } else {
                                                Logger.i("qiniu Upload Fail");
                                            }
                                            Logger.i("qiniu" + key + ",\r\n " + info + ",\r\n " + res);
                                            mUploadedImagesNum --;
                                        }
                                    }, null);
                                }
                            }
                        } catch (JSONException e) {
                            mUploadedImagesNum = 0;
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            mUploadedImagesNum = 0;
            e.printStackTrace();
        } catch (JSONException e) {
            mUploadedImagesNum = 0;
            e.printStackTrace();
        }
    }

    private void initClick() {
        final Button submitView = findViewById(R.id.activity_create_coin_task_submit);
        TextInputLayout titleView = findViewById(R.id.activity_create_coin_task_title);
        TextInputLayout descView = findViewById(R.id.activity_create_coin_task_desc);
        TextInputLayout bonusView = findViewById(R.id.activity_create_coin_task_bonus);
        TextInputLayout amountView = findViewById(R.id.activity_create_coin_task_amount);
        TextInputLayout startDateView = findViewById(R.id.activity_create_coin_task_start_date_picker);
        TextInputLayout endDateView = findViewById(R.id.activity_create_coin_task_end_date_picker);
        mAwesomeValidation.addValidation(CreateCoinTaskActivity.this, titleView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_task_title);
        mAwesomeValidation.addValidation(CreateCoinTaskActivity.this, descView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_task_desc);
        mAwesomeValidation.addValidation(CreateCoinTaskActivity.this, bonusView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_task_bonus);
        mAwesomeValidation.addValidation(CreateCoinTaskActivity.this, amountView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_task_amount);
        mAwesomeValidation.addValidation(CreateCoinTaskActivity.this, startDateView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_task_start_date);
        mAwesomeValidation.addValidation(CreateCoinTaskActivity.this, endDateView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_task_end_date);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    if (mUploadedImagesNum > 0) {
                        CreateCoinTaskActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MaterialDialog.Builder(CreateCoinTaskActivity.this)
                                        .title(R.string.dialog_tip_title)
                                        .content(R.string.dialog_tip_uploading_image)
                                        .positiveText(R.string.dialog_positive)
                                        .show();
                            }
                        });
                        return;
                    }
                    TextInputEditText titleContentView = findViewById(R.id.activity_create_coin_task_title_content);
                    TextInputEditText descContentView = findViewById(R.id.activity_create_coin_task_desc_content);
                    TextInputEditText bonusContentView = findViewById(R.id.activity_create_coin_task_bonus_content);
                    TextInputEditText tagsContentView = findViewById(R.id.activity_create_coin_task_tags_content);
                    TextInputEditText amountContentView = findViewById(R.id.activity_create_coin_task_amount_content);
                    TextInputEditText startDateContentView = findViewById(R.id.activity_create_coin_task_start_date_picker_content);
                    TextInputEditText endDateContentView = findViewById(R.id.activity_create_coin_task_end_date_picker_content);
                    RadioButton needEvidenceView = findViewById(R.id.activity_create_coin_task_need_evidence_content);
                    String title = titleContentView.getText().toString();
                    String desc = descContentView.getText().toString();
                    String bonus = bonusContentView.getText().toString();
                    String tags = tagsContentView.getText().toString();
                    String amount = amountContentView.getText().toString();
                    String startDate = startDateContentView.getText().toString();
                    String endDate = endDateContentView.getText().toString();
                    try {
                        UiUtil.showLoading(CreateCoinTaskActivity.this);
                        JSONObject params = new JSONObject();
                        Object[] imagesValues = mUploadImageMaps.values().toArray();
                        String[] imagesStringArr = new String[mUploadImageMaps.size()];
                        for (int i = 0; i < imagesValues.length; i++) {
                            imagesStringArr[i] = String.valueOf(imagesValues[i]);
                        }
                        Logger.i(StringUtils.join(imagesStringArr, ","));
                        Date startDateObj = DTUtil.dateParse(startDate, DTUtil.DATE_PATTERN);
                        Date endDateObj = DTUtil.dateParse(endDate, DTUtil.DATE_PATTERN);
                        params.put("token", mCoinAddress);
                        params.put("title", title);
                        params.put("desc", desc);
                        params.put("amount", Integer.valueOf(amount));
                        params.put("bonus", Double.valueOf(bonus));
                        params.put("tags", tags);
                        params.put("start_date", DTUtil.dateFormat(startDateObj, DTUtil.DATE_TIME_API_PATTERN));
                        params.put("end_date", DTUtil.dateFormat(endDateObj, DTUtil.DATE_TIME_API_PATTERN));
                        params.put("images", StringUtils.join(imagesStringArr, ","));
                        params.put("need_evidence", needEvidenceView.isChecked() ? 1 : -1);
                        Api.request("createCoinTask", "POST", params, false,CreateCoinTaskActivity.this, new Callback() {
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
                                    CreateCoinTaskActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(CreateCoinTaskActivity.this)
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
                                            CreateCoinTaskActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MaterialDialog.Builder(CreateCoinTaskActivity.this)
                                                        .title(R.string.dialog_tip_title)
                                                        .content(msg)
                                                        .positiveText(R.string.dialog_positive)
                                                        .show();
                                                }
                                            });
                                        } else {
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
                    } catch (ParseException e) {
                        UiUtil.hideLoading();
                        e.printStackTrace();
                    }
                }
            }
        });

        final TextInputEditText startDatePickerView = findViewById(R.id.activity_create_coin_task_start_date_picker_content);
        startDatePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(CreateCoinTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        startDatePickerView.setText(String.format("%d-%s-%s", i, (i1 < 10 ? "0" + (i1 + 1) : i1 + 1), (i2 < 10 ? "0" + i2 : i2)));
                    }
                }, year, month, day).show();
            }
        });

        final TextInputEditText endDatePickerView = findViewById(R.id.activity_create_coin_task_end_date_picker_content);
        endDatePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(CreateCoinTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        endDatePickerView.setText(String.format("%d-%s-%s", i, (i1 < 10 ? "0" + (i1 + 1) : i1 + 1), (i2 < 10 ? "0" + i2 : i2)));
                    }
                }, year, month, day).show();
            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        mCoinAddress = intent.getStringExtra("coin_address");
        mCoinName = intent.getStringExtra("coin_name");
        mCoinLogo = intent.getStringExtra("coin_logo");
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
    }

    private void initTitleBar() {
        mToolbar = findViewById(R.id.view_toolbar);
        TextView textView = mToolbar.findViewById(R.id.view_toolbar_title);
        textView.setText("新建任务");
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    private void initTakePhoto() {
        View view = findViewById(R.id.activity_create_coin_task_take_photo);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] stringItems = {"拍照", "相册"};
                new MaterialDialog.Builder(CreateCoinTaskActivity.this)
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
