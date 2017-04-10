package teamfour.bluetoothtracker;


import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    String regNum;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*I really need to add a menu bar later*/

        //Creating the BTManager and the BTAdapter
        final BluetoothManager bluetoothManager =(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        final BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        /*The buttons for the scan and locate*/
        Button scanButton = (Button)findViewById(R.id.scanButton);
        final Button locateButton = (Button)findViewById(R.id.locateButton);

        /* Ensures Bluetooth is available on the device and it is enabled. If not,
        displays a dialog requesting user permission to enable Bluetooth.*/
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }

        /*Getting Fine Location Access from the User. For odd reasons, the device won't scan if the location access isn't granted by the user.
        * Also getting the READ_PHONE_STATE permission in the same dialog*/
            if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION , android.Manifest.permission.READ_PHONE_STATE},
                                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                    /*ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE , android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);*/
                }
            }

        SharedPreferences sharedPreferences = getSharedPreferences("hasRunBefore", 0);
        final SharedPreferences.Editor edit = sharedPreferences.edit();
        boolean hasRun = sharedPreferences.getBoolean("hasRun", false);
        if (!hasRun) {
            edit.putBoolean("hasRun", true);


            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = LayoutInflater.from(this);

            final View dialogView = layoutInflater.inflate(R.layout.alertdialog, null);
            final EditText editText = (EditText) dialogView.findViewById(R.id.alert_dialog_editText);

            // Keyboard
            final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

            // Auto show keyboard
            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean isFocused) {

                    if (isFocused)
                    {
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    }
                }
            });

            builder.setView(dialogView)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            regNum = editText.getText().toString();
                            Log.d("STACKOVERFLOW", "Registration number: " + regNum);

                            edit.putString("regNumber" , regNum);
                            edit.commit();
                           /* String filename = "myfile.txt";
                            FileOutputStream outputStream;

                            try {
                                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                                outputStream.write(regNum.getBytes());
                                outputStream.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/

                            TextView textView = (TextView) findViewById(R.id.regNum_textView);
                            textView.setText(regNum);

                            // Hide keyboard
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            // Hide keyboard
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                            dialog.cancel();
                        }
                    });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();


        }
        else
        {
            //code if the app HAS run before

            SharedPreferences prefs1 = getApplicationContext().getSharedPreferences("hasRunBefore",0);
            regNum = prefs1.getString("regNumber" , "");

            TextView textView = (TextView) findViewById(R.id.regNum_textView);
            textView.setText(regNum);
        }

            scanButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StartScan(bluetoothLeScanner);

                    SharedPreferences prefs = getApplicationContext().getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("regNumber", regNum );
                    editor.apply();

                    /*TextView textView = (TextView) findViewById(R.id.regNum_textView);
                    textView.setText(regNum);*/
                }
            });
            /*The button for the locate calls the locate functions when pressed*/
            locateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    Locate();
                }
            });}


    public void StartScan(final BluetoothLeScanner bluetoothLeScanner) {
    //Perform the Scan actions here
    Toast.makeText(MainActivity.this, "Scanning", Toast.LENGTH_LONG).show();
    Log.v("S","Scan Has Started");

    final ScanSettings scanSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

    final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            String IMEI = telephonyManager.getDeviceId();

            userData uData = new userData(IMEI , result.getDevice().getName(), regNum);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference(regNum);

            databaseReference.setValue(uData);

            TextView textView = (TextView)findViewById(R.id.textView);
            textView.setText(result.getDevice().getName() + " is the current location ID");
        }

        @Override
        public void onScanFailed(int errorCode)
        {
            super.onScanFailed(errorCode);
            Log.e("Error","Error Occurred");
        }
        };

        //The list for the filters
        final ArrayList<ScanFilter> filters= new ArrayList<>();

        //MAC addresses of ble devices that needs to be scanned. No other devices will be scanned other than these devices.
        String[] filterList = { "40:B9:04:96:59:D6" , "5E:81:40:1A:76:B4" };

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
        int stopScanPeriod = 5000, startScanPeriod = 10000;
        handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            bluetoothLeScanner.stopScan(scanCallback);
        }
        },stopScanPeriod);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bluetoothLeScanner.startScan(filters,scanSettings,scanCallback);
            }
        },startScanPeriod);
    }

    public void Locate()
    {
            //Perform the Locate actions here
            Toast.makeText(this, "Locate Started", Toast.LENGTH_LONG).show();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = database.getReference();
            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    /*userData data =  dataSnapshot.getValue(userData.class);*/

                    TextView textView1 = (TextView)findViewById(R.id.textView);
                    /*textView1.setText(data.registerNum +" is at " + data.BeaconID);*/

                    /*This code works and prints the value for single user. The active code below prints
                    all the users*/

                    textView1.setText("The user list is as follows\n");

                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren())
                    {
                        userData post = postSnapshot.getValue(userData.class);

                        textView1.append(post.registerNum +" is at " + post.BeaconID + "\n");

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
    }
}








