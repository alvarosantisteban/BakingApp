package com.alvarosantisteban.bakingapp;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarosantisteban.bakingapp.model.Ingredient;
import com.alvarosantisteban.bakingapp.model.Recipe;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static com.alvarosantisteban.bakingapp.R.id.recipe_step_next_button;
import static com.alvarosantisteban.bakingapp.R.id.recipe_step_previous_button;

/**
 * A fragment representing a single RecipeStep detail screen.
 * This fragment is either contained in a {@link RecipeStepListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeStepDetailActivity}
 * on handsets.
 */
public class RecipeStepDetailFragment extends Fragment implements View.OnClickListener, ExoPlayer.EventListener {

    private static final String TAG = RecipeStepDetailFragment.class.getSimpleName();

    public static final String ARG_RECIPE = "recipe";
    public static final String ARG_RECIPE_STEP_POS = "recipeStepPOS";
    public static final String ARG_IS_TWO_PANE = "isTwoPane";
    static final String RECIPE_USER_AGENT = "RecipeUserAgent";

    private int selectedStepPos;
    private Recipe selectedRecipe;
    private boolean isTwoPane;

    private ImageView previous;
    private ImageView next;
    private TextView descriptionTextView;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView playerView;
    private ImageView placeholderImageView;
    private FrameLayout frameLayout;

    public RecipeStepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_RECIPE) &&
                getArguments().containsKey(ARG_RECIPE_STEP_POS) &&
                getArguments().containsKey(ARG_IS_TWO_PANE)) {

            selectedRecipe = getArguments().getParcelable(ARG_RECIPE);
            selectedStepPos = getArguments().getInt(ARG_RECIPE_STEP_POS);
            isTwoPane = getArguments().getBoolean(ARG_IS_TWO_PANE);

            String title = selectedStepPos > RecipeStepsAdapter.NO_STEP_SELECTED_POS ?
                    selectedRecipe.getSteps().get(selectedStepPos).getShortDescription() :
                    getString(R.string.recipe_ingredient_title);
            getActivity().setTitle(title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);
        String description = selectedStepPos > RecipeStepsAdapter.NO_STEP_SELECTED_POS ?
                selectedRecipe.getSteps().get(selectedStepPos).getDescription() :
                formatAllIngredients();
        descriptionTextView = (TextView) rootView.findViewById(R.id.recipe_step_description);
        descriptionTextView.setText(description);

        previous = (ImageView) rootView.findViewById(R.id.recipe_step_previous_button);
        next = (ImageView) rootView.findViewById(R.id.recipe_step_next_button);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        playerView = (SimpleExoPlayerView) inflater.inflate(R.layout.recipestep_video, container, false);
        placeholderImageView = (ImageView) inflater.inflate(R.layout.recipestep_image, container, false);

        if(isTwoPane) {
            // Hide the navigation arrows, they are not needed
            previous.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        } else {
            maybeChangeNavigationArrows(selectedStepPos);
        }

        frameLayout = (FrameLayout) rootView.findViewById(R.id.recipe_step_upper_area);
        if(selectedStepPos > RecipeStepsAdapter.NO_STEP_SELECTED_POS) {
            exchangeUpperPart(selectedStepPos);
        }
        return rootView;
    }

    private String formatAllIngredients() {
        String ingredientsString = "";
        for (Ingredient ingredient :selectedRecipe.getIngredients()) {
            ingredientsString = ingredientsString +Utils.formatFloatToString(ingredient.getQuantity()) + " (" +ingredient.getMeasure() +") " +ingredient.getIngredient() +"\n";
        }
        return ingredientsString;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    ////////////////////////////
    // PLAYER
    ////////////////////////////

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    private void initializePlayer(Uri mediaUri) {
        if (exoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            exoPlayer = ExoPlayerFactory.newSimpleInstance(getActivity(), trackSelector, loadControl);
            playerView.setPlayer(exoPlayer);

            // Set the ExoPlayer.EventListener to this activity.
            exoPlayer.addListener(this);

            // Prepare the MediaSource.
            prepareMediaSource(mediaUri);
        }
    }

    private void prepareMediaSource(Uri mediaUri) {
        String userAgent = Util.getUserAgent(getActivity(), RECIPE_USER_AGENT);
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(exoPlayer != null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
        // Do nothing
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
        // Do nothing
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
        // Do nothing
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        // TODO
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.e(TAG, error.toString());
    }

    @Override
    public void onPositionDiscontinuity() {
        // Do nothing
    }
    
    ////////////////////////////
    // UI
    ////////////////////////////

    private void exchangeUpperPart(int pos) {
        frameLayout.removeAllViews();
        if (!TextUtils.isEmpty(selectedRecipe.getSteps().get(pos).getVideoUrl())) {
            // Add the player view
            frameLayout.addView(playerView);

            // Initialize the player.
            initializePlayer(Uri.parse(selectedRecipe.getSteps().get(pos).getVideoUrl()));
        } else if(!TextUtils.isEmpty(selectedRecipe.getSteps().get(pos).getImageUrl())) {
            // Add the image view
            frameLayout.addView(placeholderImageView);
        }
    }

    private void updateFragmentForPos(int newPosition) {
        if(newPosition > RecipeStepsAdapter.NO_STEP_SELECTED_POS && newPosition < selectedRecipe.getSteps().size()) {
            // Replace the upper part
            exchangeUpperPart(newPosition);

            // Replace video
            Uri mediaUri = Uri.parse(selectedRecipe.getSteps().get(newPosition).getVideoUrl());
            prepareMediaSource(mediaUri);

            // Replace title and description
            getActivity().setTitle(selectedRecipe.getSteps().get(newPosition).getShortDescription());
            descriptionTextView.setText(selectedRecipe.getSteps().get(newPosition).getDescription());
        } else {
            frameLayout.removeAllViews();

            getActivity().setTitle(getString(R.string.recipe_ingredient_title));
            descriptionTextView.setText(formatAllIngredients());
        }
        maybeChangeNavigationArrows(newPosition);

        selectedStepPos = newPosition;
    }

    private void maybeChangeNavigationArrows(int newPosition) {
        if(!isTwoPane) {
            if (newPosition == RecipeStepsAdapter.NO_STEP_SELECTED_POS) {
                // Grey the Previous button
                next.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                previous.setColorFilter(ContextCompat.getColor(getActivity(), R.color.greyLight), PorterDuff.Mode.SRC_IN);

                next.setClickable(true);
                previous.setClickable(false);
            } else if (newPosition == selectedRecipe.getSteps().size() - 1) {
                // Grey the Next button
                next.setColorFilter(ContextCompat.getColor(getActivity(), R.color.greyPink), android.graphics.PorterDuff.Mode.SRC_IN);
                previous.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

                next.setClickable(false);
                previous.setClickable(true);
            } else {
                // Ungrey both
                next.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);
                previous.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black), android.graphics.PorterDuff.Mode.SRC_IN);

                next.setClickable(true);
                previous.setClickable(true);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(exoPlayer != null) {
            exoPlayer.stop();
        }
        switch(view.getId()) {
            case recipe_step_previous_button:
                updateFragmentForPos(selectedStepPos-1);
                break;
            case recipe_step_next_button:
                updateFragmentForPos(selectedStepPos+1);
                break;
        }
    }
}
