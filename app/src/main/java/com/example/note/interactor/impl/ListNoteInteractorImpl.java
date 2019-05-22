package com.example.note.interactor.impl;

import javax.inject.Inject;

import com.example.note.interactor.ListNoteInteractor;
import com.example.note.model.Note;
import com.example.note.presenter.impl.ListNotePresenterImpl;

import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public final class ListNoteInteractorImpl implements ListNoteInteractor {
    @Inject
    public ListNoteInteractorImpl() {
    }

    @Override
    public List<Note> getNotes() {
        Realm realm = Realm.getDefaultInstance();
        List<Note> notes = realm.where(Note.class).findAll().sort("id", Sort.ASCENDING);
        return notes;
    }
}