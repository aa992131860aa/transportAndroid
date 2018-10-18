package com.otqc.transbox.db;

import org.litepal.crud.DataSupport;

/**
 * Created by 99213 on 2017/11/14.
 */

public class PowerTemp extends DataSupport {
  private String Time;
  private int level;

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
