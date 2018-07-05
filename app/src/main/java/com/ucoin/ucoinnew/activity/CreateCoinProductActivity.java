package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

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
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.orhanobut.logger.Logger;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.utils.StringUtils;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.util.UiUtil;
import com.ucoin.ucoinnew.util.DTUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;

public class CreateCoinProductActivity extends TakePhotoActivity {

    private CommonTitleBar mTitleBar;
    private ArrayList<TImage> mUploadImages = new ArrayList<>();
    private HashMap<String, String> mUploadImageMaps = new HashMap<>();
    private String mTokenAddress = "";
    private String mTokenName = "";
    private String mTokenLogo = "";
    private AwesomeValidation mAwesomeValidation;
    final private int mImageLimit = 3;

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
            final android.support.v7.widget.GridLayout ll = findViewById(R.id.activity_create_coin_product_upload_logo);
            int uploadImageWidth = findViewById(R.id.activity_create_coin_product_take_photo).getWidth();
            ArrayList<File> files = new ArrayList<>();
            for (final TImage image : tmpImages) {
                mUploadImages.add(image);
                SimpleDraweeView imgDraweeView = new SimpleDraweeView(CreateCoinProductActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                layoutParams.width = uploadImageWidth;
                layoutParams.height = layoutParams.width;
                imgDraweeView.setLayoutParams(layoutParams);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                imgDraweeView.getHierarchy().setRoundingParams(roundingParams);
                imgDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        new MaterialDialog.Builder(CreateCoinProductActivity.this)
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
        setContentView(R.layout.activity_create_coin_product);
        init();
    }

    private void init() {
        initTitleBar();
        initTakePhoto();
        initView();
        initClick();
    }

    private void uploadImages(final List<File> files) {
        JSONObject params = new JSONObject();
        try {
            params.put("token", mTokenAddress);
            params.put("amount", mUploadImages.size());
            Api.request("uploadCoinProductImages", "POST", params, false,CreateCoinProductActivity.this, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Logger.e(String.valueOf(e));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();

                    Logger.i(jsonStr);
                    if (TextUtils.isEmpty(jsonStr)) {
                        CreateCoinProductActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new MaterialDialog.Builder(CreateCoinProductActivity.this)
                                    .title(R.string.dialog_tip_title)
                                    .content(R.string.dialog_unknow_err_tip)
                                    .positiveText(R.string.dialog_positive)
                                    .show();
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
                                        }
                                    }, null);
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

    private void initClick() {
        final Button submitView = findViewById(R.id.activity_create_coin_product_submit);
        TextInputLayout titleView = findViewById(R.id.activity_create_coin_product_title);
        TextInputLayout descView = findViewById(R.id.activity_create_coin_product_desc);
        TextInputLayout priceView = findViewById(R.id.activity_create_coin_product_price);
        TextInputLayout amountView = findViewById(R.id.activity_create_coin_product_amount);
        TextInputLayout startDateView = findViewById(R.id.activity_create_coin_product_start_date_picker);
        TextInputLayout endDateView = findViewById(R.id.activity_create_coin_product_end_date_picker);
        mAwesomeValidation.addValidation(CreateCoinProductActivity.this, titleView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_product_title);
        mAwesomeValidation.addValidation(CreateCoinProductActivity.this, descView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_product_desc);
        mAwesomeValidation.addValidation(CreateCoinProductActivity.this, priceView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_product_price);
        mAwesomeValidation.addValidation(CreateCoinProductActivity.this, amountView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_product_amount);
        mAwesomeValidation.addValidation(CreateCoinProductActivity.this, startDateView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_product_start_date);
        mAwesomeValidation.addValidation(CreateCoinProductActivity.this, endDateView.getId(), RegexTemplate.NOT_EMPTY, R.string.activity_create_coin_product_end_date);
        submitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate()) {
                    TextInputEditText titleContentView = findViewById(R.id.activity_create_coin_product_title_content);
                    TextInputEditText descContentView = findViewById(R.id.activity_create_coin_product_desc_content);
                    TextInputEditText priceContentView = findViewById(R.id.activity_create_coin_product_price_content);
                    TextInputEditText tagsContentView = findViewById(R.id.activity_create_coin_product_tags_content);
                    TextInputEditText amountContentView = findViewById(R.id.activity_create_coin_product_amount_content);
                    TextInputEditText startDateContentView = findViewById(R.id.activity_create_coin_product_start_date_picker_content);
                    TextInputEditText endDateContentView = findViewById(R.id.activity_create_coin_product_end_date_picker_content);
                    String title = titleContentView.getText().toString();
                    String desc = descContentView.getText().toString();
                    String price = priceContentView.getText().toString();
                    String tags = tagsContentView.getText().toString();
                    String amount = amountContentView.getText().toString();
                    String startDate = startDateContentView.getText().toString();
                    String endDate = endDateContentView.getText().toString();
                    try {
                        UiUtil.showLoading(CreateCoinProductActivity.this);
                        JSONObject params = new JSONObject();
                        Object[] imagesValues = mUploadImageMaps.values().toArray();
                        String[] imagesStringArr = new String[0];
                        for (int i = 0; i < imagesValues.length; i++) {
                            imagesStringArr[i] = String.valueOf(imagesValues[i]);
                        }
                        Date startDateObj = DTUtil.dateParse(startDate, DTUtil.DATE_PATTERN);
                        Date endDateObj = DTUtil.dateParse(endDate, DTUtil.DATE_PATTERN);
                        params.put("token", mTokenAddress);
                        params.put("title", title);
                        params.put("desc", desc);
                        params.put("amount", Integer.valueOf(amount));
                        params.put("price", Double.valueOf(price));
                        params.put("tags", tags);
                        params.put("start_date", DTUtil.dateFormat(startDateObj, "yyyy-MM-dd'T'HH:mm:ssZ"));
                        params.put("end_date", DTUtil.dateFormat(endDateObj, "yyyy-MM-dd'T'HH:mm:ssZ"));
                        params.put("images", StringUtils.join(imagesStringArr, ","));
                        Api.request("createCoinProduct", "POST", params, false,CreateCoinProductActivity.this, new Callback() {
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
                                    CreateCoinProductActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new MaterialDialog.Builder(CreateCoinProductActivity.this)
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
                                            CreateCoinProductActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new MaterialDialog.Builder(CreateCoinProductActivity.this)
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

        final TextInputEditText startDatePickerView = findViewById(R.id.activity_create_coin_product_start_date_picker_content);
        startDatePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(CreateCoinProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        startDatePickerView.setText(String.format("%d-%s-%s", i, (i1 < 10 ? "0" + i1 : i1), (i2 < 10 ? "0" + i2 : i2)));
                    }
                }, year, month, day).show();
            }
        });

        final TextInputEditText endDatePickerView = findViewById(R.id.activity_create_coin_product_end_date_picker_content);
        endDatePickerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar ca = Calendar.getInstance();
                int year = ca.get(Calendar.YEAR);
                int month = ca.get(Calendar.MONTH);
                int day = ca.get(Calendar.DAY_OF_MONTH);
                new DatePickerDialog(CreateCoinProductActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        endDatePickerView.setText(String.format("%d-%s-%s", i, (i1 < 10 ? "0" + i1 : i1), (i2 < 10 ? "0" + i2 : i2)));
                    }
                }, year, month, day).show();
            }
        });
    }

    private void initView() {
        Intent intent = getIntent();
        mTokenAddress = intent.getStringExtra("token_address");
        mTokenName = intent.getStringExtra("token_name");
        mTokenLogo = intent.getStringExtra("token_logo");
        mAwesomeValidation = new AwesomeValidation(TEXT_INPUT_LAYOUT);
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
        View view = findViewById(R.id.activity_create_coin_product_take_photo);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] stringItems = {"拍照", "相册"};
                new MaterialDialog.Builder(CreateCoinProductActivity.this)
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
