package com.redgeckotech.beerfinder.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.Constants;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BreweryDetailFragment extends Fragment {

    @Inject Picasso picasso;

    @BindView(R.id.brewery_detail_scrollview) ScrollView mScrollView;

    private Brewery brewery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dependency injection
        ((BreweryInfoApplication) getActivity().getApplicationContext()).getApplicationComponent().inject(this);

        if (savedInstanceState != null) {
            brewery = savedInstanceState.getParcelable(Constants.EXTRA_BREWERY);
        } else {
            if (getArguments() != null) {
                brewery = getArguments().getParcelable(Constants.EXTRA_BREWERY);
            }
        }

        if (brewery == null) {
            Timber.w("brewery is null.");
            return;
        }

        Timber.d("brewery: %s", brewery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_brewery_detail, container, false);

        ButterKnife.bind(this, rootView);

        updateUI();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constants.EXTRA_BREWERY, brewery);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void updateUI() {
        final Activity activity = getActivity();

        if (activity == null) {
            Timber.w("Activity no longer exists, returning.");
            return;
        } else {

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (brewery == null) {
                            mScrollView.setVisibility(View.INVISIBLE);
                        } else {
                            mScrollView.setVisibility(View.VISIBLE);
                        }
                    } catch (IllegalStateException e) {
                        Timber.e(e, null);
                    }
                }
            });
        }
    }
}

