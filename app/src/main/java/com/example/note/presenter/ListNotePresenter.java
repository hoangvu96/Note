package com.example.note.presenter;

import com.example.note.view.ListNoteView;

public interface ListNotePresenter extends BasePresenter<ListNoteView> {
    void getNotes();

    void actionAddNoteClick();

}