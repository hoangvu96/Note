package com.example.note.interactor;

import com.example.note.model.ImagePath;
import com.example.note.model.Note;

import java.util.List;

public interface EditNotefragmentInteractor extends BaseInteractor {
    void editNote(Note note);
    List<ImagePath> showImage();
    String[] spinerDate();
    String[] spinerTime();
    Note returnNote(int id);
    void deleteNote(int id);
    Note left(int id);
    Note right(int id);
    boolean isFirt(int id);
    boolean isLast(int id);
}