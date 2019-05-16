package com.example.note.presenter.impl;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.example.note.model.ImagePath;
import com.example.note.model.Note;
import com.example.note.presenter.AddNotefragmentPresenter;
import com.example.note.view.AddNotefragmentView;
import com.example.note.interactor.AddNotefragmentInteractor;

import java.util.List;

import javax.inject.Inject;

public final class AddNotefragmentPresenterImpl extends BasePresenterImpl<AddNotefragmentView> implements AddNotefragmentPresenter {
    /**
     * The interactor
     */
    @NonNull
    private final AddNotefragmentInteractor mInteractor;

    // The view is available using the mView variable

    @Inject
    public AddNotefragmentPresenterImpl(@NonNull AddNotefragmentInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean viewCreated) {
        super.onStart(viewCreated);
        if (viewCreated){
            showImage();
            showSpinerDate();
            showSpinerTime();
        }

        // Your code here. Your view is available using mView and will not be null until next onStop()
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
    public void onClickCamera() {
        if (mView!=null){
            mView.showDialogCamera();
        }
    }

    @Override
    public void onClickColor() {
        if (mView!=null){
            mView.showDialogColor();
        }
    }

    @Override
    public void onClickDone(Note note) {
        mInteractor.addNote(note);
        if (mView!=null){
            mView.onDone();
        }
    }

    @Override
    public void showImage() {
       List<ImagePath> imagePaths = mInteractor.showImage();
        if (mView!=null){
            mView.showImage(imagePaths);
        }
    }

    @Override
    public void showSpinerDate() {
        String[] str = mInteractor.spinerDate();
        if (mView!=null){
            mView.showSpinerDate(str);
        }
    }

    @Override
    public void showSpinerTime() {
        String[] str = mInteractor.spinerTime();
        if (mView!=null){
            mView.showSpinerTime(str);
        }
    }

}