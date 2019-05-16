package com.example.note.interactor.impl;

import javax.inject.Inject;

import com.example.note.interactor.AddNotefragmentInteractor;
import com.example.note.model.ImagePath;
import com.example.note.model.Note;
import com.example.note.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
                Number currentIdNum = realm.where(Note.class).max(Constant.ID);
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }
                if (note.getDate().equals(Constant.TODAY)){
                    note.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(new Date()));
                }else if (note.getDate().equals(Constant.TOMORROW)){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE,1);
                    note.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(calendar.getTime()));
                }else if (note.getDate().contains(Constant.NEXT)){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DATE,7);
                    note.setDate(new SimpleDateFormat(Constant.DD_MM_YYYY).format(calendar.getTime()));
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
}