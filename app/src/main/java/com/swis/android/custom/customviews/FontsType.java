package com.swis.android.custom.customviews;

public enum FontsType {

    ITALIC("fonts/FiraSans-BookItalic.otf"),
    GOTHAM_ROUNDED_BOOK("fonts/FiraSans-Book.otf"),
    GOTHAM_ROUNDED_BOOK_BOLD("fonts/FiraSans-Bold.otf"),
    GOTHAM_ROUNDED_MED("fonts/FiraSans-Medium.otf");


    private String path;

    private FontsType(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}