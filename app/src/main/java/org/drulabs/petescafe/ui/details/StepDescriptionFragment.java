package org.drulabs.petescafe.ui.details;


import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import org.drulabs.petescafe.R;
import org.drulabs.petescafe.data.model.RecipeStep;
import org.drulabs.petescafe.di.FragmentDetailsScope;

import java.util.Locale;

@FragmentDetailsScope
public class StepDescriptionFragment extends Fragment {

    private static final String TAG = "StepDesc";
    private static final String KEY_CURRENT_STEP_ID = "current_step";
    private static final String KEY_PLAYBACK_POSITION = "playback_position";
    private static final String KEY_IS_PLAYING = "is_playing";

    // class vars
    int totalSteps = 0;
    private int currentStepId;
    DetailVM detailVM;

    private ExoPlayer player;

    // UI elements
    private ImageView imgPreview;
    private ImageView imgPrevStep, imgNextStep;
    private TextView tvStepDesc;
    private TextView tvStepPosition;
    private PlayerView playerView;

    public StepDescriptionFragment() {
        // Required empty public constructor
    }

    public static StepDescriptionFragment newInstance(int currentStepId) {
        StepDescriptionFragment fragment = new StepDescriptionFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_CURRENT_STEP_ID, currentStepId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();

        if (args != null) {
            currentStepId = args.getInt(KEY_CURRENT_STEP_ID);
        }

        detailVM = ViewModelProviders.of(getActivity()).get(DetailVM.class);

        detailVM.getRecipe().observe(this, recipe -> {
            if (recipe != null) {
                totalSteps = recipe.getSteps().size();
            }
        });

        detailVM.getCurrentStep().observe(this, recipeStep -> {
            if (recipeStep != null) {

                currentStepId = recipeStep.getId();

                tvStepPosition.setText(String.format(Locale.getDefault(), "Step %d of %d",
                        currentStepId + 1, totalSteps));
                tvStepDesc.setText(recipeStep.getDescription());

                releasePlayer();

                if (!recipeStep.getVideoURL().isEmpty() || !recipeStep.getThumbnailURL().isEmpty()) {
                    // Initialize the player
                    initializePlayer(recipeStep);
                    imgPreview.setVisibility(View.GONE);
                } else {
                    imgPreview.setVisibility(View.VISIBLE);
                }

                if (currentStepId == 0 && currentStepId < totalSteps) {
                    imgPrevStep.setVisibility(View.GONE);
                    imgNextStep.setVisibility(View.VISIBLE);
                } else if (currentStepId == totalSteps - 1) {
                    imgPrevStep.setVisibility(View.VISIBLE);
                    imgNextStep.setVisibility(View.GONE);
                } else {
                    imgPrevStep.setVisibility(View.VISIBLE);
                    imgNextStep.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_step_description, container, false);
        playerView = view.findViewById(R.id.exo_player_view);
        tvStepDesc = view.findViewById(R.id.tv_stepdec_description);
        tvStepPosition = view.findViewById(R.id.tv_step_position);
        imgPrevStep = view.findViewById(R.id.img_prev_step);
        imgNextStep = view.findViewById(R.id.img_next_step);
        imgPreview = view.findViewById(R.id.img_stepdesc_media_preview);

        imgPrevStep.setOnClickListener(v -> detailVM.previousStep(currentStepId));
        imgNextStep.setOnClickListener(v -> detailVM.nextStep(currentStepId));

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (player != null) {
            detailVM.setPlaybackPosition(player.getCurrentPosition());
            boolean isPlaying = (player.getPlaybackState() == PlaybackStateCompat.STATE_BUFFERING) ||
                    (player.getPlaybackState() == PlaybackStateCompat.STATE_PLAYING);
            detailVM.setPlaying(isPlaying);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void initializePlayer(RecipeStep step) {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(
                    new DefaultRenderersFactory(getActivity()),
                    new DefaultTrackSelector(),
                    new DefaultLoadControl());
            playerView.setPlayer(player);
            player.setPlayWhenReady(detailVM.isPlaying());
        }

        String mediaUrl = step.getVideoURL();
        if (mediaUrl.isEmpty()) {
            mediaUrl = step.getThumbnailURL();
        }

        MediaSource mediaSource = buildMediaSource(Uri.parse(mediaUrl));
        if (mediaSource != null) {
            player.seekTo(detailVM.getPlaybackPosition());
            player.prepare(mediaSource, false, false);
        }
    }

    @Nullable
    private MediaSource buildMediaSource(Uri uri) {

        String userAgent = System.getProperty("http.agent");

        if (uri.getLastPathSegment().contains("mp4") /* Other supported format checks*/) {
            return new ExtractorMediaSource.Factory(new DefaultHttpDataSourceFactory(userAgent))
                    .createMediaSource(uri);
        } else {
            return null;
        }
    }

    void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
