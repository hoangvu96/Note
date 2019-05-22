package com.example.note.interactor;

import com.example.note.model.ImagePath;
import com.example.note.model.Note;

import java.util.List;

public interface AddNotefragmentInteractor extends BaseInteractor {
    void addNote(Note note);
    List<ImagePath> showImage();
    String[] spinerDate();
    String[] spinerTime();
}