package com.example.note.injection;

import com.example.note.view.impl.EditNotefragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = EditNotefragmentViewModule.class)
public interface EditNotefragmentViewComponent {
    void inject(EditNotefragment fragment);
}