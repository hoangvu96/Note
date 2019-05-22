package com.example.note.injection;

import com.example.note.view.impl.AddNotefragment;

import dagger.Component;

@FragmentScope
@Component(dependencies = AppComponent.class, modules = AddNotefragmentViewModule.class)
public interface AddNotefragmentViewComponent {
    void inject(AddNotefragment fragment);
}