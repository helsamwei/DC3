package com.example.dc3;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.dc3.FileUnitil.MyConstans;
import com.example.dc3.RP_Class.RPListClass;
import com.example.dc3.ScanService.Scan;
import com.example.dc3.ScanService.SensorService;
import com.example.dc3.ScanService.WifiAndBleService;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author :hswei0614
 * @date:2019/02/22;
 * @description:the optimization of rssi collecting
 */

/**
 * @author：hswei0614
 * @date:2019/02/23
 * @descripton:添加了显示已采集的RP ListView及数据采集时的等待对话框
 */

/**
 * @author：hswei0614
 * @date:2019/02/24
 * @description:对输入框进行非空判断，并且在活动变为不可见时保存相关参数的设置
 */

/**
 * @author :hswei0614
 * @date:2019/04/04
 * @description:添加IMU传感器的扫描
 */

/**
 * @author :hswei
 * @date: 2019/04/10
     * @description; 将Wifi扫描结果弃用广播接收
 */

/**
 * @author :hswei0614
 * @date  :2019/05/06
 * @description   :将扫描放在后台服务IntentService中
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    /**
     * 参数列表
     * @param
     */
    public ProgressDialog waitingDialog;
    public static MainActivity mainActivity;

    private EditText Duration,Device,Head,inputX,inputY,inputZ;
    private String xx,yy,zz,dur,device,head;
    private float Dur,x,y,z;
    private int Dev;
    private char Orentation;
    private Button START;
    private Chronometer chronometer;
    private ListView rpListView;
    private FloatingActionButton floatingAct;
    private final String TAG_SERVICE = "ok";
    private SensorManager sensorManager;
    private WifiManager mWifiManager;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private Context context;
    public  Scan scan;
    private RPListClass rpList = new RPListClass();
    private ArrayList<String> RpListData;
    private ArrayAdapter<String> adapter;

