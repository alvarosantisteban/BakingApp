package com.alvarosantisteban.bakingapp;

import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.alvarosantisteban.bakingapp.model.Recipe;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
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
public class RecipeStepDetailFragment extends Fragment implements View.OnClickListener {

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
    private FrameLayout frameLayout;
    private SimpleExoPlayer exoPlayer;
    private SimpleExoPlayerView playerView;
    private ImageView placeholderImageView;
    private RecyclerView ingredientsRv;
    private long lastSavedVideoPos;

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

            String title = isRecipeStep(selectedStepPos) ?
                    selectedRecipe.getSteps().get(selectedStepPos).getShortDescription() :
                    getString(R.string.recipe_ingredient_title);
            getActivity().setTitle(title);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        boolean isRecipeStep = isRecipeStep(selectedStepPos);

        View rootView = inflater.inflate(R.layout.recipestep_detail, container, false);

        String description = isRecipeStep ?
                selectedRecipe.getSteps().get(selectedStepPos).getDescription() :
                "";
        descriptionTextView = (TextView) rootView.findViewById(R.id.recipe_step_description);
        descriptionTextView.setText(description);
        descriptionTextView.setVisibility(isRecipeStep ? View.VISIBLE : View.GONE);

        previous = (ImageView) rootView.findViewById(R.id.recipe_step_previous_button);
        next = (ImageView) rootView.findViewById(R.id.recipe_step_next_button);
        previous.setOnClickListener(this);
        next.setOnClickListener(this);

