package com.otaTest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.otaTest.CustomOBjects.CustBluetootDevices;
import com.otaTest.Fragments.FragmentScan;
import com.otaTest.Service.BluetoothLeService;
import com.otaTest.Service.DfuService;
import com.otaTest.interfaceActivityFragment.PassConnectionStatusToFragment;
import com.otaTest.interfaceActivityFragment.PassScanDeviceToActivity_interface;
import com.otaTest.interfaceFragmentActivity.DeviceConnectDisconnect;
import com.otaTest.interfaceFragmentActivity.OtaUpdate;

import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class MainActivity extends AppCompatActivity
        implements
        DeviceConnectDisconnect, OtaUpdate {

    public static final String TAG=MainActivity.class.getSimpleName();
    /**
     *BluetoothLeService class Variables.
     */
    public BluetoothLeService mBluetoothLeService;
    String mDeviceAddress="D4:A6:CB:43:B6:70";
    /*    Button demoapplicaiton,sendCommand;*/


    /**
     *Scan for the Ble Devices.
     */
    private BluetoothLeScanner bluetoothLeScanner =
            BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
    private boolean mScanning;
    private Handler handler = new Handler();
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    /**
     * Activity to Fragment interface
     */
    PassScanDeviceToActivity_interface passScanDeviceToActivity_interface;
    PassConnectionStatusToFragment passConnectionStatusToFragment;
    public static  String SCAN_TAG="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        interfaceIntialization();
        bindBleServiceToMainActivity();
        intializeFragmentManager();
      /*  demoapplicaiton=(Button) findViewById(R.id.demo_applciaiton);
        sendCommand=(Button) findViewById(R.id.send_command);*/

      /*  demoapplicaiton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    mBluetoothLeService.connect(mDeviceAddress);
                }
            }
        });

        sendCommand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String test="smpon";
               mBluetoothLeService.sendDataToBleDevice(test.getBytes());
            }
        });*/
        //scanLeDevice();
        //////////////////////////
        replaceFragmentTransaction(new FragmentScan(),null);
     //   replaceFragmentTransaction(new FragmentData(),null);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetootServiceRecieverData, makeGattUpdateIntentFilter());
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetootServiceRecieverData);
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (fragment.toString().equalsIgnoreCase(new FragmentScan().toString())) {
        }/*else if(fragment.toString().equalsIgnoreCase(new FragmentData().toString())){
            replaceFragmentTransaction(new FragmentScan(),null);
        }*/
    }

    /**
     * Code to manage Service life Cycle.
     */
    private final ServiceConnection serviceConnection=new ServiceConnection() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                finish();
            }
            // mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService = null;
        }
    };
    /**
     * BroadCast Reciever tot Recieve Data from BLE Service class..
     */
    private boolean mConnected = false;
    private final BroadcastReceiver bluetootServiceRecieverData=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String data=intent.getStringExtra(BluetoothLeService.EXTRA_DATA);
            }else if((action!=null)&&(action.equalsIgnoreCase(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS)))||(action.equalsIgnoreCase(getResources().getString(R.string.CONNECTION_STATUS_BLE_DEVICE)))){
                String bleAddress=intent.getStringExtra((getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS)));
                boolean connectionStatus=intent.getBooleanExtra(getResources().getString(R.string.CONNECTION_STATUS_BLE_DEVICE),false);
                passConnectionSucesstoFragmentScanForUIChange(bleAddress,connectionStatus);
            }
        }
        private void passConnectionSucesstoFragmentScanForUIChange(String connectedDeviceAddress,boolean connect_disconnect) {
            if(passConnectionStatusToFragment!=null){
                passConnectionStatusToFragment.connectDisconnect(connectedDeviceAddress,connect_disconnect);
            }
        }
    };
    /**
     * BroadCast Reciever Data Trigger.
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(getResources().getString(R.string.BLUETOOTHLE_SERVICE_BLE_ADDRESS));
        return intentFilter;
    }

    private void bindBleServiceToMainActivity(){
        Intent intent = new Intent(this, BluetoothLeService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    /**
     * Scan for the BLE Devices.
     */
    public void scanLeDevice() {
        if (!mScanning) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    bluetoothLeScanner.stopScan(leScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            bluetoothLeScanner.startScan(leScanCallback);
        } else {
            mScanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
    }

    public void start_stop_scan(){
        if(SCAN_TAG.equalsIgnoreCase(getResources().getString(R.string.SCAN_STOPED))||(SCAN_TAG.equalsIgnoreCase(""))){
            startScan();
        }
    }

    private void startScan(){
        SCAN_TAG=getResources().getString(R.string.SCAN_STARTED);
        bluetoothLeScanner.startScan(leScanCallback);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning = false;
                stopScan();
            }
        }, SCAN_PERIOD);
    }
    private void stopScan(){
        SCAN_TAG=getResources().getString(R.string.SCAN_STOPED);
        bluetoothLeScanner.stopScan(leScanCallback);
    }

    private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    if(passScanDeviceToActivity_interface!=null){
                        if(result!=null){
                            if((result.getDevice().getName()!=null)&&(result.getDevice().getName().length()>0)){
                                passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(),result.getDevice().getName(),result.getDevice(),false));
                            }else {
                                passScanDeviceToActivity_interface.sendCustomBleDevice(new CustBluetootDevices(result.getDevice().getAddress(),"NA",result.getDevice(),false));

                            }
                        }
                    }
                }
            };

    /**
     * Fragment Transaction
     */

    public void replaceFragmentTransaction(Fragment fragment,Bundle bundleData){
        fragmentTransaction= fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    private void intializeFragmentManager(){
        fragmentManager=getSupportFragmentManager();
    }

    /**
     * Interface intialization (From Activity to Fragment)
     */
    public void  setupPassScanDeviceToActivity_interface(PassScanDeviceToActivity_interface loc_passScanDeviceToActivity_interface){
        this.passScanDeviceToActivity_interface=loc_passScanDeviceToActivity_interface;
    }



    public void setupPassConnectionStatusToFragment(PassConnectionStatusToFragment locpassConnectionStatusToFragment){
        this.passConnectionStatusToFragment=locpassConnectionStatusToFragment;
    }

    private void interfaceIntialization(){
        setupPassScanDeviceToActivity_interface(new PassScanDeviceToActivity_interface() {
            @Override
            public void sendCustomBleDevice(CustBluetootDevices custBluetootDevices) {

            }
        });

        setupPassConnectionStatusToFragment(new PassConnectionStatusToFragment() {
            @Override
            public void connectDisconnect(String bleAddress, boolean connected_disconnected) {

            }
        });
    }
    @Override
    public void makeDevieConnecteDisconnect(CustBluetootDevices custBluetootDevices, boolean connect_disconnect) {
        if(connect_disconnect){
            mBluetoothLeService.connect(custBluetootDevices.getBleAddress());
        }else {
            mBluetoothLeService.disconnect();
        }
    }


    CustBluetootDevices custBluetootDevicesForOta;

    private Uri fileStreamUri;
    public void dfuupdate(CustBluetootDevices custBluetootDevices){
        Log.d(TAG, "dfuupdate: dfuUpdate method called");
        final DfuServiceInitiator starter = new DfuServiceInitiator(custBluetootDevices.getBleAddress())
                .setForeground(false)
                .setDeviceName(custBluetootDevices.getDeviceName());

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("DATA "+data.getData());
        uri=data.getData();
        fileStreamUri=data.getData();
        filepath=uri.getPath();
       // filepath=uri.getPath();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: DFU OTA UPDATE CALLED");
                dfuupdate(custBluetootDevicesForOta);
            }
        });

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
            System.out.println("DFU_TESTING onError");
        }
    };

    @Override
    public void makeOTA_For_Device(CustBluetootDevices custBluetootDevices) {
        custBluetootDevicesForOta=custBluetootDevices;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 7);

    }
}