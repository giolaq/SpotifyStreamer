package com.laquysoft.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.laquysoft.spotifystreamer.model.ParcelableSpotifyObject;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class SpotifyObjectAdapter extends ArrayAdapter<ParcelableSpotifyObject> {


    private int mObjectType;

    public static final int VIEW_TYPE_ARTIST = 0;
    public static final int VIEW_TYPE_TOP_TRACK = 1;


    static class ViewHolder {
        @InjectView(R.id.list_item_first_textview)
        public TextView name;
        @InjectView(R.id.thumbnail)
        public ImageView thumbnail;
        @InjectView(R.id.list_item_second_textview)
        public TextView secondTextLine;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_artist, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ParcelableSpotifyObject track = getItem(position);

        if (mObjectType == VIEW_TYPE_TOP_TRACK ) {
            viewHolder.secondTextLine.setVisibility(View.VISIBLE);
            viewHolder.secondTextLine.setText(track.mFatherName);
        }


        viewHolder.name.setText(track.mName);

        if (!track.smallThumbnailUrl.isEmpty()) {
            Picasso.with(parent.getContext()).load(track.smallThumbnailUrl).into(viewHolder.thumbnail);
        }
        return convertView;
    }


    public SpotifyObjectAdapter(Context context, int resource,
                                List<ParcelableSpotifyObject> tracks, int objectType) {
        super(context, resource, tracks);
        mObjectType = objectType;
    }
}
