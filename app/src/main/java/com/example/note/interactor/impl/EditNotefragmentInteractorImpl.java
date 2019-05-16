package com.example.note.interactor.impl;

import javax.inject.Inject;

import com.example.note.interactor.EditNotefragmentInteractor;
import com.example.note.model.ImagePath;
import com.example.note.model.Note;
import com.example.note.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public final class EditNotefragmentInteractorImpl implements EditNotefragmentInteractor {
    @Inject
    public EditNotefragmentInteractorImpl() {

    }

    @Override
    public void editNote(final Note note) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Note mNote = realm.where(Note.class).equalTo(Constant.ID, note.getId()).findFirst();
        mNote.setColor(note.getColor());
        mNote.setTitle(note.getTitle());
        mNote.setContent(note.getContent());
        mNote.setDateCreate(note.getDateCreate());
        mNote.setImages(note.getImages());
        mNote.setDate(note.getDate());
        mNote.setTimeAlarm(note.getTimeAlarm());
        mNote.setAlarm(note.isAlarm());
        realm.insertOrUpdate(mNote);
        realm.commitTransaction();
        realm.close();
    }

    @Override
    public List<ImagePath> showImage() {
        return new ArrayList<>();
    }

    @Override
    public String[] spinerDate() {
        SimpleDateFormat dayFormat = new SimpleDateFormat(Constant.EEEE, Locale.US);
        Calendar calendar = Calendar.getInstance();
        String weekDay = dayFormat.format(calendar.getTime());
        String nextWeek = Constant.NEXT + weekDay;
        String[] str = new String[]{Constant.TODAY,Constant.TOMORROW,nextWeek,Constant.OTHER};
        return str;
    }

    @Override
    public String[] spinerTime() {
        String[] str = new String[]{Constant.NINE,Constant.THIRTEEN,Constant.SEVENTEEN,Constant.TWENTY,Constant.OTHER};
        return str;
    }

    @Override
    public Note returnNote(int id) {
        Realm realm = Realm.getDefaultInstance();
        Note note = realm.where(Note.class).equalTo(Constant.ID, id).findFirst();
        return note;
    }

    @Override
    public void deleteNote(final int id) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(Note.class).equalTo(Constant.ID, id).findFirst().deleteFromRealm();
            }
        });
    }

    @Override
    public Note left(int id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Note> notes = realm.where(Note.class).findAll().sort(Constant.ID, Sort.ASCENDING);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id) {
                if (i > 0) {
                    return notes.get(i - 1);
                }
            }
        }
        realm.close();
        return realm.where(Note.class).equalTo(Constant.ID, id).findFirst();
    }

    @Override
    public Note right(int id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Note> notes = realm.where(Note.class).findAll().sort(Constant.ID, Sort.ASCENDING);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id && i < (notes.size() - 1)) {
                if (i < notes.size()) {
                    return notes.get(i + 1);
                }
            }
        }
        realm.close();
        return realm.where(Note.class).equalTo(Constant.ID, id).findFirst();
    }

    @Override
    public boolean isFirt(int id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Note> notes = realm.where(Note.class).findAll().sort(Constant.ID, Sort.ASCENDING);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id && i == 0) {
                return true;
            }
        }
        realm.close();
        return false;
    }

    @Override
    public boolean isLast(int id) {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Note> notes = realm.where(Note.class).findAll().sort(Constant.ID, Sort.ASCENDING);
        for (int i = 0; i < notes.size(); i++) {
            if (notes.get(i).getId() == id && i == notes.size()-1) {
                return true;
            }
        }
        realm.close();
        return false;
    }
}