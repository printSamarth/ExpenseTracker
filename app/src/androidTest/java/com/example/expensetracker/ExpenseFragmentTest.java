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
public class ExpenseFragmentTest {
    @Rule
    public ActivityTestRule activityRule = new ActivityTestRule<>(Home.class);

    @Test
    public void testExpenseFragment() {
        activityRule.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ExpenseFragment expenseFragment = startExpenseFragment();
            }
        });
        // Then use Espresso to test the Fragment
        onView(withId(R.id.drawer_layout)).check(matches(isDisplayed()));
    }

    private ExpenseFragment startExpenseFragment() {
        Home activity = (Home) activityRule.getActivity();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        ExpenseFragment expenseFragment = new ExpenseFragment();
        transaction.add(expenseFragment, "expenseFragment");
        transaction.commit();
        return expenseFragment;
    }

}

