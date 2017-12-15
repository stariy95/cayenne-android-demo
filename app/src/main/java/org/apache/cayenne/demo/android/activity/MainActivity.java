package org.apache.cayenne.demo.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.apache.cayenne.demo.android.R;
import org.apache.cayenne.demo.android.model.Artist;
import org.apache.cayenne.demo.android.service.CayenneService;
import org.apache.cayenne.demo.android.view.ArtistAdapter;
import org.apache.cayenne.query.ObjectSelect;
import org.apache.cayenne.query.Ordering;

import java.util.List;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Main activity with list of {@link Artist}s.
 */
public class MainActivity extends DaggerAppCompatActivity {

    public static final String EXTRA_ARTIST_ID = "org.apache.cayenne.demo.cayennedemo.ARTIST_ID";

    @Inject
    CayenneService cayenneService;

    Ordering ordering = Artist.NAME.asc();

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        initListView();
        setListViewData();

        findViewById(R.id.fab).setOnClickListener(v -> createOrEditArtist(0));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setListViewData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void initListView() {
        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    protected void setListViewData() {
        final List<Artist> artists = ObjectSelect.query(Artist.class)
                .orderBy(ordering)
                .select(cayenneService.sharedContext());
        RecyclerView.Adapter adapter = new ArtistAdapter(artists, this, view -> {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            Artist item = artists.get(itemPosition);
            createOrEditArtist((Integer) item.getObjectId().getIdSnapshot().get(Artist.ID_PK_COLUMN));
        });
        recyclerView.setAdapter(adapter);
    }

    public void createOrEditArtist(int artistId) {
        Intent intent = new Intent(this, ArtistActivity.class);
        intent.putExtra(EXTRA_ARTIST_ID, artistId);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_sort_by_date:
                ordering = Artist.DATE_OF_BIRTH.asc();
                setListViewData();
                return true;

            case R.id.action_sort_by_name:
                ordering = Artist.NAME.asc();
                setListViewData();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
