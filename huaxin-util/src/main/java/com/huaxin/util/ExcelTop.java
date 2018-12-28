package com.huaxin.util;

import java.util.List;

/**
 * Created by U on 2016/7/2.
 */
public class ExcelTop {
    private int rowIndex;
    private int rowText;
    private int rowNum;
    private int start;
    private int end;
    private String data;
    private List<ExcelTop> ets;
    private boolean sl;
    public int getRowText() {
        return rowText;
    }

    public void setRowText(int rowText) {
        this.rowText = rowText;
    }

    public int getRowNum() {
        return rowNum;
    }

    public void setRowNum(int rowNum) {
        this.rowNum = rowNum;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public List<ExcelTop> getEts() {
        return ets;
    }

    public void setEts(List<ExcelTop> ets) {
        this.ets = ets;
    }

    public boolean isSl() {
        return sl;
    }

    public void setSl(boolean sl) {
        this.sl = sl;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
