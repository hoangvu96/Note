package com.example.note.injection;

import com.example.note.view.impl.ListNoteFragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = ListNoteViewModule.class)
public interface ListNoteViewComponent {
    void inject(ListNoteFragment fragment);
}