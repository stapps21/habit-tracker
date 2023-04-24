package com.teampingui.models;

import com.teampingui.exceptions.NotInDatabaseException;

public class JournalEntry {
    private int mID = -1;
    private String mContent;
    private String mDate;

    public JournalEntry(String date, String entry) {
        this.mDate = date;
        this.mContent = entry;
    }

    public JournalEntry(int id, String date, String entry) {
        this.mID = id;
        this.mDate = date;
        this.mContent = entry;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        this.mDate = date;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    @Override
    public String toString() {
        return "JournalEntryItem: " +
                "mDate='" + mDate + '\'' +
                " - mContent='" + mContent + '\'';
    }

    public int getID() throws NotInDatabaseException {
        if (mID == -1) {
            throw new NotInDatabaseException("Entry is not connected to database");
        }
        return mID;
    }

    public void setID(int id) {
        this.mID = id;
        if (id < 0) {
            throw new IllegalArgumentException();
        }
    }
}
