package com.redgeckotech.beerfinder.view;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Brewery;
import com.redgeckotech.beerfinder.view.BreweryListFragment.OnListFragmentInteractionListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Brewery} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class BreweryListRecyclerViewAdapter extends CursorRecyclerViewAdapter<BreweryListRecyclerViewAdapter.BreweryViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private final Picasso picasso;

    public BreweryListRecyclerViewAdapter(Cursor cursor,
                                          OnListFragmentInteractionListener listener,
                                          Picasso picasso) {
        super(cursor);

        mListener = listener;
        this.picasso = picasso;
    }

    @Override
    public void onBindViewHolderCursor(final BreweryViewHolder viewHolder,
                                       Cursor cursor) {

        viewHolder.brewery = new Brewery(cursor);

        Timber.d(viewHolder.brewery.toString());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(viewHolder.brewery);
                }
            }
        });

        viewHolder.breweryName.setText(viewHolder.brewery.getName());
        viewHolder.breweryAddress.setText(viewHolder.brewery.getAddress());

        picasso.load(viewHolder.brewery.getLogoUrl())
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

    static class BreweryViewHolder extends RecyclerView.ViewHolder {

        Brewery brewery;

        @BindView(R.id.brewery_logo) ImageView breweryLogo;
        @BindView(R.id.brewery_name) TextView breweryName;
        @BindView(R.id.brewery_address) TextView breweryAddress;

        BreweryViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.template_brewery_item, parent, false));

            ButterKnife.bind(this, itemView);
        }
    }
}
