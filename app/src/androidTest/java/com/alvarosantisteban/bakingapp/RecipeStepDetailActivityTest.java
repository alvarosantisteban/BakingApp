package com.alvarosantisteban.bakingapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assume.assumeTrue;

/**
 * UI tests for testing the navigation buttons and the player.
 */
@RunWith(AndroidJUnit4.class)
public class RecipeStepDetailActivityTest {

    private static final int TOTAL_NUMBER_OF_STEPS_FOR_NUTELLA = 7;
    @Rule
    public ActivityTestRule<RecipesActivity> activityTestRule = new ActivityTestRule<>(RecipesActivity.class);
    private IdlingResource mIdlingResource;

    // Registers any resource that needs to be synchronized with Espresso before the test is run.
    @Before
    public void registerIdlingResource() {
        mIdlingResource = activityTestRule.getActivity().getIdlingResource();
        Espresso.registerIdlingResources(mIdlingResource);
    }

    @Test
    public void testNavigationButtonsAndPlayerPhone() {
        assumeTrue(!isTabletAndLandscape());

        // Click on first element (nutella pie)
        onView(withId(R.id.recipes_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click on first element (ingredients)
        onView(withId(R.id.recipestep_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that the previous button is not clickable
        onView(withId(R.id.recipe_step_previous_button)).check(matches(not(isClickable())));

        // Check that the next button is clickable
        onView(withId(R.id.recipe_step_next_button))
                .perform(scrollTo(), click());

        // Check that the video was displayed
        onView(withId(R.id.exo_pause)).check(matches(isDisplayed()));

        // Go to the last position and check that the "next button" is not clickable
        for (int i = 1; i< TOTAL_NUMBER_OF_STEPS_FOR_NUTELLA; i++) {
            onView(withId(R.id.recipe_step_next_button))
                    .perform(scrollTo(), click());
        }
        onView(withId(R.id.recipe_step_next_button)).check(matches(not(isClickable())));
    }

    @Test
    public void testNavigationButtonsAndPlayerTabletLandscape() {
        assumeTrue(isTabletAndLandscape());

        // Click on first element (nutella pie)
        onView(withId(R.id.recipes_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Click on first element (ingredients)
        onView(withId(R.id.recipestep_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        // Check that the navigation buttons are not displayed
        onView(withId(R.id.recipe_step_previous_button)).check(matches(not(isDisplayed())));
        onView(withId(R.id.recipe_step_next_button)).check(matches(not(isDisplayed())));

        // Click on second element
        onView(withId(R.id.recipestep_list))
                .perform(RecyclerViewActions.actionOnItemAtPosition(1, click()));

        // Check that the video was displayed
        onView(withId(R.id.exo_pause)).check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            Espresso.unregisterIdlingResources(mIdlingResource);
        }
    }

    private boolean isTabletAndLandscape() {
        Context context = InstrumentationRegistry.getTargetContext().getApplicationContext();
        return Utils.isTablet(context) && Utils.isLandscape(context);
    }
}
