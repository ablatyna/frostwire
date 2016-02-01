/*
 * Copyright (C) 2012 Andrew Neal Licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.andrew.apollo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.andrew.apollo.model.Album;
import com.andrew.apollo.ui.MusicHolder;
import com.andrew.apollo.ui.fragments.profile.ArtistAlbumFragment;
import com.andrew.apollo.utils.Lists;
import com.andrew.apollo.utils.MusicUtils;
import com.frostwire.android.R;
import com.frostwire.util.Ref;

import java.util.List;

/**
 * This {@link ArrayAdapter} is used to display the albums for a particular
 * artist for {@link ArtistAlbumFragment} .
 * 
 * @author Andrew Neal (andrewdneal@gmail.com)
 */
public class ArtistAlbumAdapter extends ApolloFragmentAdapter<Album> {

    /**
     * The header view
     */
    private static final int ITEM_VIEW_TYPE_HEADER = 0;

    /**
     * * The data in the list.
     */
    private static final int ITEM_VIEW_TYPE_MUSIC = 1;

    /**
     * Number of views (ImageView, TextView, header)
     */
    private static final int VIEW_TYPE_COUNT = 3;

    /**
     * LayoutInflater
     */
    private final LayoutInflater mInflater;

    /**
     * Fake header
     */
    private final View mHeader;

    /**
     * Used to set the size of the data in the adapter
     */
    private List<Album> mCount = Lists.newArrayList();

    /**
     * Constructor of <code>ArtistAlbumAdapter</code>
     * 
     * @param context The {@link Context} to use
     * @param layoutId The resource Id of the view to inflate.
     */
    public ArtistAlbumAdapter(final Activity context, final int layoutId) {
        super(context, layoutId);
        // Used to create the custom layout
        mInflater = LayoutInflater.from(context);
        // Cache the header
        mHeader = mInflater.inflate(R.layout.faux_carousel, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // Return a faux header at position 0
        if (position == 0) {
            return mHeader;
        }

        // Recycle MusicHolder's items
        MusicHolder holder = prepareMusicHolder(mLayoutId, getContext(), convertView, parent);
        if (holder != null && Ref.alive(holder.mOverlay)) {
            holder.mOverlay.get().setBackgroundColor(0);
        }

        // Retrieve the album
        final Album album = getItem(position - 1);
        final String albumName = album.mAlbumName;

        if (holder != null) {
            if (Ref.alive(holder.mLineOne)) {
                // Set each album name (line one)
                holder.mLineOne.get().setText(albumName);
            }
            if (Ref.alive(holder.mLineTwo)) {
                // Set the number of songs (line two)
                holder.mLineTwo.get().setText(MusicUtils.makeLabel(getContext(),
                        R.plurals.Nsongs, album.mSongNumber));
            }
            if (Ref.alive(holder.mLineThree)) {
                // Set the album year (line three)
                holder.mLineThree.get().setText(album.mYear);
            }
        }

        // Asynchronously load the album images into the adapter
        mImageFetcher.loadAlbumImage(album.mArtistName,
                albumName, album.mAlbumId,
                holder.mImage.get());
        // Play the album when the artwork is touched
        playAlbum(holder.mImage.get(), position);
        return convertView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemViewType(final int position) {
        if (position == 0) {
            return ITEM_VIEW_TYPE_HEADER;
        }
        return ITEM_VIEW_TYPE_MUSIC;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(final int position) {
        if (position == 0) {
            return -1;
        }
        return position - 1;
    }

    /**
     * Starts playing an album if the user touches the artwork in the list.
     * 
     * @param album The {@link ImageView} holding the album
     * @param position The position of the album to play.
     */
    protected void playAlbum(final ImageView album, final int position) {
        if (album != null) {
            album.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(final View v) {
                    final long id = getItem(position - 1).mAlbumId;
                    final long[] list = MusicUtils.getSongListForAlbum(getContext(), id);
                    MusicUtils.playAll(getContext(), list, 0, false);
                }
            });
        }
    }
}
