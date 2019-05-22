package com.example.note.view;

import android.support.annotation.UiThread;

import com.example.note.model.ImagePath;
import com.example.note.model.Note;

import java.util.List;

@UiThread
public interface AddNotefragmentView {
    void showDialogCamera();

    void showDialogColor();

    void showSpinerDate(String[] strDate);

    void showSpinerTime(String[] strTime);

    void onDone();

    void showImage(List<ImagePath> imagePaths);

}