package org.drulabs.petescafe.di;

import org.drulabs.petescafe.ui.details.DetailVMFactory;

import dagger.Component;
import dagger.Subcomponent;

@Subcomponent(modules = {DetailsModule.class})
public interface DetailComponent {

    DetailVMFactory getDetailVMFactory();

}
