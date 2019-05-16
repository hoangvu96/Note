package com.example.note.view;

import android.support.annotation.UiThread;

import com.example.note.model.ImagePath;
import com.example.note.model.Note;

import java.util.List;

@UiThread
public interface EditNotefragmentView {
    void showDialogCamera();

    void showDialogColor();

    void showSpinerDate(String[] strDate);

    void showSpinerTime(String[] strTime);

    void onDone();

    void showImage(List<ImagePath> imagePaths);

    void initData(Note note);

    void deleteNote();

    void share();

    void left(Note note);

    void right(Note note);

    void showLeft();

    void showRight();

    void hideLeft();

    void hideRight();
}