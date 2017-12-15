package org.apache.cayenne.demo.android.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.demo.android.R;
import org.apache.cayenne.demo.android.model.Artist;
import org.apache.cayenne.demo.android.service.CayenneService;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;

/**
 * Activity for {@link Artist} modification
 */
public class ArtistActivity extends DaggerAppCompatActivity {

    @Inject
    CayenneService cayenneService;

    ObjectContext context;
    Artist artist;

    EditText editArtistName;
    EditText editArtistDateOfBirth;
    Button pickDateBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist);

        Toolbar toolbar = findViewById(R.id.artistActivityToolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        editArtistName = findViewById(R.id.editArtistName);
        editArtistDateOfBirth = findViewById(R.id.editArtistDateOfBirth);
        pickDateBtn = findViewById(R.id.pickDateBtn);

        context = cayenneService.newContext();

        initArtist();
        initDatePicker();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                updateArtist();
                return true;

            case R.id.action_delete:
                deleteArtist();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void deleteArtist() {
        new AlertDialog.Builder(this)
                .setMessage("Delete artist?")
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    context.deleteObject(artist);
                    context.commitChanges();
                    onBackPressed();
                })
                .setNegativeButton(android.R.string.cancel, null).show();
    }

    private void updateArtist() {
        artist.setName(editArtistName.getText().toString());
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
        try {
            artist.setDateOfBirth(dateFormat.parse(editArtistDateOfBirth.getText().toString()));
        } catch (ParseException e) {
            Log.w("Cayenne", e);
        }
        context.commitChanges();
        onBackPressed();
    }

    protected void initArtist() {
        Intent intent = getIntent();
        int artistId = intent.getIntExtra(MainActivity.EXTRA_ARTIST_ID, 0);
        if(artistId == 0) {
            // new artist
            artist = context.newObject(Artist.class);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(R.string.new_artist);
        } else {
            // existing
            artist = Cayenne.objectForPK(context, Artist.class, artistId);
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(R.string.edit_artist);
        }

        editArtistName.setText(artist.getName());

        if(artist.getDateOfBirth() != null) {
            DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
            editArtistDateOfBirth.setText(dateFormat.format(artist.getDateOfBirth()));
        }
    }

    private void initDatePicker() {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        DatePickerDialog.OnDateSetListener dateListener = (view, year, monthOfYear, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, monthOfYear, dayOfMonth);
            editArtistDateOfBirth.setText(dateFormat.format(calendar.getTime()));
        };

        pickDateBtn.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            Date date = artist.getDateOfBirth();
            if(date != null) {
                calendar.setTime(date);
            } else {
                calendar.set(1900, 0, 1);
            }

            DatePickerDialog dp = new DatePickerDialog(ArtistActivity.this, dateListener,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            Calendar minDate = Calendar.getInstance();
            minDate.set(1000, 0, 1);
            dp.getDatePicker().setMinDate(minDate.getTimeInMillis());
            dp.show();
        });
    }
}
