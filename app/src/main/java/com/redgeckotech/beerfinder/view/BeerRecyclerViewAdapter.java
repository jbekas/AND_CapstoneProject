package com.redgeckotech.beerfinder.view;

import android.app.Activity;
import android.app.ApplicationErrorReport;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.crash.FirebaseCrash;
import com.redgeckotech.beerfinder.R;
import com.redgeckotech.beerfinder.data.Beer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

class BeerRecyclerViewAdapter extends RecyclerView.Adapter<BeerRecyclerViewAdapter.BeerViewHolder> {

    private Context context;
    private List<Beer> beerList;

    BeerRecyclerViewAdapter(Context context, List<Beer> beerList) {
        this.context = context;
        this.beerList = beerList;
    }

    @Override
    public BeerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new BeerRecyclerViewAdapter.BeerViewHolder(LayoutInflater.from(parent.getContext()), parent);
    }

    @Override
    public void onBindViewHolder(BeerViewHolder holder, int position) {
        try {
            Beer beer = beerList.get(position);

            Timber.d(beer.toString());

            if (context != null) {
                holder.beerName.setText(beer.getName());

                if (TextUtils.isEmpty(beer.getStyle())) {
                    holder.style.setVisibility(View.GONE);
                } else {
                    holder.style.setVisibility(View.VISIBLE);
                    holder.style.setText(context.getString(R.string.style_template, beer.getStyle()));
                }

                if (beer.getAbv() == 0) {
                    holder.abv.setVisibility(View.GONE);
                } else {
                    holder.abv.setVisibility(View.VISIBLE);
                    holder.abv.setText(context.getString(R.string.abv_template, beer.getAbv()));
                }

                if (beer.getIbu() == 0) {
                    holder.ibu.setVisibility(View.GONE);
                } else {
                    holder.ibu.setVisibility(View.VISIBLE);
                    holder.ibu.setText(context.getString(R.string.ibu_template, beer.getIbu()));
                }

                if (TextUtils.isEmpty(beer.getDescription())) {
                    holder.description.setVisibility(View.GONE);
                } else {
                    holder.description.setVisibility(View.VISIBLE);
                    holder.description.setText(beer.getDescription());
                }
            }
        } catch (Exception e) {
            Timber.e(e, null);
            FirebaseCrash.report(e);
        }
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
        @BindView(R.id.description) TextView description;

        BeerViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.template_beer, parent, false));

            ButterKnife.bind(this, itemView);
        }

    }
}
