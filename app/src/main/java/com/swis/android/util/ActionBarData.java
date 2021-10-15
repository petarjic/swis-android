package com.swis.android.util;

public class ActionBarData {
    private String title;
    private int homeUpIcon;
    private int subsIcon;
    public ActionBarData(int icon, String title) {
        this.homeUpIcon = icon;
        this.title = title;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getHomeUpIcon() {
        return homeUpIcon;
    }

    public void setHomeUpIcon(int homeUpIcon) {
        this.homeUpIcon = homeUpIcon;
    }

    public int getSubsIcon() {
        return subsIcon;
    }

    public void setSubsIcon(int subsIcon) {
        this.subsIcon = subsIcon;
    }
}
