package com.anggitprayogo.crudfirebase.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anggitprayogo.crudfirebase.Model.Track;
import com.anggitprayogo.crudfirebase.R;

import java.util.List;

/**
 * Created by Anggit on 17/12/2017.
 */

public class TrackList extends ArrayAdapter<Track>{

    private Activity context;
    List<Track> tracks;

    public TrackList(@NonNull Activity context, List<Track> tracks) {
        super(context, R.layout.layout_artist_list,tracks);
        this.context    =   context;
        this.tracks     =   tracks;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View    view    = LayoutInflater.from(context).inflate(R.layout.layout_artist_list,null,true);

        TextView textViewName = (TextView) view.findViewById(R.id.textViewName);
        TextView textViewRating = (TextView) view.findViewById(R.id.textViewGenre);

        Track track = tracks.get(position);
        textViewName.setText(track.getTrackName());
        textViewRating.setText(String.valueOf(track.getRating()));

        return view;
    }
}