//    public static scanThread scanT;
//    public static sensorThread sensorT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initManager();

        scan = new Scan(context,sensorManager,mWifiManager,mBluetoothAdapter,chronometer);
        if(savedInstanceState != null){
            loadData(savedInstanceState);
            Log.e(TAG_SERVICE, "Saved!");
        }
        createDic();
        mainActivity = this;
        Log.e(TAG_SERVICE, "onCreate: ");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(!mWifiManager.isWifiEnabled()){
            mWifiManager.setWifiEnabled(true);
        }

        START.setOnClickListener(this);
        floatingAct.setOnClickListener(this);
        Log.e(TAG_SERVICE, "onResume: ");

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG_SERVICE, "onSaveInstanceState: " + "save data");
        super.onSaveInstanceState(outState);
        String dur = Duration.getText().toString();
        if(!dur.isEmpty()){
            outState.putString("Dur",dur);
        }
        String id = Device.getText().toString();
        if(!id.isEmpty()){
            outState.putString("ID",id);
        }
        if(!TextUtils.isEmpty(Head.getText())) {
            char head = Head.getText().toString().charAt(0);
            outState.putChar("Head", head);
        }
        String x = inputX.getText().toString();
        if(!x.isEmpty()){
            outState.putString("input_x",x);
        }
        String y = inputY.getText().toString();
        if(!y.isEmpty()){
            outState.putString("input_y",y);
        }
        String z = inputZ.getText().toString();
        if(!z.isEmpty()){
            outState.putString("input_z",z);
        }
        if(RpListData != null){
            ArrayList<String> RpList = RpListData;
            outState.putStringArrayList("RPList",RpList);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.e(TAG_SERVICE, "onRestoreInstanceState:");
        loadData(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start:
                //判段输入是否都完成
                Boolean isInputCompleted = (Duration.getText().toString().isEmpty())||
                        (Device.getText().toString().isEmpty())|| (Head.getText().toString().isEmpty())
                        ||(inputX.getText().toString().isEmpty()) ||(inputY.getText().toString().isEmpty())
                        ||(inputZ.getText().toString().isEmpty());
                if(!isInputCompleted){
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    chronometer.start();
                    WaitingDialog();
                    xx = inputX.getText().toString();
                    x = Float.parseFloat(xx);
                    yy = inputY.getText().toString();
                    y = Float.parseFloat(yy);
                    zz = inputZ.getText().toString();
                    z = Float.parseFloat(zz);
                    device = Device.getText().toString();
                    Dev = Integer.parseInt(device);
                    head = Head.getText().toString();
                    Orentation = head.charAt(0);
                    dur = Duration.getText().toString();
                    Dur = Float.parseFloat(dur);

                    rpList.addString(xx,yy,zz,device,head);
                    RpListData = rpList.getList();
                    adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_list_item_1,RpListData);
                    rpListView.setAdapter(adapter);

                    Intent intentService_WifiAndBle = new Intent(MainActivity.this, WifiAndBleService.class);
                    intentService_WifiAndBle.putExtra("scanMode",0);
                    intentService_WifiAndBle.putExtra("dur",Dur);
                    intentService_WifiAndBle.putExtra("device",Dev);
                    intentService_WifiAndBle.putExtra("head",Orentation);
                    intentService_WifiAndBle.putExtra("x",x);
                    intentService_WifiAndBle.putExtra("y",y);
                    intentService_WifiAndBle.putExtra("z",z);
                    startService(intentService_WifiAndBle);

                    Intent intentService_sensor = new Intent(MainActivity.this, SensorService.class);
                    intentService_sensor.putExtra("scanMode",0);
                    intentService_sensor.putExtra("dur",Dur);
                    intentService_sensor.putExtra("device",Dev);
                    intentService_sensor.putExtra("head",Orentation);
                    intentService_sensor.putExtra("x",x);
                    intentService_sensor.putExtra("y",y);
                    intentService_sensor.putExtra("z",z);
                    startService(intentService_sensor);


                }
                else {
                    Toast.makeText(this,"请完成相关参数的设置！",Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.model:
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
        }
    }

    /**
     * 初始化UI组件
     */
    public void initUI(){
        Duration = findViewById(R.id.Duration);
        Device = findViewById(R.id.device);
        Head = findViewById(R.id.head);
        inputX = findViewById(R.id.inputX);
        inputY = findViewById(R.id.inputY);
        inputZ = findViewById(R.id.inputZ);
        START = findViewById(R.id.start);
        chronometer = findViewById(R.id.chronometer);
        rpListView = findViewById(R.id.rpList);
        floatingAct = findViewById(R.id.model);
    }

    /**
     * 初始化Wifi和蓝牙及其相应的权限
     */
    public void initManager(){
        context = this;
        mWifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        mBluetoothManager = (BluetoothManager) getApplication().getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            finish();
        }

    }

    /**
     * 创建存储路径
     */
    public void createDic(){
        File file1 = new File(MyConstans.DATA_PATH1);
        File file2 = new File(MyConstans.DATA_PATH2);
        if(!file1.exists() || !file2.exists()){
            file1.mkdirs();
            file2.mkdirs();
        }
    }
    /**
     * 等待定时采样结束dialog
     *
     */
    public void WaitingDialog(){
        /**
         * 等待Dialog具有屏蔽其他控件交互能力
         * @setCancelable为是屏幕不可点击，设置不可取消(false)
         * 下载等事件完成后，主动调用函数关闭该dialog
         */
        waitingDialog = new ProgressDialog(MainActivity.this);
        waitingDialog.setTitle("数据定时采集中，请等待!");
        waitingDialog.setMessage("采集中...");
        waitingDialog.setCancelable(false);
        waitingDialog.show();
    }


    /**
     * 当Activity由不可见变为可见时重载输入的参数及已采集数据的RP列表
     * @param bundle
     */
    public void loadData(Bundle bundle){
        if(bundle.getBoolean("Dur")){
            String Dur = bundle.getString("Dur");
            Duration.setText(Dur);
        }
        if(bundle.getBoolean("ID")){
            String ID = bundle.getString("ID");
            Device.setText(ID);
        }
        if(bundle.getBoolean("Head")){
            char head = bundle.getChar("Head");
            Head.setText(head);
        }
        if(bundle.getBoolean("input_x")){
            String x = bundle.getString("input_x");
            inputX.setText(x);
        }
        if(bundle.getBoolean("input_y")){
            String y = bundle.getString("input_y");
            inputX.setText(y);
        }
        if(bundle.getBoolean("input_z")){
            String z = bundle.getString("input_z");
            inputX.setText(z);
        }
        if(bundle.getBoolean("RPList")){
            ArrayList<String> RpList = bundle.getStringArrayList("RPList");
            adapter = new ArrayAdapter<String>(MainActivity.this,
                    android.R.layout.simple_list_item_1,RpList);
            rpListView.setAdapter(adapter);
        }
    }
}
