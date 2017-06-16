package wj.com.note;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.Collections;
import java.util.List;

import wj.com.note.db.Note;

public class MainActivity extends AppCompatActivity {

    private CollapsingToolbarLayout toolbar;
    private Toolbar toolbar_title;
    private MyListView listView;
    private FloatingActionButton edit;
    private DrawerLayout mDrawerLayout;
    private IntentFilter intentFilter;
    private LocalReceiver localReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private Button cshowmode;
    private TextView showmode;
    private int showmodenum = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //View实例化
        toolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_bar);
        toolbar_title = (Toolbar) findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar_title);
        edit = (FloatingActionButton)findViewById(R.id.edit_fab);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Edit.class);
                startActivity(intent);
            }
        });
        mDrawerLayout = (DrawerLayout)findViewById(R.id.activity_main);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.home);
        }
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_call:
                        try{
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:13100935892"));
                            startActivity(intent);
                        } catch(SecurityException e){
                                 e.printStackTrace();
                                  }
                        break;
                }
                return true;
            }
        });
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction("wj.com.note.LOCAL_UPDATE_UI");
        localReceiver = new LocalReceiver();
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);
        //改变显示模式
        final LinearLayout smodecolor = (LinearLayout)findViewById(R.id.smode_color);
        cshowmode = (Button)findViewById(R.id.change_showmode);
        showmode = (TextView)findViewById(R.id.showmode);
        cshowmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (showmodenum){
                    case 1:
                        showmodenum = 2;
                        showmode.setText("显示未完成记录");
                        smodecolor.setBackgroundColor(getResources().getColor(R.color.color3));
                        onResume();
                        break;
                    case 2:
                        showmodenum = 3;
                        showmode.setText("显示正在进行记录");
                        smodecolor.setBackgroundColor(getResources().getColor(R.color.color4));
                        onResume();
                        break;
                    case 3:
                        showmodenum = 4;
                        showmode.setText("显示已完成记录");
                        smodecolor.setBackgroundColor(getResources().getColor(R.color.color2));
                        onResume();
                        break;
                    case 4:
                        showmodenum = 1;
                        showmode.setText("显示所有记录");
                        smodecolor.setBackgroundColor(getResources().getColor(R.color.color1));
                        onResume();
                        break;
                }
            }
        });
        //启动闹钟服务
        Intent intent = new Intent(this,alarm.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
    }
    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Note> noteList = DataSupport.findAll(Note.class);
        switch (showmodenum){
            case 1:
                noteList = DataSupport.findAll(Note.class);
                break;
            case 2:
                noteList = DataSupport.where("status=?","1").find(Note.class);
                break;
            case 3:
                noteList = DataSupport.where("status=?","2").find(Note.class);
                break;
            case 4:
                noteList = DataSupport.where("status=?","3").find(Note.class);
                break;
        }
        Collections.reverse(noteList);
        NoteAdapter adapter = new NoteAdapter(this,R.layout.note_item,noteList);
        listView = (MyListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        if (listView.getFooterViewsCount()==0){
            TextView tV = new TextView(this);
            tV.setText("Best wish to 龚乐施！\n2017 6.26\nfrom wj");
            listView.addFooterView(tV);
        }
        //单击查看
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Note note = (Note)listView.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this,noteitemActivity.class);
                i.putExtra("i_d",""+note.getId());
                startActivity(i);
            }
        });
        //长摁删除功能
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                long [] pattern = {0,100};
                vibrator.vibrate(pattern,-1);
                Snackbar.make(view,"删除记录",Snackbar.LENGTH_SHORT).setAction("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Note note =(Note) listView.getItemAtPosition(position);
                        DataSupport.deleteAll(Note.class,"id=?",""+note.getId());
                        onResume();
                        Toast.makeText(MainActivity.this,"成功删除",Toast.LENGTH_SHORT).show();
                    }
                }).show();

                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}
