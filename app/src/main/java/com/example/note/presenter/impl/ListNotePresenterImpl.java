package com.example.note.presenter.impl;

import android.support.annotation.NonNull;

import com.example.note.model.Note;
import com.example.note.presenter.ListNotePresenter;
import com.example.note.view.ListNoteView;
import com.example.note.interactor.ListNoteInteractor;

import java.util.List;

import javax.inject.Inject;

public final class ListNotePresenterImpl extends BasePresenterImpl<ListNoteView> implements ListNotePresenter {
    /**
     * The interactor
     */
    @NonNull
    private final ListNoteInteractor mInteractor;

    // The view is available using the mView variable

    @Inject
    public ListNotePresenterImpl(@NonNull ListNoteInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean viewCreated) {
        super.onStart(viewCreated);

        // Your code here. Your view is available using mView and will not be null until next onStop()
        getNotes();
    }

    @Override
    public void onStop() {
        // Your code here, mView will be null after this method until next onStart()

        super.onStop();
    }

    @Override
    public void onPresenterDestroyed() {
        /*
         * Your code here. After this method, your presenter (and view) will be completely destroyed
         * so make sure to cancel any HTTP call or database connection
         */

        super.onPresenterDestroyed();
    }

    @Override
    public void getNotes() {
        List<Note> noteList = mInteractor.getNotes();
        if (mView != null) {
            mView.onShow(noteList);
        }
    }

    @Override
    public void actionAddNoteClick() {
        if (mView != null) {
            mView.showAddNoteScreen();
        }
    }
}