package com.example.note.presenter.impl;

import android.support.annotation.NonNull;

import com.example.note.model.ImagePath;
import com.example.note.model.Note;
import com.example.note.presenter.EditNotefragmentPresenter;
import com.example.note.view.EditNotefragmentView;
import com.example.note.interactor.EditNotefragmentInteractor;

import java.util.List;

import javax.inject.Inject;

public final class EditNotefragmentPresenterImpl extends BasePresenterImpl<EditNotefragmentView> implements EditNotefragmentPresenter {
    /**
     * The interactor
     */
    @NonNull
    private final EditNotefragmentInteractor mInteractor;

    // The view is available using the mView variable

    @Inject
    public EditNotefragmentPresenterImpl(@NonNull EditNotefragmentInteractor interactor) {
        mInteractor = interactor;
    }

    @Override
    public void onStart(boolean viewCreated) {
        super.onStart(viewCreated);

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
        mInteractor.editNote(note);
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

    @Override
    public void initData(int id) {
        Note note = mInteractor.returnNote(id);
        if (mView!=null){
            mView.initData(note);
        }
    }

    @Override
    public void deleteNote(int id) {
        mInteractor.deleteNote(id);
        if (mView!=null){
            mView.deleteNote();
        }
    }

    @Override
    public void share() {
        if (mView!=null){
            mView.share();
        }
    }

    @Override
    public void left(int id) {
       Note note =  mInteractor.left(id);
        if (mView!=null){
            mView.left(note);
        }
    }

    @Override
    public void right(int id) {
        Note note =  mInteractor.right(id);
        if (mView!=null){
            mView.right(note);
        }
    }

    @Override
    public void checkIsLast(int id) {
        boolean check = mInteractor.isLast(id);
        if (mView!=null){
            if (check){
                mView.hideRight();
            }else {
                mView.showRight();
            }
        }
    }

    @Override
    public void checkIsFirt(int id) {
        boolean check = mInteractor.isFirt(id);
        if (mView!=null){
            if (check){
                mView.hideLeft();
            }else {
                mView.showLeft();
            }
        }
    }
}