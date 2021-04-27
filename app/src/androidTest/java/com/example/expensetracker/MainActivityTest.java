package com.example.expensetracker;

import androidx.test.core.app.ActivityScenario;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;


@RunWith(AndroidJUnit4ClassRunner.class)
public class MainActivityTest {
    @Test
    public void testActivityInView() {
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickToRegisterButton(){
        ActivityScenario.launch(MainActivity.class);
        onView(withId(R.id.signup_registration)).perform(click());
        onView(withId(R.id.registration)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testClickToSignInButton(){
//        ActivityScenario.launch(MainActivity.class);
//        onView(withId(R.id.button_login)).perform(click());
//        onView(withId(R.id.dashboard)).check(matches(isDisplayed()));
//
//
//    }
}