package com.example.databaserealtime;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity {

    public static final String ARTIST_NAME = "artistName";
    public static final String ARTIST_ID = "artistId";


    EditText editTextName;
    Button buttonAddArtist;
    Spinner spinnerGenre;
    DatabaseReference databaseArtists;

    ListView listViewArtists;
    List<Artist>  artists;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        databaseArtists = FirebaseDatabase.getInstance().getReference("artists");

        editTextName =  findViewById(R.id.editTextName);
        buttonAddArtist =  findViewById(R.id.buttonAddArtist);
        spinnerGenre =  findViewById(R.id.spinnerGenre);

        listViewArtists =   findViewById(R.id.listViewArtists);

        artists = new ArrayList<>();

        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addArtist();
            }
        });

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {

                Artist artist = artists.get(i);

                Intent intent = new Intent(getApplicationContext(), ArtistActivity.class);

                intent.putExtra(ARTIST_ID, artist.getArtistId());
                intent.putExtra(ARTIST_NAME, artist.getArtistName());

                startActivity(intent);


            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int i, long id) {

                Artist artist = artists.get(i);

                showUpdateDialog(artist.getArtistId(), artist.getArtistName());

                return false;



            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        databaseArtists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                artists.clear();

                for (DataSnapshot artistSnapshot : dataSnapshot.getChildren()){

                    Artist artist = artistSnapshot.getValue(Artist.class);

                    artists.add(artist);
                }
                ArtistList artistAdapter = new ArtistList(MainActivity.this, artists);

                listViewArtists.setAdapter(artistAdapter);
            }




            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showUpdateDialog(final String artistId, final String artistName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams") final View dialogView = inflater.inflate(R.layout.update_dialog, null);


        dialogBuilder.setView(dialogView);

        final EditText editTextName =  dialogView.findViewById(R.id.editTextName);
        final Button updateButton =  dialogView.findViewById(R.id.buttonUpdate);
        final Spinner spinnerGenres =  dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);



        dialogBuilder.setTitle("Updating Artist" + artistName);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenres.getSelectedItem().toString();

                if (!TextUtils.isEmpty(name)) {
                    updateArtist(artistId, name, genre);
                    alertDialog.dismiss();
                } else {
                    editTextName.setError("Name Required");

                }



            }

            });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteArtist(artistId);
            }
        });


    }

    private void deleteArtist(String artistId) {

        DatabaseReference drArtist = FirebaseDatabase.getInstance().getReference("artists").child(artistId);
        DatabaseReference drTracks = FirebaseDatabase.getInstance().getReference("Tracks").child(artistId);

        drArtist.removeValue();
        drTracks.removeValue();

        Toast.makeText(this, "Artist Deleted", Toast.LENGTH_SHORT).show();
    }

    private boolean updateArtist(String id, String name, String genre) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("artists").child(id);

        Artist artist = new Artist(id, name, genre);
        databaseReference.setValue(artist);

        Toast.makeText(this, "Artist updated Successfully", Toast.LENGTH_LONG).show();

        return true;
    }

    private void addArtist() {

        String name = editTextName.getText().toString().trim();
        String genre = spinnerGenre.getSelectedItem().toString();

        if(!TextUtils.isEmpty(name)){
            String id = databaseArtists.push().getKey();
            Artist artist = new Artist(id, name, genre);

            assert id != null;
            databaseArtists.child(id).setValue(artist);

            editTextName.setText("");

            Toast.makeText(this, "Artist Added", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "You should enter a name ", Toast.LENGTH_LONG).show();
        }
    }
}
