package org.drulabs.petescafe.ui.details;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import org.drulabs.petescafe.data.RecipeRepository;
import org.drulabs.petescafe.data.model.Recipe;
import org.drulabs.petescafe.data.model.RecipeStep;

public class DetailVM extends ViewModel {

    private final LiveData<Recipe> recipe;

    private final MutableLiveData<RecipeStep> currentStep;
    private final MutableLiveData<Integer> stepStateHolder;

    private long playbackPosition = 0;
    private boolean playing = false;

    DetailVM(RecipeRepository repository, int recipeId) {
        recipe = repository.getRecipe(recipeId);
        currentStep = new MutableLiveData<>();
        stepStateHolder = new MutableLiveData<>();
    }

    LiveData<Recipe> getRecipe() {
        return recipe;
    }

    LiveData<RecipeStep> getCurrentStep() {
        return currentStep;
    }

    LiveData<Integer> getStepStateHolder() {
        return stepStateHolder;
    }

    void setCurrentStep(RecipeStep step) {
        this.currentStep.postValue(step);
    }

    void nextStep(int currentStepId) {
        this.stepStateHolder.postValue(++currentStepId);
        playbackPosition = 0;
    }

    void previousStep(int currentStepId) {
        this.stepStateHolder.postValue(--currentStepId);
        playbackPosition = 0;
    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }

    public void setPlaybackPosition(long playbackPosition) {
        this.playbackPosition = playbackPosition;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isPlaying() {
        return playing;
    }
}
