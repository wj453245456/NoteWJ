package wj.com.note;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.w3c.dom.Text;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wj.com.note.db.Note;

public class Edit extends AppCompatActivity {

    public LocationClient mLocationClient;
    private EditText editText;
    private Button editApply;
    private CheckBox locationButton;
    private TextView locationText;
    private ImageView locationIcon;
    private boolean Islocated = false;
    private String times;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(new MyLocationListener());
        setContentView(R.layout.activity_edit);
        editText = (EditText)findViewById(R.id.edit_text);
        editApply = (Button)findViewById(R.id.edit_apply);
        locationButton = (CheckBox) findViewById(R.id.locationButton);
        locationText = (TextView)findViewById(R.id.location_text);
        locationIcon = (ImageView)findViewById(R.id.loaction_icon);
       locationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
           public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
               if (locationButton.isChecked()){
                   locationButton.setText("取消定位");
                   locationIcon.setVisibility(View.VISIBLE);
                   inlocation();
                   locationText.setVisibility(View.VISIBLE);
               }else{
                   Islocated = false;
                   locationButton.setText("打开定位");
                   locationIcon.setVisibility(View.GONE);
                   locationText.setVisibility(View.GONE);
               }
           }
       });
        //提醒功能
        final int year, month, day, hour, minute;
        final Button time_chose = (Button)findViewById(R.id.time_chose);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        time_chose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder timeString = new StringBuilder();
                TimePickerDialog timeDialog = new TimePickerDialog(Edit.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeString.append(hourOfDay+":"+minute);
                        Toast.makeText(Edit.this,timeString,Toast.LENGTH_SHORT).show();
                        SimpleDateFormat df1 = new SimpleDateFormat("yyyy MM dd  HH:mm");
                        SimpleDateFormat df2 = new SimpleDateFormat("yy MM dd  HH:mm");
                        try{
                            Date d1 = df1.parse(timeString.toString());
                            times = df2.format(d1);
                            time_chose.setText(times);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }



                    }
                },hour,minute,true);
                timeDialog.show();
                Dialog dateDialog = new DatePickerDialog(Edit.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        timeString.append(year+" "+(month+1)+" "+dayOfMonth+"  ");
                    }
                },year,month,day);
                dateDialog.show();
            }
        });
        editText.addTextChangedListener(watcher);//编辑框监听
        editApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note();
                note.setNoteText(editText.getText().toString());
                SimpleDateFormat format=new SimpleDateFormat("yy MM dd  HH:mm");
                Date date = new Date();
                if (Islocated){
                    note.setLocated(true);
                    note.setLocation(locationText.getText().toString());
                }else{
                    note.setLocated(false);
                }
                note.setNoteTime(format.format(date));
                note.setAlarm(times);
                note.setStatus(1);//设记录状态为未开始
                note.save();
                Intent intent = new Intent(Edit.this,MainActivity.class);
                startActivity(intent);
            }
        });



    }
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            editApply.setVisibility(View.VISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length()<=0){
                editApply.setVisibility(View.INVISIBLE);
            }
        }
    };
    //定位权限组
    private void inlocation(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(Edit.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(Edit.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(Edit.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(Edit.this,permissions,1);
        }else {
            initLocation();
            mLocationClient.start();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意所有全县才能试用本程序",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    initLocation();
                    mLocationClient.start();
                }else {
                    Toast.makeText(this,"发生未知错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            double x = bdLocation.getLongitude();
            double y = bdLocation.getLatitude();
            double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
            double temp = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
            double bdLon = z * Math.cos(temp) + 0.0065;
            double bdLat = z * Math.sin(temp) + 0.006;
            bdLocation.setLatitude(bdLat);
            bdLocation.setLongitude(bdLon);
            final StringBuilder currentPosition = new StringBuilder();
            currentPosition.append(bdLocation.getCountry()).append(" ");
            currentPosition.append(bdLocation.getProvince()).append(" ");
            currentPosition.append(bdLocation.getCity()).append(" ");
            currentPosition.append(bdLocation.getDistrict()).append(" ");
            currentPosition.append(bdLocation.getStreet()).append(" ");
            if (bdLocation.getStreetNumber().equals("")) {
                currentPosition.append("\n");
            } else {
                currentPosition.append(bdLocation.getStreetNumber()).append("\n");
            }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Islocated = true;
                                locationText.setText(currentPosition);
                            }
                        });
                    }
                }).start();

        }
        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }
public void initLocation(){
    LocationClientOption option = new LocationClientOption();
    option.setIsNeedAddress(true);
    mLocationClient.setLocOption(option);
}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
