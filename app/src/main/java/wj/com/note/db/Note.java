package wj.com.note.db;

import org.litepal.crud.DataSupport;

/**
 * Created by wj on 2017/5/22.
 */

public class Note extends DataSupport{
    private  int id;
    private String noteText;//文本
    private String noteTime;//发布时间
    private boolean isLocated;
    private  String location;
    private int status;
    private String alarm;//闹铃

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isLocated() {
        return isLocated;
    }

    public void setLocated(boolean located) {
        isLocated = located;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getNoteTime() {
        return noteTime;
    }

    public void setNoteTime(String noteTime) {
        this.noteTime = noteTime;
    }
}
