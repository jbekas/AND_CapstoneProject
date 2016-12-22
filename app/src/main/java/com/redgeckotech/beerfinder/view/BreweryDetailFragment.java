package com.redgeckotech.beerfinder.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.Constants;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class BreweryDetailFragment extends Fragment {

    private final static String BUNDLE_KEY_MAP_STATE = "mapData";

    @Inject FirebaseAnalytics firebaseAnalytics;
    @Inject Picasso picasso;

    @BindView(R.id.brewery_detail_scrollview) ScrollView scrollView;
    @BindView(R.id.brewery_image_layout) ViewGroup breweryImageLayout;
    @BindView(R.id.brewery_image) ImageView breweryImage;
    @BindView(R.id.phone) TextView phone;
    @BindView(R.id.address) TextView address;
    @BindView(R.id.address2) TextView address2;
    @BindView(R.id.city_state_postal_code) TextView cityStatePostal;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.taproom_hours) TextView taproomHours;
    @BindView(R.id.tour_hours) TextView tourHours;
    @BindView(R.id.website) TextView website;
    @BindView(R.id.twitter) TextView twitter;
    @BindView(R.id.beer_list) RecyclerView beerListView;

    private Brewery brewery;
    private GoogleMap googleMap;

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

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Long.toString(brewery.getId()));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, brewery.getName());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        Timber.d("brewery: %s", brewery);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_brewery_detail, container, false);

        ButterKnife.bind(this, rootView);

        Bundle mapState = null;
        if (savedInstanceState != null) {
            // Load the map state bundle from the main savedInstanceState
            mapState = savedInstanceState.getBundle(BUNDLE_KEY_MAP_STATE);
        }
        mapView.onCreate(mapState);

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            Timber.e(e, null);
        }

        if (brewery == null) {
            scrollView.setVisibility(View.GONE);
        } else {
            scrollView.setVisibility(View.VISIBLE);

            if (brewery.getLatitude() == 0 || brewery.getLongitude() == 0) {
                mapView.setVisibility(View.GONE);
            } else {
                mapView.setVisibility(View.VISIBLE);
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap mMap) {
                        googleMap = mMap;

                        LatLng latLng = new LatLng(brewery.getLatitude(), brewery.getLongitude());

                        String addressString = String.format(Locale.US, "%s\n%s, %s %s", brewery.getAddress(), brewery.getCity(), brewery.getState(), brewery.getPostalCode());
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(brewery.getName()).snippet(addressString));

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
                        //googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }


            if (TextUtils.isEmpty(brewery.getPhotoUrl())) {
                breweryImageLayout.setVisibility(View.GONE);
            } else {
                breweryImageLayout.setVisibility(View.VISIBLE);

                picasso.load(brewery.getPhotoUrl()).into(breweryImage);
            }

            if (TextUtils.isEmpty(brewery.getPhone())) {
                phone.setVisibility(View.GONE);
            } else {
                phone.setVisibility(View.VISIBLE);
                phone.setText(getString(R.string.phone_template, brewery.getPhone()));
            }

            if (TextUtils.isEmpty(brewery.getAddress())) {
                address.setVisibility(View.GONE);
            } else {
                address.setVisibility(View.VISIBLE);
                address.setText(brewery.getAddress());
            }

            if (TextUtils.isEmpty(brewery.getAddress2())) {
                address2.setVisibility(View.GONE);
            } else {
                address2.setVisibility(View.VISIBLE);
                address2.setText(brewery.getAddress2());
            }

            String cityStatePostalString = String.format(Locale.getDefault(),
                    "%s, %s %s", brewery.getCity(), brewery.getState(),
                    brewery.getPostalCode());
            cityStatePostalString = cityStatePostalString.trim();

            if (TextUtils.isEmpty(cityStatePostalString) || ",".equals(cityStatePostalString)) {
                cityStatePostal.setVisibility(View.GONE);
            } else {
                cityStatePostal.setVisibility(View.VISIBLE);
                cityStatePostal.setText(cityStatePostalString);
            }

            if (TextUtils.isEmpty(brewery.getTaproomHours())) {
                taproomHours.setVisibility(View.GONE);
            } else {
                taproomHours.setVisibility(View.VISIBLE);
                taproomHours.setText(getString(R.string.taproom_hours_template, brewery.getTaproomHours()));
            }

            if (TextUtils.isEmpty(brewery.getTourHours())) {
                tourHours.setVisibility(View.GONE);
            } else {
                tourHours.setVisibility(View.VISIBLE);
                tourHours.setText(getString(R.string.tour_hours_template, brewery.getTourHours()));
            }

            if (TextUtils.isEmpty(brewery.getWebsite())) {
                website.setVisibility(View.GONE);
            } else {
                website.setVisibility(View.VISIBLE);
                website.setText(getString(R.string.website_template, brewery.getWebsite()));
            }

            if (TextUtils.isEmpty(brewery.getTwitter())) {
                twitter.setVisibility(View.GONE);
            } else {
                twitter.setVisibility(View.VISIBLE);
                twitter.setText(getString(R.string.twitter_template, brewery.getTwitter()));
            }

            BeerRecyclerViewAdapter beerAdapter = new BeerRecyclerViewAdapter(brewery.getBeerList());
            beerListView.setLayoutManager(new LinearLayoutManager(getActivity()));
            beerListView.setAdapter(beerAdapter);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapState = new Bundle();
        mapView.onSaveInstanceState(mapState);
        // Put the map bundle in the main outState
        outState.putBundle(BUNDLE_KEY_MAP_STATE, mapState);

        outState.putParcelable(Constants.EXTRA_BREWERY, brewery);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        mapView.onResume(); // needed to get the map to display immediately
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Timber.d("onActivityCreated called.");

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            if (brewery != null) {
                actionBar.setTitle(brewery.getName());
            }
        }
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
                            scrollView.setVisibility(View.INVISIBLE);
                        } else {
                            scrollView.setVisibility(View.VISIBLE);

                            if (TextUtils.isEmpty(brewery.getPhotoUrl())) {
                                breweryImageLayout.setVisibility(View.GONE);
                            } else {
                                breweryImageLayout.setVisibility(View.VISIBLE);

                                picasso.load(brewery.getPhotoUrl()).into(breweryImage);
                            }

                            if (TextUtils.isEmpty(brewery.getPhone())) {
                                phone.setVisibility(View.GONE);
                            } else {
                                phone.setVisibility(View.VISIBLE);
                                phone.setText(getString(R.string.phone_template, brewery.getPhone()));
                            }

                            if (TextUtils.isEmpty(brewery.getAddress())) {
                                address.setVisibility(View.GONE);
                            } else {
                                address.setVisibility(View.VISIBLE);
                                address.setText(brewery.getAddress());
                            }

                            if (TextUtils.isEmpty(brewery.getAddress2())) {
                                address2.setVisibility(View.GONE);
                            } else {
                                address2.setVisibility(View.VISIBLE);
                                address2.setText(brewery.getAddress2());
                            }

                            String cityStatePostalString = String.format(Locale.getDefault(),
                                    "%s, %s %s", brewery.getCity(), brewery.getState(),
                                    brewery.getPostalCode());
                            cityStatePostalString = cityStatePostalString.trim();

                            if (TextUtils.isEmpty(cityStatePostalString) || ",".equals(cityStatePostalString)) {
                                cityStatePostal.setVisibility(View.GONE);
                            } else {
                                cityStatePostal.setVisibility(View.VISIBLE);
                                cityStatePostal.setText(cityStatePostalString);
                            }

                            if (TextUtils.isEmpty(brewery.getTaproomHours())) {
                                taproomHours.setVisibility(View.GONE);
                            } else {
                                taproomHours.setVisibility(View.VISIBLE);
                                taproomHours.setText(getString(R.string.taproom_hours_template, brewery.getTaproomHours()));
                            }

                            if (TextUtils.isEmpty(brewery.getTourHours())) {
                                tourHours.setVisibility(View.GONE);
                            } else {
                                tourHours.setVisibility(View.VISIBLE);
                                tourHours.setText(getString(R.string.tour_hours_template, brewery.getTourHours()));
                            }

                            if (TextUtils.isEmpty(brewery.getWebsite())) {
                                website.setVisibility(View.GONE);
                            } else {
                                website.setVisibility(View.VISIBLE);
                                website.setText(getString(R.string.website_template, brewery.getWebsite()));
                            }

                            if (TextUtils.isEmpty(brewery.getTwitter())) {
                                twitter.setVisibility(View.GONE);
                            } else {
                                twitter.setVisibility(View.VISIBLE);
                                twitter.setText(getString(R.string.twitter_template, brewery.getTwitter()));
                            }


                        }
                    } catch (IllegalStateException e) {
                        Timber.e(e, null);
                    }
                }
            });
        }
    }
}

