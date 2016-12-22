package com.redgeckotech.beerfinder.view;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Beer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

class BeerRecyclerViewAdapter extends RecyclerView.Adapter<BeerRecyclerViewAdapter.BeerViewHolder> {

    private List<Beer> beerList;

    BeerRecyclerViewAdapter(List<Beer> beerList) {
        this.beerList = beerList;
    }

    @Override
    public BeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeerRecyclerViewAdapter.BeerViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(BeerViewHolder holder, int position) {
        Beer beer = beerList.get(position);

        Timber.d("%d %s", position, beer);

        holder.beerName.setText(beer.getName());
        holder.style.setText(beer.getStyle());
        holder.abv.setText(Float.toString(beer.getAbv()));
        holder.ibu.setText(Integer.toString(beer.getIbu()));
    }

    @Override
    public int getItemCount() {
        return beerList != null ? beerList.size() : 0;
    }

    static class BeerViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.beer_name) TextView beerName;
        @BindView(R.id.style) TextView style;
        @BindView(R.id.abv) TextView abv;
        @BindView(R.id.ibu) TextView ibu;

        BeerViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.template_beer, parent, false));

            ButterKnife.bind(this, itemView);
        }

    }
}
