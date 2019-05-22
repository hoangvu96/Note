package com.example.note.injection;

import android.support.annotation.NonNull;

import com.example.note.interactor.AddNotefragmentInteractor;
import com.example.note.interactor.impl.AddNotefragmentInteractorImpl;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.AddNotefragmentPresenter;
import com.example.note.presenter.impl.AddNotefragmentPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class AddNotefragmentViewModule {
    @Provides
    public AddNotefragmentInteractor provideInteractor() {
        return new AddNotefragmentInteractorImpl();
    }

    @Provides
    public PresenterFactory<AddNotefragmentPresenter> providePresenterFactory(@NonNull final AddNotefragmentInteractor interactor) {
        return new PresenterFactory<AddNotefragmentPresenter>() {
            @NonNull
            @Override
            public AddNotefragmentPresenter create() {
                return new AddNotefragmentPresenterImpl(interactor);
            }
        };
    }
}
