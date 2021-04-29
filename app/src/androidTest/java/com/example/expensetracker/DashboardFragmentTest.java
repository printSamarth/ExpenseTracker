package com.example.expensetracker;


import androidx.fragment.app.FragmentTransaction;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4ClassRunner.class)
public class DashboardFragmentTest {
    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(Home.class);

    @Test
    public void testDashboardFragment() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DashBoardFragment dashBoardFragment = startDashboardFragment();
            }
        });
        // Then use Espresso to test the Fragment
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }

    private DashBoardFragment startDashboardFragment() {
        Home activity = (Home) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        DashBoardFragment dashBoardFragment = new DashBoardFragment();
        transaction.add(dashBoardFragment, "dashBoardFragment");
        transaction.commit();
        return dashBoardFragment;
    }
}
