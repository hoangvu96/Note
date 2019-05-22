package com.example.note.injection;

import android.support.annotation.NonNull;

import com.example.note.interactor.EditNotefragmentInteractor;
import com.example.note.interactor.impl.EditNotefragmentInteractorImpl;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.EditNotefragmentPresenter;
import com.example.note.presenter.impl.EditNotefragmentPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class EditNotefragmentViewModule {
    @Provides
    public EditNotefragmentInteractor provideInteractor() {
        return new EditNotefragmentInteractorImpl();
    }

    @Provides
    public PresenterFactory<EditNotefragmentPresenter> providePresenterFactory(@NonNull final EditNotefragmentInteractor interactor) {
        return new PresenterFactory<EditNotefragmentPresenter>() {
            @NonNull
            @Override
            public EditNotefragmentPresenter create() {
                return new EditNotefragmentPresenterImpl(interactor);
            }
        };
    }
}
