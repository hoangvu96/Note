package com.example.note.presenter;

import com.example.note.model.Note;
import com.example.note.view.EditNotefragmentView;

public interface EditNotefragmentPresenter extends BasePresenter<EditNotefragmentView> {
    void onClickCamera();

    void onClickColor();

    void onClickDone(Note note);

    void showImage();

    void showSpinerDate();

    void showSpinerTime();

    void initData(int id);

    void deleteNote(int id);

    void share(int id);

    void left(int id);

    void right(int id);

    void checkIsLast(int id);

    void checkIsFirt(int id);

    void addNewNote();
}