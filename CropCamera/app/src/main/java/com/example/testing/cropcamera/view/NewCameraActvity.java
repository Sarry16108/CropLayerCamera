package com.example.testing.cropcamera.view;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.testing.cropcamera.R;
import com.example.testing.cropcamera.databinding.ActivityNewCameraBinding;


/**
 * Created by Administrator on 2017/4/5.
 * 支持拍照剪切。
 */

public class NewCameraActvity extends Activity {

    static class Size {
        private int width = 0;
        private int height = 0;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public Size() {
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
    //各种拍照类型
    public static final int  CAMERA_TIP_DEFAULT = -1;  //没有提示内容
    public static final int  CAMERA_TIP_DRIVERING_LICENSE = 1;   //行驶证
    public static final int  CAMERA_TIP_CAR_FRONT_IMG = 2;   //车头照
    public static final int  CAMERA_TIP_TRANSPORT_LICENSE = 3;   //道路运输证
    public static final int  CAMERA_TIP_PERSON_IMG= 4;   //个人头像
    public static final int  CAMERA_TIP_DRIVER_CARD = 5;   //驾驶证照
    public static final int  CAMERA_TIP_TRANSPORT_QUALIFICATE_LICENSE = 6;   //道路运输从业资格证

    public static final Size SIZE_DEFAULT                           = new Size(0, 0);
    public static final Size SIZE_DRIVERING_LICENSE                 = new Size(3, 1);
    public static final Size SIZE_CAR_FRONT_IMG                      = new Size(1, 1);
    public static final Size SIZE_TRANSPORT_LICENSE                 = new Size(24, 17);
    public static final Size SIZE_PERSON_IMG                         = new Size(1, 1);
    public static final Size SIZE_DRIVER_CARD                        = new Size(3, 1);
    public static final Size SIZE_TRANSPORT_QUALIFICATE_LICENSE   = new Size(24, 17);


    private boolean mIsPortrait = false;
    private ActivityNewCameraBinding    mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mIsPortrait = getIntent().getBooleanExtra("isPortrait", false);
        if (mIsPortrait && ActivityInfo.SCREEN_ORIENTATION_PORTRAIT != getRequestedOrientation()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else if (!mIsPortrait && ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE != getRequestedOrientation()) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_new_camera);

        String path = getIntent().getStringExtra("path");
        int type = getIntent().getIntExtra("type", 0);


        Size imgSize = getImageSize(type);
        mBinding.cameraDrawView.setPath(path);
        mBinding.cameraDrawView.setVisibleSize(imgSize.getWidth(), imgSize.getHeight(), mIsPortrait);


        showTipLayer(type);
    }


    //size是一个宽高比
    public static Size getImageSize(int type) {
        switch (type) {
            case CAMERA_TIP_DRIVERING_LICENSE:
                return SIZE_DRIVERING_LICENSE;
            case CAMERA_TIP_CAR_FRONT_IMG:
                return SIZE_CAR_FRONT_IMG;
            case CAMERA_TIP_TRANSPORT_LICENSE:
                return SIZE_TRANSPORT_LICENSE;
            case CAMERA_TIP_PERSON_IMG:
                return SIZE_PERSON_IMG;
            case CAMERA_TIP_DRIVER_CARD:
                return SIZE_DRIVER_CARD;
            case CAMERA_TIP_TRANSPORT_QUALIFICATE_LICENSE:
                return SIZE_TRANSPORT_QUALIFICATE_LICENSE;
            case CAMERA_TIP_DEFAULT:
            default:
                return SIZE_DEFAULT;
        }
    }

    public void showTipLayer(int type) {
        ViewStub viewStub = null;
        switch (type) {
            case CAMERA_TIP_DRIVERING_LICENSE:
                viewStub = (ViewStub) mBinding.driveringLicense.getViewStub();
                break;
            case CAMERA_TIP_CAR_FRONT_IMG:
                viewStub = (ViewStub) mBinding.carFrontImg.getViewStub();
                break;
            case CAMERA_TIP_TRANSPORT_LICENSE:
                viewStub = (ViewStub) mBinding.transportLicense.getViewStub();
                break;
            case CAMERA_TIP_PERSON_IMG:
                viewStub = (ViewStub) mBinding.personImg.getViewStub();
                break;
            case CAMERA_TIP_DRIVER_CARD:
                viewStub = (ViewStub) mBinding.driverCard.getViewStub();
                break;
            case CAMERA_TIP_TRANSPORT_QUALIFICATE_LICENSE:
                viewStub = (ViewStub) mBinding.transportQualificateLicense.getViewStub();
                break;
            case CAMERA_TIP_DEFAULT:
            default:
                return;
        }
        viewStub.setVisibility(View.VISIBLE);

        LinearLayout tipLayer = (LinearLayout) mBinding.getRoot().findViewById(R.id.tipLayer);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tipLayer.getLayoutParams();
        Rect layoutRect = mBinding.cameraDrawView.getImageRect();
        layoutParams.width = layoutRect.width();
        layoutParams.height = layoutRect.height();
        layoutParams.topMargin = layoutRect.top;
        layoutParams.leftMargin = layoutRect.left;
        tipLayer.setLayoutParams(layoutParams);
    }


    //拍照（包括剪切）页面
    //type  拍照的类型，根据类型选择覆盖层
    public static void toNewCamera(Activity activity, int request, String path, int type, boolean isPortrait) {
        Intent intent = new Intent(activity, NewCameraActvity.class);
        intent.setFlags(isPortrait ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        intent.putExtra("path", path);
        intent.putExtra("type", type);
        intent.putExtra("isPortrait", isPortrait);
        activity.startActivityForResult(intent, request);
    }
}
