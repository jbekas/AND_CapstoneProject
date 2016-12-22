package com.redgeckotech.beerfinder.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.Constants;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;

import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements BreweryListFragment.OnListFragmentInteractionListener {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((BreweryInfoApplication) getApplicationContext()).getApplicationComponent().inject(this);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.brewery_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.brewery_detail_container, new BreweryDetailFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }

    @Override
    public void onListFragmentInteraction(Brewery brewery) {
        Timber.d("onListFragmentInteraction: %s", brewery);

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (brewery != null) {
                Bundle arguments = new Bundle();
                arguments.putParcelable(Constants.EXTRA_BREWERY, brewery);

                BreweryDetailFragment fragment = new BreweryDetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.brewery_detail_container, fragment, DETAILFRAGMENT_TAG)
                        .commit();
            } else {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(DETAILFRAGMENT_TAG);
                if (fragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .commit();
                }
            }
        } else {
            // for phones, start new activity

            if (brewery != null) {
                Intent intent = new Intent(this, BreweryDetailActivity.class);
                intent.putExtra(Constants.EXTRA_BREWERY, brewery);
                startActivity(intent);
            } else {
                Timber.w("brewery is null.");
            }
        }
    }
}
