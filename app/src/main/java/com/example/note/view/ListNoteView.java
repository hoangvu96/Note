package com.example.note.view;

import android.support.annotation.UiThread;

import com.example.note.model.Note;

import java.util.List;

@UiThread
public interface ListNoteView {
    void onShow(List<Note> noteList);
//    void onError(String message);
    void showAddNoteScreen();
}