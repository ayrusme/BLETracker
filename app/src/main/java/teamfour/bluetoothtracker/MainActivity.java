package teamfour.bluetoothtracker;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity

{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*I really need to add a menu bar later*/
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        //Creating the BTManager and the BTAdapter
        final BluetoothManager bluetoothManager =(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        /*Getting the device's IMEI number. This is unique to each user.*/
        String IMEI;
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        IMEI = telephonyManager.getDeviceId();



        /*The buttons for the scan and locate*/
        Button scanButton = (Button)findViewById(R.id.scanButton);
        Button locateButton = (Button)findViewById(R.id.locateButton);

        /* Ensures Bluetooth is available on the device and it is enabled. If not,
        displays a dialog requesting user permission to enable Bluetooth.*/
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        /*Getting Fine Location Access from the User. For odd reasons, the device won't scan if the location access isn't granted by the user.*/
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                }
            }

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartScan(bluetoothLeScanner);
                }
            });
            /*The button for the locate calls the locate functions when pressed*/
            locateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Locate();
                }
            });}



    public void StartScan(final BluetoothLeScanner bluetoothLeScanner) {
    //Perform the Scan actions here
    Toast.makeText(MainActivity.this, "Scanning", Toast.LENGTH_LONG).show();
    Log.v("S","Scan Has Started");

    ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            String new_result = result.toString();
            Log.v(new_result, "Visible?");
            TextView textView = (TextView)findViewById(R.id.textView);
            textView.setText(new_result);

        }

        @Override
        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);
            Log.e("Error","Error Occurred");
        }
    };

    //The list for the filters
    ArrayList<ScanFilter> filters= new ArrayList<>();

    //MAC addresses of ble devices that needs to be scanned. No other devices will be scanned other than these devices.
    String[] filterList = {"4B:46:E7:E9:35:EF"};

    //Adding the MAC addresses to the filters list
    for(int i=0; i< filterList.length ; i++) {
        ScanFilter filter = new ScanFilter.Builder().setDeviceAddress(filterList[i]).build();
        filters.add(filter);

    }

    /*The scan starts here. The MAC ID's in the filters array are the only one's which will be scanned.
    * If they're found in the range of the BLE device, it returns a callback*/
    bluetoothLeScanner.startScan(filters,scanSettings,scanCallback);

    /*This function is to stop the scan after a minute, which is the scanPeriod ( in milliseconds)*/
    Handler handler = new Handler();
    int scanPeriod = 60000;
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            bluetoothLeScanner.stopScan(scanCallback);
        }
    },scanPeriod);

}

    public void Locate() {
        //Perform the Locate actions here
        Toast.makeText(MainActivity.this, "Locate Started", Toast.LENGTH_LONG).show();

    }
}








