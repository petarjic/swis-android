package com.swis.android.listeners;


import com.swis.android.util.NisinFile;

import java.util.List;

/**
 * Created by ZIploan-Nitesh on 9/4/2017.
 */

public interface LoadFileListener {
    public void getFiles(List<NisinFile> arrImages, int sourceType);
    public void processingImages();
}