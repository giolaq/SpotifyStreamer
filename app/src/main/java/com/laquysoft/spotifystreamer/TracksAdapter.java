package com.laquysoft.spotifystreamer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by joaobiriba on 12/06/15.
 */
public class TracksAdapter extends ArrayAdapter<Track> {


    private class ViewHolder {
        public TextView name;
        public ImageView thumbnail;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_artist, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView)convertView.findViewById(R.id.list_item_artist_textview);
            viewHolder.thumbnail = (ImageView)convertView.findViewById(R.id.thumbnail);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Track track = getItem(position);
        viewHolder.name.setText(track.name);

        if ( track.album.images.size() > 0 ) {
            int littleThumbnailPos = track.album.images.size() - 1;
            Picasso.with(parent.getContext()).load(track.album.images.get(littleThumbnailPos).url).into(viewHolder.thumbnail);
        }

        return convertView;
    }


    public TracksAdapter(Context context, int resource,
                         List<Track> tracks) {
        super(context, resource, tracks);
    }
}
