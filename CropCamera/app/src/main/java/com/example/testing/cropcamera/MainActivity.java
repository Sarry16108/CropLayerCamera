package com.example.testing.cropcamera;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.testing.cropcamera.databinding.ActivityMainBinding;
import com.example.testing.cropcamera.view.NewCameraActvity;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        mBinding.takePic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCameraActvity.toNewCamera(MainActivity.this, 1, path + "/test.jpeg", NewCameraActvity.CAMERA_TIP_PERSON_IMG, true);
            }
        });
        mBinding.takePic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCameraActvity.toNewCamera(MainActivity.this, 1, path + "/test1.jpeg", NewCameraActvity.CAMERA_TIP_DRIVER_CARD, false);
            }
        });
        mBinding.takePic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCameraActvity.toNewCamera(MainActivity.this, 1, path + "/test2.jpeg", NewCameraActvity.CAMERA_TIP_DRIVERING_LICENSE, false);
            }
        });
        mBinding.takePic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCameraActvity.toNewCamera(MainActivity.this, 1, path + "/test3.jpeg", NewCameraActvity.CAMERA_TIP_TRANSPORT_LICENSE, false);
            }
        });
        mBinding.takePic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewCameraActvity.toNewCamera(MainActivity.this, 1, path + "/test4.jpeg", NewCameraActvity.CAMERA_TIP_DEFAULT, true);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK != resultCode) {
            return;
        }

        if (1 == requestCode) {
            Toast.makeText(this, "save path:" + path, Toast.LENGTH_SHORT).show();
        }
    }
}
