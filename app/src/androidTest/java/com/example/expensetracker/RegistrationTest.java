package com.example.expensetracker;

import androidx.test.core.app.ActivityScenario;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.*;

public class RegistrationTest {

    @Test
    public void testActivityInView() {
        ActivityScenario.launch(Registration.class);
        onView(withId(R.id.registration)).check(matches(isDisplayed()));
    }

    @Test
    public void goBackToSignIn(){
        ActivityScenario.launch(Registration.class);
        onView(withId(R.id.signin_registration)).perform(click());
        onView(withId(R.id.main)).check(matches(isDisplayed()));
    }
}