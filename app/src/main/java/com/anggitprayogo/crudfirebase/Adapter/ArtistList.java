package com.anggitprayogo.crudfirebase.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anggitprayogo.crudfirebase.Model.Artist;
import com.anggitprayogo.crudfirebase.R;

import java.util.List;

/**
 * Created by Anggit on 17/12/2017.
 */

public class ArtistList extends ArrayAdapter<Artist>{

    private Activity context;
    List<Artist> artists;

    public ArtistList(Activity context, List<Artist> artists) {
        super(context, R.layout.layout_artist_list,artists);
        this.context    =   context;
        this.artists    =   artists;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view   =   LayoutInflater.from(context).inflate(R.layout.layout_artist_list,null,true);

        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        TextView textViewGenre = (TextView) view.findViewById(R.id.textViewGenre);


        Artist  artist  =   artists.get(position);


        Log.d("DEBUG 1", String.valueOf(artist.getArtistName()));

        textViewName.setText(artist.getArtistName());
        textViewGenre.setText(artist.getArtistGenre());

        return view;
    }
}
