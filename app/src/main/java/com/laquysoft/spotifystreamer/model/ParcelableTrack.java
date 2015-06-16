package com.laquysoft.spotifystreamer.model;

import android.os.Parcel;
import android.os.Parcelable;

import kaaes.spotify.webapi.android.models.Track;

/**
 * Created by joaobiriba on 16/06/15.
 */
public class ParcelableTrack implements Parcelable {

    public String trackName;
    public String albumName;
    public String largeThumbnailUrl;
    public String smallThumbnailUrl;
    public String previewUrl;

    public ParcelableTrack(String name, String albumName, String largeThumbnailUrl,
                           String smallThumbnailUrl, String previewUrl) {
        this.trackName = name;
        this.albumName = albumName;
        this.largeThumbnailUrl = largeThumbnailUrl;
        this.smallThumbnailUrl = smallThumbnailUrl;
        this.previewUrl = previewUrl;
    }

    private ParcelableTrack(Parcel in) {
        trackName = in.readString();
        albumName = in.readString();
        largeThumbnailUrl = in.readString();
        smallThumbnailUrl = in.readString();
        previewUrl = in.readString();
    }


    public static final Creator<ParcelableTrack> CREATOR = new Creator<ParcelableTrack>() {
        @Override
        public ParcelableTrack createFromParcel(Parcel in) {
            return new ParcelableTrack(in);
        }

        @Override
        public ParcelableTrack[] newArray(int size) {
            return new ParcelableTrack[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.trackName);
        dest.writeString(this.albumName);
        dest.writeString(this.largeThumbnailUrl);
        dest.writeString(this.smallThumbnailUrl);
        dest.writeString(this.previewUrl);
    }
}
