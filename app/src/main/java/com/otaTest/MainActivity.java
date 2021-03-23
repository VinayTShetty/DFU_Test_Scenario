package com.otaTest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class MainActivity extends AppCompatActivity {

    Button btn_dfuPdate,btn_fielSelection;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_dfuPdate=(Button)findViewById(R.id.dfuUpdate);
        btn_fielSelection=(Button)findViewById(R.id.fileSelection);
        btn_dfuPdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    dfuupdate();
                }
            }
        });

        btn_fielSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    checkPermissionGiven();
                }
            }
        });


    }
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    public void dfuupdate(){
        final DfuServiceInitiator starter = new DfuServiceInitiator("C2:81:CF:7F:8C:1F")
                .setForeground(false)
                .setDeviceName("Succorfish SC2");

        starter.setPrepareDataObjectDelay(300L);

        if (0 == DfuService.TYPE_AUTO)
            starter.setZip(fileStreamUri, filepath);
        else {
            starter.setBinOrHex(0, uri, filepath).setInitFile(uri, filepath);
        }
        starter.start(this, DfuService.class);

    }
    Uri uri;
    private String filepath;

    private Uri fileStreamUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("DATA "+data.getData());
        uri=data.getData();
        fileStreamUri=data.getData();
        filepath=uri.getPath();
        filepath=uri.getPath();
    }

    private final DfuProgressListener dfuProgressListener=new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(@NonNull String s) {
            System.out.println("DFU_TESTING onDeviceConnecting");
        }

        @Override
        public void onDeviceConnected(@NonNull String s) {
            System.out.println("DFU_TESTING onDeviceConnected");
        }

        @Override
        public void onDfuProcessStarting(@NonNull String s) {
            System.out.println("DFU_TESTING onDfuProcessStarting");
        }

        @Override
        public void onDfuProcessStarted(@NonNull String s) {
            System.out.println("DFU_TESTING onDfuProcessStarted");
        }

        @Override
        public void onEnablingDfuMode(@NonNull String s) {
            System.out.println("DFU_TESTING onEnablingDfuMode");
        }

        @Override
        public void onProgressChanged(@NonNull String s, int i, float v, float v1, int i1, int i2) {
            System.out.println("DFU_TESTING onProgressChanged");
        }

        @Override
        public void onFirmwareValidating(@NonNull String s) {
            System.out.println("DFU_TESTING onFirmwareValidating");
        }

        @Override
        public void onDeviceDisconnecting(String s) {
            System.out.println("DFU_TESTING onDeviceDisconnecting");
        }

        @Override
        public void onDeviceDisconnected(@NonNull String s) {
            System.out.println("DFU_TESTING onDeviceDisconnected");
        }

        @Override
        public void onDfuCompleted(@NonNull String s) {
            System.out.println("DFU_TESTING onDfuCompleted");
        }

        @Override
        public void onDfuAborted(@NonNull String s) {
            System.out.println("DFU_TESTING onDfuAborted");
        }

        @Override
        public void onError(@NonNull String s, int i, int i1, String s1) {
            System.out.println("DFU_TESTING onError s= "+s);
            System.out.println("DFU_TESTING onError s1= "+s1);
            System.out.println("DFU_TESTING onError i= "+i);
            System.out.println("DFU_TESTING onError i1= "+i1);
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void checkPermissionGiven() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            /**
             * opening of the file logic here
             */
            openFilesStorage();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, internalStorageReadPermissionRequestCode);
        }

    }

    private void openFilesStorage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, START_ACTIVITY_REQUEST_CODE);
    }

    private final int internalStorageReadPermissionRequestCode = 100;
    public static final int START_ACTIVITY_REQUEST_CODE=101;
}