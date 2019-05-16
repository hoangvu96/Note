package com.example.note.view.impl;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.note.R;
import com.example.note.adapter.NoteAdapter;
import com.example.note.model.Note;
import com.example.note.view.ListNoteView;
import com.example.note.presenter.loader.PresenterFactory;
import com.example.note.presenter.ListNotePresenter;
import com.example.note.injection.AppComponent;
import com.example.note.injection.ListNoteViewModule;
import com.example.note.injection.DaggerListNoteViewComponent;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public final class ListNoteFragment extends BaseFragment<ListNotePresenter, ListNoteView> implements ListNoteView, NoteAdapter.OnNoteAdapter {
    @BindView(R.id.rcv)
    RecyclerView rcv;
    private NoteAdapter noteAdapter;

    @Inject
    PresenterFactory<ListNotePresenter> mPresenterFactory;

    // Your presenter is available using the mPresenter variable

    public ListNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_note, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        setHasOptionsMenu(true);
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        // Your code here
        // Do not call mPresenter from here, it will be null! Wait for onStart
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle("Note");
        }
    }

    @Override
    protected void setupComponent(@NonNull AppComponent parentComponent) {
        DaggerListNoteViewComponent.builder()
                .appComponent(parentComponent)
                .listNoteViewModule(new ListNoteViewModule())
                .build()
                .inject(this);
    }

    @NonNull
    @Override
    protected PresenterFactory<ListNotePresenter> getPresenterFactory() {
        return mPresenterFactory;
    }

    @Override
    public void onShow(List<Note> noteList) {
        rcv = getView().findViewById(R.id.rcv);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        rcv.setLayoutManager(gridLayoutManager);
        noteAdapter = new NoteAdapter(noteList, getContext(), this);
        rcv.setAdapter(noteAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_toolbar, menu);
        MenuItem add = menu.findItem(R.id.action_add);
        add.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return false;
            case R.id.action_add:
                if (mPresenter != null) {
                    mPresenter.actionAddNoteClick();
                }
                return false;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClickNote(int pos) {
        int id = noteAdapter.getNoteList().get(pos).getId();
        changeFragment("editnote", new EditNotefragment(id));
    }

    @Override
    public void showAddNoteScreen() {
        changeFragment("addnote",new AddNotefragment());
    }
}
