package com.surpassplus.transformers.activities;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import com.surpassplus.transformers.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class TransformerContainerActivityTest {

    @Rule
    public ActivityTestRule<TransformerContainerActivity> mActivityTestRule = new ActivityTestRule<TransformerContainerActivity>(TransformerContainerActivity.class);
    private TransformerContainerActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testApplicationLaunches() {
        View view = mActivity.findViewById(R.id.branchcontainer);
        assertNotNull(view);
    }

    @After
    public void tearDown() throws Exception {
        mActivity = null;
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void onFragmentInteraction() {
    }
}