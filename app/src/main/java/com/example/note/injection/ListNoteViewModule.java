package com.example.note.injection;

import android.support.annotation.NonNull;

import com.example.note.interactor.ListNoteInteractor;
import com.example.note.interactor.impl.ListNoteInteractorImpl;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.ListNotePresenter;
import com.example.note.presenter.impl.ListNotePresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class ListNoteViewModule {
    @Provides
    public ListNoteInteractor provideInteractor() {
        return new ListNoteInteractorImpl();
    }

    @Provides
    public PresenterFactory<ListNotePresenter> providePresenterFactory(@NonNull final ListNoteInteractor interactor) {
        return new PresenterFactory<ListNotePresenter>() {
            @NonNull
            @Override
            public ListNotePresenter create() {
                return new ListNotePresenterImpl(interactor);
            }
        };
    }
}
