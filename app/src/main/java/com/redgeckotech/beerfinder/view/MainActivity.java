package com.redgeckotech.beerfinder.view;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redgeckotech.beerfinder.BreweryInfoApplication;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;
import com.redgeckotech.beerfinder.data.BreweryLoader;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends BaseActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BREWERY_LIST_LOADER = 0;
    private static final String BUNDLE_BREWERY_ID = "conversation_id";

    private LinearLayoutManager linearLayoutManager;
    private BreweryRecyclerAdapter breweryRecyclerAdapter;

    @BindView(R.id.brewery_list) RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((BreweryInfoApplication) getApplicationContext()).getApplicationComponent().inject(this);

        ButterKnife.bind(this);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        getLoaderManager().initLoader(BREWERY_LIST_LOADER, new Bundle(), this);

    }

    static class BreweryViewHolder extends ViewHolder {

        @BindView(R.id.brewery_logo) ImageView breweryLogo;
        @BindView(R.id.brewery_name) TextView breweryName;
        @BindView(R.id.brewery_address) TextView breweryAddress;

        public BreweryViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.template_brewery_item, parent, false));

            ButterKnife.bind(this, itemView);
        }
    }

    public class BreweryRecyclerAdapter
            extends CursorRecyclerViewAdapter<BreweryViewHolder> {


        public BreweryRecyclerAdapter(Cursor cursor) {
            super(cursor);
        }

        @Override
        public void onBindViewHolderCursor(BreweryViewHolder viewHolder, Cursor cursor) {
            Brewery brewery = new Brewery(cursor);

            Timber.d(brewery.toString());

            viewHolder.breweryName.setText(brewery.getName());
            viewHolder.breweryAddress.setText(brewery.getAddress());

            Picasso.with(MainActivity.this)
                    .load(brewery.getLogoUrl())
                    .placeholder(R.drawable.beer_mug)
                    .into(viewHolder.breweryLogo);
        }

        @Override
        public int getItemViewTypeCursor(Cursor cursor) {
            return 0;
        }

        @Override
        public BreweryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new BreweryViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public int getItemCount() {
            return getCursor() != null ? getCursor().getCount() : 0;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //
    // LoaderManager.LoaderCallbacks<Cursor> implementation
    //
    ///////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return BreweryLoader.breweryInstance(this);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        Timber.d("swapping cursor, count: %d", cursor.getCount());

        if (breweryRecyclerAdapter == null) {
            breweryRecyclerAdapter = new BreweryRecyclerAdapter(cursor);
            recyclerView.setAdapter(breweryRecyclerAdapter);
        } else {
            breweryRecyclerAdapter.swapCursor(cursor);
        }

        //updateUI();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        recyclerView.setAdapter(null);
    }

}