        playerView = (SimpleExoPlayerView) inflater.inflate(R.layout.recipestep_video, container, false);
        placeholderImageView = (ImageView) inflater.inflate(R.layout.recipestep_image, container, false);
        ingredientsRv = (RecyclerView) inflater.inflate(R.layout.recipestep_ingredients_rv, container, false);
        ingredientsRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                ingredientsRv.getContext(), DividerItemDecoration.VERTICAL);
        ingredientsRv.addItemDecoration(dividerItemDecoration);

        if (isTwoPane) {
            // Hide the navigation arrows, they are not needed
            previous.setVisibility(View.GONE);
            next.setVisibility(View.GONE);
        } else {
            maybeChangeNavigationArrows(selectedStepPos);
        }

        frameLayout = (FrameLayout) rootView.findViewById(R.id.recipe_step_upper_area);

        if(selectedRecipe != null && isRecipeStep && selectedRecipe.getSteps().get(selectedStepPos) != null) {
            // Get the video position from shared preferences
            lastSavedVideoPos = VideoPositionSharedPreferences.getVideoPosition(PreferenceManager.getDefaultSharedPreferences(getActivity()), selectedRecipe.getSteps().get(selectedStepPos).getId());
        }

        exchangeUpperPart(selectedStepPos);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(selectedRecipe != null && isRecipeStep(selectedStepPos) && selectedRecipe.getSteps().get(selectedStepPos) != null) {
            // Get the video position from shared preferences
            lastSavedVideoPos = VideoPositionSharedPreferences.getVideoPosition(PreferenceManager.getDefaultSharedPreferences(getActivity()), selectedRecipe.getSteps().get(selectedStepPos).getId());
        }
        if (Util.SDK_INT > 23) {
            exchangeUpperPart(selectedStepPos);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if ((Util.SDK_INT <= 23)) {
            exchangeUpperPart(selectedStepPos);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Save the video position in shared preferences
        if(exoPlayer != null && selectedStepPos >= 0) {
            VideoPositionSharedPreferences.putVideoPosition(PreferenceManager.getDefaultSharedPreferences(getActivity()), selectedRecipe.getSteps().get(selectedStepPos).getId(), exoPlayer.getCurrentPosition());
        }
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
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

            // Prepare the MediaSource.
            prepareMediaSource(mediaUri);
        } else {
            exoPlayer.setPlayWhenReady(true);
        }
    }

    private void prepareMediaSource(Uri mediaUri) {
        String userAgent = Util.getUserAgent(getActivity(), RECIPE_USER_AGENT);
        MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                getActivity(), userAgent), new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
        exoPlayer.setPlayWhenReady(true);
        if(lastSavedVideoPos > 0) {
            exoPlayer.seekTo(lastSavedVideoPos);
        }
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
    
    ////////////////////////////
    // UI
    ////////////////////////////

    /**
     * Exchange the layout used in the upper part of the window, so a recyclerview with ingredients,
     * a video player or an image is displayed.
     *
     * @param pos the position of the step of the current recipe.
     */
    private void exchangeUpperPart(int pos) {
        frameLayout.removeAllViews();
        if(isRecipeStep(pos)) {
            if (!TextUtils.isEmpty(selectedRecipe.getSteps().get(pos).getVideoUrl())) {
                // Add the player view
                frameLayout.addView(playerView);

                // Initialize the player.
                initializePlayer(Uri.parse(selectedRecipe.getSteps().get(pos).getVideoUrl()));
            } else if (!TextUtils.isEmpty(selectedRecipe.getSteps().get(pos).getImageUrl())) {
                // Load the image
                Glide.with(getActivity())
                        .load(selectedRecipe.getSteps().get(pos).getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.mortar_placeholder)
                        .into(placeholderImageView);
                // Add the image view
                frameLayout.addView(placeholderImageView);
            } else {
                // Add the image view which will contain the default placeholder
                frameLayout.addView(placeholderImageView);
            }
        }else {
            // Display the ingredients in a recyclerview
            frameLayout.addView(ingredientsRv);
            ingredientsRv.setAdapter(new IngredientsGridAdapter(selectedRecipe.getIngredients(), getActivity()));
        }
    }

    /**
     * Updates the current fragment so the right elements are displayed for the position passed by
     * parameter.
     *
     * @param newPosition the position of the step of the current recipe.
     */
    private void updateFragmentForPos(int newPosition) {
        if(exoPlayer != null) {
            exoPlayer.stop();
        }
        // Replace the upper part
        exchangeUpperPart(newPosition);
        if(isRecipeStep(newPosition) && newPosition < selectedRecipe.getSteps().size()) {
            // Replace title and description
            getActivity().setTitle(selectedRecipe.getSteps().get(newPosition).getShortDescription());
            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionTextView.setText(selectedRecipe.getSteps().get(newPosition).getDescription());

            // Replace video
            Uri mediaUri = Uri.parse(selectedRecipe.getSteps().get(newPosition).getVideoUrl());
            prepareMediaSource(mediaUri);


        } else {
            descriptionTextView.setVisibility(View.GONE);
            getActivity().setTitle(getString(R.string.recipe_ingredient_title));
        }
        maybeChangeNavigationArrows(newPosition);

        selectedStepPos = newPosition;
        getArguments().putInt(ARG_RECIPE_STEP_POS, selectedStepPos);
    }

    /**
     * Checks if for the position passed by parameter is it needed to activate/deactivate the
     * navigation arrows displayed at the bottom for non two pane mode.
     *
     * @param newPosition the position of the step of the current recipe.
     */
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
        // Reset the video position
        VideoPositionSharedPreferences.resetVideoPosition(PreferenceManager.getDefaultSharedPreferences(getActivity()));
        lastSavedVideoPos = VideoPositionSharedPreferences.INVALID_LAST_VIDEO_POS;

        // Update the fragment
        switch(view.getId()) {
            case recipe_step_previous_button:
                updateFragmentForPos(selectedStepPos-1);
                break;
            case recipe_step_next_button:
                updateFragmentForPos(selectedStepPos+1);
                break;
        }
    }

    /**
     * Check if the position passed by parameter belongs to a recipe step.
     * @param position the position of the step of the current recipe.
     * @return true if the position is for a recipe step, false if it is for displaying the ingredients
     */
    private boolean isRecipeStep(int position){
        return position > RecipeStepsAdapter.NO_STEP_SELECTED_POS;
    }
}
