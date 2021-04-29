package com.example.expensetracker;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                HomeTest.class,
                MainActivityTest.class,
                RegistrationTest.class}
)
public class ActivityTestSuits {
}
