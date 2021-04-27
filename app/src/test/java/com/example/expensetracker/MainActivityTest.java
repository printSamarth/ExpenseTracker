package com.example.expensetracker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(MockitoJUnitRunner.class)
@PrepareForTest
public class MainActivityTest extends TestCase {

//    private DatabaseReference mockedDatabaseReference;
//
//    @Before
//    public void before() {
//        mockedDatabaseReference = Mockito.mock(DatabaseReference.class);
//
//        FirebaseDatabase mockedFirebaseDatabase = Mockito.mock(FirebaseDatabase.class);
//        when(mockedFirebaseDatabase.getReference()).thenReturn(mockedDatabaseReference);
//
//        PowerMockito.mockStatic(FirebaseDatabase.class);
//        when(FirebaseDatabase.getInstance()).thenReturn(mockedFirebaseDatabase);
//    }
//
//    @Test
//    public void test_validate(){
//        MainActivity mainActivity = new MainActivity();
//        Boolean result = mainActivity.validate("s@gmail.com", "12345");
//        assertFalse(result);
//
//    }
}