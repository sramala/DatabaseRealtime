package com.example.databaserealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ArtistActivity extends AppCompatActivity {

    TextView textViewArtistName, textViewRating;
    EditText editTextTrackName;
    SeekBar seekBarRating;
    Button buttonAddTrack;
    DatabaseReference databaseTracks;

    ListView listViewTracks;
    List<Track> tracks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_activity);

        Intent intent = getIntent();

        databaseTracks = FirebaseDatabase.getInstance().getReference("tracks").child(intent.getStringExtra(MainActivity.ARTIST_ID));

        textViewArtistName = findViewById(R.id.textViewArtistName);
        textViewRating = findViewById(R.id.textViewRating);
        editTextTrackName = findViewById(R.id.editTextTrackName);
        seekBarRating = findViewById(R.id.seekBarRating);
        buttonAddTrack = findViewById(R.id.buttonAddTrack);

        listViewTracks = findViewById(R.id.listViewTracks);

        tracks = new ArrayList<>();

        textViewArtistName.setText(intent.getStringExtra(MainActivity.ARTIST_NAME));


        seekBarRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                textViewRating.setText(String.valueOf(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonAddTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrack();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseTracks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                tracks.clear();
                for (DataSnapshot trackSnapshot : dataSnapshot.getChildren()) {
                    Track track = trackSnapshot.getValue(Track.class);
                    tracks.add(track);
                }
                TrackList trackListAdapter = new TrackList(ArtistActivity.this, tracks);
                listViewTracks.setAdapter(trackListAdapter);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void saveTrack(){

        String trackName = editTextTrackName.getText().toString().trim();

        int rating = seekBarRating.getProgress();

        if (!TextUtils.isEmpty(trackName)){
            String id = databaseTracks.push().getKey();

            Track track = new Track(id, trackName, rating);

            assert id != null;
            databaseTracks.child(id).setValue(track);

            Toast.makeText(this, "Track Saved Successfully", Toast.LENGTH_LONG).show();

            editTextTrackName.setText("");


        } else {
            Toast.makeText(this, "Trackname should not be Empty", Toast.LENGTH_LONG).show();
        }
    }
}
