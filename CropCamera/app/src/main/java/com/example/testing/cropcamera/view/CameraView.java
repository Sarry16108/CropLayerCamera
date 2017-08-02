package com.example.testing.cropcamera.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2017/4/5.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback, Camera.AutoFocusCallback {

    public static interface BitmapCallback {
        void onCallback(Bitmap bitmap);
    }

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private int         mWidth;
    private int         mHeight;
    private String mSavePath;
    private Rect mImageRect;
    private Rect mCropRect;  //和mImageRect不一样，mImageRect是屏幕中一块，该值是bitmap中对应位置的一块
    private boolean     mIsPreview = false;
    private boolean     mIsPortrait = false;

    public CameraView(Context context) {
        super(context);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public void setPreviewSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setImageRect(Rect rect, boolean isPortrait) {
        mImageRect = rect;
        mIsPortrait = isPortrait;
    }

    public void setPath(String path) {
        mSavePath = path;
    }

    //设置parameter
    private void setCameraSize(int width, int height) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setJpegQuality(100);

        Camera.Size picSize = getCroperSize(parameters.getSupportedPictureSizes(), height, width);
        if (null != picSize) {
            parameters.setPictureSize(picSize.width, picSize.height);
        }

        Camera.Size preSize = getCroperSize(parameters.getSupportedPreviewSizes(), height, width);
        if (null != preSize) {
            parameters.setPreviewSize(preSize.width, preSize.height);
        }

        if (parameters.getSupportedFocusModes().contains(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }

        if (mIsPortrait) {
            parameters.setRotation(90);
        }

        mCamera.setParameters(parameters);
    }

    //获取合适的size
    private Camera.Size getCroperSize(List<Camera.Size> sizes, int height, int width) {
        Camera.Size result = null;
        for (Camera.Size size : sizes) {
            if (size.height == height && size.width == width/*size.height * width == size.width * height*/) {
                result = size;
                break;
            }
        }

        //取16/9
        if (null == result) {
            for (Camera.Size size : sizes) {
                if (16 * size.height == 9 * size.width || 4 * size.height == 3 * size.width) {
                    result = size;
                    break;
                }
            }
        }

        return result;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (null == mCamera) {
            mCamera = Camera.open();
            setCameraSize(mHeight, mWidth);
            if (mIsPortrait) {
                mCamera.setDisplayOrientation(90);
            }

            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

    @Override
    public void onAutoFocus(boolean success, Camera camera) {

    }

    public boolean isPreview() {
        return mIsPreview;
    }

    public void start() {
        if (null != mCamera) {
            mCamera.startPreview();
            mIsPreview = true;
        }
    }

    public void stop() {
        if (null != mCamera) {
            mCamera.stopPreview();
            mIsPreview = false;
        }
    }

    public void release() {
        if (null != mCamera) {
            try {
                mCamera.release();
            } catch (Exception ex) {
                Log.e("cameraView", "camera release()");
            }

            mCamera = null;
            mIsPreview = false;
        }
    }

    public void takePicture(final BitmapCallback bitmapCallback) {
        if (null != mCamera) {
            mIsPreview = false;
            mCamera.takePicture(null, null, new Camera.PictureCallback() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    bitmap = cropBitmap(bitmap);
                    if (null != bitmapCallback) {
                        bitmapCallback.onCallback(bitmap);
                    }

//                    saveData(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test2.jpeg", data);
                    saveBitmap(mSavePath, bitmap);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB_MR1)
    private Bitmap cropBitmap(Bitmap bitmap) {
        if (null == mImageRect) {
            return bitmap;
        } else {
            //计算等比例截取区域
            if (null == mCropRect) {
                mCropRect = new Rect();
                float widthScale = 0, heightScale = 0;
                if (mIsPortrait) {
                    widthScale = bitmap.getWidth() * 1.0f / mWidth;
                    heightScale = bitmap.getHeight() * 1.0f / mHeight;
                } else {
                    widthScale = bitmap.getWidth() * 1.0f / mHeight;
                    heightScale = bitmap.getHeight() * 1.0f / mWidth;
                }

                mCropRect.left = (int) (mImageRect.left * widthScale);
                mCropRect.top = (int) (mImageRect.top * heightScale);
                mCropRect.right = (int) (mImageRect.right * widthScale);
                mCropRect.bottom = (int) (mImageRect.bottom * heightScale);
            }

            Bitmap cropedBitmap = Bitmap.createBitmap(bitmap, mCropRect.left, mCropRect.top, mCropRect.width(), mCropRect.height(), null, false);
            bitmap.recycle();
            return cropedBitmap;
        }
    }


    private void saveData(String path, byte []datas) {
        if (null == path) {
            throw new NullPointerException("image path can not be null");
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(datas);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveBitmap(String path, Bitmap bitmap) {
        if (null == path) {
            throw new NullPointerException("image path can not be null");
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bitmap.recycle();
        }

    }
}
