package org.apache.cayenne.demo.android.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.cayenne.demo.android.R;
import org.apache.cayenne.demo.android.model.Artist;

import java.text.DateFormat;
import java.util.List;

/**
 * {@link Artist} to view adapter.
 */
public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> {

    private List<Artist> artists;

    private View.OnClickListener listener;

    private DateFormat dateFormat;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView artistIdView;
        TextView artistNameView;
        TextView artistDateOfBirthView;
        ViewHolder(View v) {
            super(v);
            artistIdView = v.findViewById(R.id.artistId);
            artistNameView = v.findViewById(R.id.artistName);
            artistDateOfBirthView = v.findViewById(R.id.artistDateOfBirth);
            artistNameView.setOnClickListener(view -> v.callOnClick());
        }
    }

    public ArtistAdapter(List<Artist> artists, Context context, View.OnClickListener listener) {
        this.artists = artists;
        this.listener = listener;
        this.dateFormat = android.text.format.DateFormat.getDateFormat(context);
    }

    @Override
    public ArtistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View artistView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.artist_view, parent, false);
        artistView.setOnClickListener(listener);
        return new ViewHolder(artistView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Artist artist = artists.get(position);
        holder.artistNameView.setText(artist.getName());
        holder.artistIdView.setText(artist.getObjectId().getIdSnapshot().get(Artist.ID_PK_COLUMN).toString());
        if(artist.getDateOfBirth() == null) {
            holder.artistDateOfBirthView.setText("");
        } else {
            holder.artistDateOfBirthView.setText(dateFormat.format(artist.getDateOfBirth()));
        }
    }

    @Override
    public int getItemCount() {
        return artists.size();
    }

}
