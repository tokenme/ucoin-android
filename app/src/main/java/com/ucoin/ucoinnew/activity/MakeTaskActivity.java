package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.flyco.dialog.listener.OnBtnClickL;
import com.flyco.dialog.listener.OnOperItemClickL;
import com.flyco.dialog.widget.ActionSheetDialog;
import com.flyco.dialog.widget.NormalDialog;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.util.UiUtil;
import com.ucoin.ucoinnew.util.Util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.io.File;
import java.util.ArrayList;

public class MakeTaskActivity extends TakePhotoActivity {

    private CommonTitleBar mTitleBar;
    private ArrayList<TImage> uploadImages = new ArrayList<>();
    final private int imageLimit = 6;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_task);
        init();
    }

    private void init() {
        initTitleBar();
        initView();
        initTakePhoto();
    }

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
            final android.support.v7.widget.GridLayout ll = findViewById(R.id.activity_make_task_upload_images);
            int uploadImageWidth = findViewById(R.id.activity_make_task_take_photo).getWidth();
            for (final TImage image : tmpImages) {
                uploadImages.add(image);
                SimpleDraweeView imgDraweeView = new SimpleDraweeView(MakeTaskActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
                layoutParams.width = uploadImageWidth;
                layoutParams.height = layoutParams.width;
                imgDraweeView.setLayoutParams(layoutParams);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
                imgDraweeView.getHierarchy().setRoundingParams(roundingParams);
                imgDraweeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        final NormalDialog dialog = new NormalDialog(MakeTaskActivity.this);
                        dialog.content("是否确定删除?")
                                .btnText("取消", "确定")
                                .show();
                        dialog.setOnBtnClickL(
                                new OnBtnClickL() {
                                    @Override
                                    public void onBtnClick() {
                                        dialog.dismiss();
                                    }
                                },
                                new OnBtnClickL() {
                                    @Override
                                    public void onBtnClick() {
                                        ll.removeView(v);
                                        uploadImages.remove(image);
                                        dialog.dismiss();
                                    }
                                });
                    }
                });
                ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.fromFile(new File(image.getOriginalPath())))
                        .setResizeOptions(new ResizeOptions(uploadImageWidth, uploadImageWidth))
                        .build();
                PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                        // .setControllerListener(controllerListener)
                        .setOldController(imgDraweeView.getController())
                        .setImageRequest(request)
                        .build();
                imgDraweeView.setController(controller);
                UiUtil.setMargins(imgDraweeView, 0, 0, 10, 10);
                ll.addView(imgDraweeView);
            }
        }
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

    private void initView() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String pic = intent.getStringExtra("pic");
        TextView titleView = findViewById(R.id.activity_make_task_title);
        titleView.setText(title);
        TextView descView = findViewById(R.id.activity_make_task_desc);
        descView.setText(desc);

        Uri picUri = Uri.parse(pic);
        SimpleDraweeView picDraweeView = findViewById(R.id.activity_make_task_pic);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
        picDraweeView.getHierarchy().setRoundingParams(roundingParams);
        picDraweeView.setImageURI(picUri);
    }

    private void initTakePhoto() {
        View view = findViewById(R.id.activity_make_task_take_photo);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] stringItems = {"拍照", "相册"};
                ExpandableListView elv = findViewById(R.id.elv);
                final ActionSheetDialog dialog = new ActionSheetDialog(MakeTaskActivity.this, stringItems, elv);
                dialog.title(String.format("您还可以选择%d张图片", imageLimit - uploadImages.size()))
                        .show();

                dialog.setOnOperItemClickL(new OnOperItemClickL() {
                    @Override
                    public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TakePhoto takePhoto = getTakePhoto();
                        CompressConfig compressConfig = new CompressConfig.Builder()
                                .setMaxSize(800 * 800)
                                .setMaxPixel(800)
                                .create();
                        takePhoto.onEnableCompress(compressConfig,false);
                        switch(position) {
                            case 0:
                                File file = new File(Environment.getExternalStorageDirectory(), "/ucoin/temp/" + System.currentTimeMillis() + ".jpg");
                                if ( ! file.getParentFile().exists()) {
                                    file.getParentFile().mkdirs();
                                }
                                Uri imageUri = Uri.fromFile(file);
                                takePhoto.onPickFromCapture(imageUri);
                                break;
                            case 1:
                                takePhoto.onPickMultiple(imageLimit - uploadImages.size());
                                break;
                        }
                        dialog.dismiss();
                    }
                });
            }
        });
    }
}
