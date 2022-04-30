package com.openclassrooms.realestatemanager;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.openclassrooms.realestatemanager.ui.main.MainActivity;
import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class RealEstateManagerInstrumentedTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = new ActivityScenarioRule<>(MainActivity.class);

    Context appContext = getInstrumentation().getTargetContext();

    @Test
    public void useAppContext() {
        assertEquals("com.openclassrooms.realestatemanager", appContext.getPackageName());
    }

    @Test
    public void checkConnectivity() {
        assertEquals(true, Utils.isInternetAvailable(appContext));
    }

    @Test
    public void checkThatListIsInitiallyDisplayed() {
        onView(withId(R.id.listFragment)).check(matches(isDisplayed()));
        onView(withId(R.id.estate_list)).check(matches(isDisplayed()));
    }

    @Test
    public void checkThatMapFragmentIsDisplayed() {
        onView(withId(R.id.mapFragment)).perform(click());
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void checkThatSearchFragmentIsDisplayed() {
        onView(withId(R.id.searchFragment)).perform(click());
        onView(withId(R.id.search_fab)).check(matches(isDisplayed()));
    }

    @Test
    public void checkThatSimFragmentIsDisplayed() {
        onView(withId(R.id.simFragment)).perform(click());
        onView(withId(R.id.sim_loan_amount)).check(matches(isDisplayed()));
    }

    @Test
    public void checkThatAddFragmentIsDisplayed() {
        onView(withId(R.id.toolbar_add)).perform(click());
        onView(withId(R.id.add_fab)).check(matches(isDisplayed()));
    }

    @Test
    public void checkThatDetailFragmentIsDisplayed() {
        onView(allOf(withId(R.id.estate_item), isDisplayed())).perform(click());
        onView(withId(R.id.detail_fab)).check(matches(isDisplayed()));
    }
}
