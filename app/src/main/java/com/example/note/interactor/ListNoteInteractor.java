package com.example.note.interactor;

import com.example.note.model.Note;

import java.util.List;

public interface ListNoteInteractor extends BaseInteractor {
    List<Note> getNotes();
}