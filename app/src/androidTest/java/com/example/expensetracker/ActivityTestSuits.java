package com.example.expensetracker;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                HomeTest.class,
                MainActivityTest.class,
                RegistrationTest.class,
                DashboardFragmentTest.class,
                IncomeFragmentTest.class,
                ExpenseFragmentTest.class
        }
)
public class ActivityTestSuits {
}
