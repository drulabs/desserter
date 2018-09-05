package org.drulabs.petescafe.di;

import android.support.v4.app.Fragment;

import org.drulabs.petescafe.ui.details.DetailVMFactory;
import org.drulabs.petescafe.ui.home.HomeVMFactory;

import dagger.Component;

@ActivityScope
@Component(dependencies = {AppComponent.class})
public interface ViewComponent {

    HomeVMFactory getHomeVMFactory();

}
