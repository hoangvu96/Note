package com.example.note.interactor.impl;

import javax.inject.Inject;

import com.example.note.interactor.AddNotefragmentInteractor;
import com.example.note.model.ImagePath;
import com.example.note.model.Note;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public final class AddNotefragmentInteractorImpl implements AddNotefragmentInteractor {
    @Inject
    public AddNotefragmentInteractorImpl() {

    }

    @Override
    public void addNote(final Note note) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(Note.class).max("id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                note.setId(nextId);
                realm.copyToRealm(note);
                realm.close();
            }
        });
    }

    @Override
    public List<ImagePath> showImage() {
        return new ArrayList<>();
    }

    @Override
    public String[] spinerDate() {
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());
        String nextWeek = "Next " + weekDay;
        String[] str = new String[]{"Today","Tomorrow",nextWeek,"Other..."};
        return str;
    }

    @Override
    public String[] spinerTime() {
        String[] str = new String[]{"09:00","13:00","17:00","20:00","Other..."};
        return str;
    }
}