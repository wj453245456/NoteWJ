package wj.com.note;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.platform.comapi.map.B;

import org.w3c.dom.Text;

import java.util.List;

import wj.com.note.db.Note;

/**
 * Created by wj on 2017/5/22.
 */

public class NoteAdapter extends ArrayAdapter<Note>{
    private int resourceId;
    private LocalBroadcastManager  localBroadcastManager;
    private boolean statusischecked = false;
    public NoteAdapter(Context context, int textViewResourceId, List<Note> objects){
        super(context,textViewResourceId,objects);
        resourceId = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Note note = getItem(position);
        final View view = LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView noteContent = (TextView) view.findViewById(R.id.note_content);
        TextView noteTime = (TextView) view.findViewById(R.id.note_time);
        LinearLayout linearLayout = (LinearLayout)view.findViewById(R.id.note_location) ;
        TextView noteLocationText = (TextView)view.findViewById(R.id.note_location_text);
        final Button status = (Button)view.findViewById(R.id.status_button);
        final RadioGroup rg = (RadioGroup)view.findViewById(R.id.status_group);
        final RadioButton r1 =(RadioButton)view.findViewById(R.id.status_r1);
        final RadioButton r2 =(RadioButton)view.findViewById(R.id.status_r2);
        final RadioButton r3 =(RadioButton)view.findViewById(R.id.status_r3);
        final Button sapply = (Button)view.findViewById(R.id.status_apply);
        LinearLayout itemhead = (LinearLayout)view.findViewById(R.id.item_head);
        ImageView itemfoot = (ImageView)view.findViewById(R.id.item_foot);
        TextView alarmText = (TextView)view.findViewById(R.id.alarm_text);
        LinearLayout alarm = (LinearLayout)view.findViewById(R.id.alarmLin);
        noteContent.setText(note.getNoteText());
        noteTime.setText(note.getNoteTime());
        if (note.isLocated()){
            linearLayout.setVisibility(View.VISIBLE);
            noteLocationText.setText(note.getLocation());
        }
        if (note.getAlarm()==null||note.getAlarm().equals("")){
            alarm.setVisibility(View.GONE);
        }else {
            alarmText.setText(note.getAlarm());
        }

        status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!statusischecked){
                    statusischecked = true;
                    rg.setVisibility(View.VISIBLE);
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
                    sapply.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            note.save();
                            localBroadcastManager = LocalBroadcastManager.getInstance(getContext());
                            Intent intent = new Intent("wj.com.note.LOCAL_UPDATE_UI");
                            localBroadcastManager.sendBroadcast(intent);
                            rg.setVisibility(View.GONE);
                            statusischecked = false;
                        }
                    });
                }


            }
        });
        switch (note.getStatus()){
            case 1:
                status.setTextColor(getContext().getResources().getColor(R.color.color3));
                itemhead.setBackgroundColor(getContext().getResources().getColor(R.color.color3));
                itemfoot.setBackgroundColor(getContext().getResources().getColor(R.color.color3));
                status.setText("未开始");
                break;
            case 2:
                status.setTextColor(getContext().getResources().getColor(R.color.color4));
                itemhead.setBackgroundColor(getContext().getResources().getColor(R.color.color4));
                itemfoot.setBackgroundColor(getContext().getResources().getColor(R.color.color4));
                status.setText("正在进行");
                break;
            case 3:
                status.setTextColor(getContext().getResources().getColor(R.color.color2));
                itemhead.setBackgroundColor(getContext().getResources().getColor(R.color.color2));
                itemfoot.setBackgroundColor(getContext().getResources().getColor(R.color.color2));
                status.setText("已完成");
                break;
        }
        return view;
    }
}
