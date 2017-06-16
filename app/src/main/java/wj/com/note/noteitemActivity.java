package wj.com.note;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.platform.comapi.map.B;

import org.litepal.crud.DataSupport;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wj.com.note.db.Note;

public class noteitemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noteitem);
        String id = getIntent().getStringExtra("i_d");
        //view
        final RadioGroup rg = (RadioGroup)findViewById(R.id.status_group_nitem);
        final RadioButton r1 =(RadioButton)findViewById(R.id.status_r1_nitem);
        final RadioButton r2 =(RadioButton)findViewById(R.id.status_r2_nitem);
        final RadioButton r3 =(RadioButton)findViewById(R.id.status_r3_nitem);

        TextView title = (TextView)findViewById(R.id.nitem_title);
        final EditText content = (EditText) findViewById(R.id.nitem_content);
        final Button setalarm = (Button)findViewById(R.id.set_alarm);
        Button quitalarm = (Button)findViewById(R.id.quit_alarm);
        LinearLayout loaction = (LinearLayout)findViewById(R.id.nitem_location);
        TextView locationText = (TextView)findViewById(R.id.nitem_location_text);
        Button apply = (Button)findViewById(R.id.nitem_apply);
        final TextView alarm = (TextView)findViewById(R.id.nitem_alarm_text);
        Log.d("sss", "onCreate: "+id);
        List<Note> notes = DataSupport.where("id=?",id).find(Note.class);
        final Note note =notes.get(0);
        note.setAlarm("");

        content.setText(note.getNoteText());
        if (note.isLocated()){
            loaction.setVisibility(View.VISIBLE);
            locationText.setText(note.getLocation());
        }
        //标题
        switch (note.getStatus()){
            case 1:
                title.setText(note.getNoteTime()+"     未开始");
                title.setBackgroundColor(getResources().getColor(R.color.color3));
                r1.setChecked(true);
                break;
            case 2:
                title.setText(note.getNoteTime()+"     正在进行");
                title.setBackgroundColor(getResources().getColor(R.color.color4));
                r2.setChecked(true);
                break;
            case 3:
                title.setText(note.getNoteTime()+"     已完成");
                title.setBackgroundColor(getResources().getColor(R.color.color2));
                r3.setChecked(true);
                break;
        }
        //状态选择


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==r1.getId()){
                    note.setStatus(1);
                }
                if (checkedId==r2.getId()){
                    note.setStatus(2);
                }
                if (checkedId==r3.getId()){
                    note.setStatus(3);
                }
            }
        });


        //设置闹钟
        if (note.getAlarm()==null||note.getAlarm().equals("")){
            alarm.setText("无");
        }else {
            alarm.setText(note.getAlarm());
        }

        final int year, month, day, hour, minute;
        final Button time_chose = (Button)findViewById(R.id.time_chose);
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        setalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder timeString = new StringBuilder();
                TimePickerDialog timeDialog = new TimePickerDialog(noteitemActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeString.append(hourOfDay+":"+minute);
                        Toast.makeText(noteitemActivity.this,timeString,Toast.LENGTH_SHORT).show();
                        SimpleDateFormat df1 = new SimpleDateFormat("yyyy MM dd  HH:mm");
                        SimpleDateFormat df2 = new SimpleDateFormat("yy MM dd  HH:mm");
                        try{
                            Date d1 = df1.parse(timeString.toString());
                            String times = df2.format(d1);
                            alarm.setText(times);
                            note.setAlarm(times);
                        }catch (ParseException e){
                            e.printStackTrace();
                        }
                    }
                },hour,minute,true);
                timeDialog.show();
                Dialog dateDialog = new DatePickerDialog(noteitemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        timeString.append(year+" "+(month+1)+" "+dayOfMonth+"  ");
                    }
                },year,month,day);
                dateDialog.show();
            }
        });

        //取消闹钟
        quitalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setAlarm("");
                alarm.setText("无");
            }
        });
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                note.setNoteText(content.getText().toString());
                note.save();
                finish();
            }
        });
    }

}
