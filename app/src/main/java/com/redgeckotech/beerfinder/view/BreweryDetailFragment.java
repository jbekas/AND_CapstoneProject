package com.redgeckotech.beerfinder.view;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.Toast;

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

import static android.Manifest.permission.CALL_PHONE;

public class BreweryDetailFragment extends Fragment {

    private final static int ACCESS_MAKE_CALL = 1;

    private final static String BUNDLE_KEY_MAP_STATE = "mapData";
    private final static String BUNDLE_PHONE_NUMBER_TO_CALL = "phoneNumber";

    @Inject FirebaseAnalytics firebaseAnalytics;
    @Inject Picasso picasso;

    @BindView(R.id.brewery_detail_scrollview) ScrollView scrollView;
    @BindView(R.id.brewery_detail_layout) ViewGroup breweryDetailLayout;
    @BindView(R.id.brewery_image_layout) ViewGroup breweryImageLayout;
    @BindView(R.id.brewery_image) ImageView breweryImage;
    @BindView(R.id.address) TextView address;
    @BindView(R.id.address2) TextView address2;
    @BindView(R.id.city_state_postal_code) TextView cityStatePostal;
    @BindView(R.id.map) MapView mapView;
    @BindView(R.id.taproom_hours_layout) ViewGroup taproomHoursLayout;
    @BindView(R.id.taproom_hours) TextView taproomHours;
    @BindView(R.id.tour_hours_layout) ViewGroup tourHoursLayout;
    @BindView(R.id.tour_hours) TextView tourHours;
    @BindView(R.id.other_info_layout) ViewGroup otherInfoLayout;
    @BindView(R.id.phone_layout) ViewGroup phoneLayout;
    @BindView(R.id.phone) TextView phone;
    @BindView(R.id.website_layout) ViewGroup websiteLayout;
    @BindView(R.id.website) TextView website;
    @BindView(R.id.twitter_layout) ViewGroup twitterLayout;
    @BindView(R.id.twitter) TextView twitter;
    @BindView(R.id.beer_list) RecyclerView beerListView;

    private Brewery brewery;
    private GoogleMap googleMap;

    private String phoneNumberToCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dependency injection
        ((BreweryInfoApplication) getActivity().getApplicationContext()).getApplicationComponent().inject(this);

        if (savedInstanceState != null) {
            brewery = savedInstanceState.getParcelable(Constants.EXTRA_BREWERY);
            phoneNumberToCall = savedInstanceState.getString(BUNDLE_PHONE_NUMBER_TO_CALL);
        } else {
            if (getArguments() != null) {
                brewery = getArguments().getParcelable(Constants.EXTRA_BREWERY);
            }
        }

//        if (brewery == null) {
//            Timber.w("brewery is null.");
//            return;
//        }

        if (brewery != null) {
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, Long.toString(brewery.getId()));
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, brewery.getName());
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            Timber.d("brewery: %s", brewery);
        } else {
            Timber.i("brewery is null.");
        }
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

        if (brewery == null) {
            breweryDetailLayout.setVisibility(View.GONE);
        } else {
            breweryDetailLayout.setVisibility(View.VISIBLE);

            try {
                MapsInitializer.initialize(getActivity().getApplicationContext());
            } catch (Exception e) {
                Timber.e(e, null);
            }

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
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(brewery.getName()));//.snippet(addressString));

                        // For zooming automatically to the location of the marker
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
                        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                });
            }


            if (TextUtils.isEmpty(brewery.getPhotoUrl())) {
                picasso.load(R.drawable.no_brewery_image).into(breweryImage);
            } else {
                picasso.load(brewery.getPhotoUrl()).into(breweryImage);
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

            // Taproom Hours Section

            if (TextUtils.isEmpty(brewery.getTaproomHours())) {
                taproomHoursLayout.setVisibility(View.GONE);
            } else {
                taproomHoursLayout.setVisibility(View.VISIBLE);
                taproomHours.setText(brewery.getTaproomHours());
            }

            // Tour Hours Section

            if (TextUtils.isEmpty(brewery.getTourHours())) {
                tourHoursLayout.setVisibility(View.GONE);
            } else {
                tourHoursLayout.setVisibility(View.VISIBLE);
                tourHours.setText(brewery.getTourHours());
            }

            // Other Info Section

            if (TextUtils.isEmpty(brewery.getPhone()) &&
                    TextUtils.isEmpty(brewery.getWebsite()) &&
                    TextUtils.isEmpty(brewery.getTwitter())) {

                otherInfoLayout.setVisibility(View.GONE);
            } else {

                otherInfoLayout.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(brewery.getPhone())) {
                    phoneLayout.setVisibility(View.GONE);
                } else {
                    phoneLayout.setVisibility(View.VISIBLE);
                    phoneLayout.setOnClickListener(v -> {
                        makeCall(brewery.getPhone());
                    });
                    phone.setText(brewery.getPhone());
                }

                if (TextUtils.isEmpty(brewery.getWebsite())) {
                    websiteLayout.setVisibility(View.GONE);
                } else {
                    websiteLayout.setVisibility(View.VISIBLE);
                    websiteLayout.setOnClickListener(v -> {
                        viewUrl(brewery.getWebsite());
                    });
                    website.setText(brewery.getWebsite());
                }

                if (TextUtils.isEmpty(brewery.getTwitter())) {
                    twitterLayout.setVisibility(View.GONE);
                } else {
                    twitterLayout.setVisibility(View.VISIBLE);
                    twitterLayout.setOnClickListener(v -> {
                        String twitterName = brewery.getTwitter().replace("@", "");
                        String twitterUrl = String.format(Locale.US, "http://www.twitter.com/%s", twitterName);
                        viewUrl(twitterUrl);
                    });
                    twitter.setText(brewery.getTwitter());
                }
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

        outState.putString(BUNDLE_PHONE_NUMBER_TO_CALL, phoneNumberToCall);

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        Timber.d("onRequestPermissionsResult requestCode: %d, permissions: %s, grantResults: %s", requestCode, permissions, grantResults);

        switch (requestCode) {
            case ACCESS_MAKE_CALL: {
                if (hasMakeCallPermission()) {
                    makeCall(phoneNumberToCall);
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean hasMakeCallPermission() {
        return (getActivity()).checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkPermissionsAndMakeCall(String phoneNumber) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // If user already has external storage permissions, initiate gallery intent.
            if ((getActivity()).checkSelfPermission(CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                makeCall(phoneNumber);
            } else {
                phoneNumberToCall = phoneNumber;
                requestPermissions(new String[] {CALL_PHONE}, ACCESS_MAKE_CALL);
            }
        } else {
            makeCall(phoneNumber);
        }
    }

    private void makeCall(String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null));
            startActivity(intent);
        } catch (Exception e) {
            Timber.e(e, null);
            Toast.makeText(getActivity(), R.string.unable_to_make_call, Toast.LENGTH_SHORT).show();
        }
    }

    private void viewUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            Timber.e(e, null);
            Toast.makeText(getActivity(), R.string.unable_to_view_website, Toast.LENGTH_SHORT).show();
        }
    }
}

