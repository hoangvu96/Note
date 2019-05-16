package com.example.note.injection;

import android.support.annotation.NonNull;

import com.example.note.interactor.MainInteractor;
import com.example.note.interactor.impl.MainInteractorImpl;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.MainPresenter;
import com.example.note.presenter.impl.MainPresenterImpl;

import dagger.Module;
import dagger.Provides;

@Module
public final class MainViewModule {
    @Provides
    public MainInteractor provideInteractor() {
        return new MainInteractorImpl();
    }

    @Provides
    public PresenterFactory<MainPresenter> providePresenterFactory(@NonNull final MainInteractor interactor) {
        return new PresenterFactory<MainPresenter>() {
            @NonNull
            @Override
            public MainPresenter create() {
                return new MainPresenterImpl(interactor);
            }
        };
    }
}
