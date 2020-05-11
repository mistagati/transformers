package com.surpassplus.transformers.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.surpassplus.transformers.R;
import com.surpassplus.transformers.fragments.TransformerDetailsFragment;
import com.surpassplus.transformers.fragments.TransformerListingFragment;
import com.surpassplus.transformers.fragments.WarSummaryFragment;



public class TransformerContainerActivity extends AppCompatActivity implements TransformerListingFragment.OnFragmentInteractionListener,
TransformerDetailsFragment.OnFragmentInteractionListener, WarSummaryFragment.OnFragmentInteractionListener {

    Fragment fragment;

    boolean initiateReload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transformer_container);
        intUi();
    }

    private void intUi() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = TransformerListingFragment.newInstance();
        if (fragment != null) {
            fragmentManager.beginTransaction().add(R.id.branchcontainer, fragment).commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        String s = uri.getPath();
    }

    public void setInitiateReload(boolean initiate) {
        initiateReload = initiate;
    }

    @Override
    public void onBackPressed() {
        if (initiateReload) {
            if (fragment instanceof TransformerListingFragment) {
                initiateReload = false;
                ((TransformerListingFragment) fragment).loadData(false);
            }
            super.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }
}
