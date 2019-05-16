package com.example.note.presenter;

import android.net.Uri;

import com.example.note.model.Note;
import com.example.note.view.AddNotefragmentView;

public interface AddNotefragmentPresenter extends BasePresenter<AddNotefragmentView> {
    void onClickCamera();
    void onClickColor();
    void onClickDone(Note note);
    void showImage();
    void showSpinerDate();
    void showSpinerTime();
}