package com.anggitprayogo.crudfirebase;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.anggitprayogo.crudfirebase.Adapter.ArtistList;
import com.anggitprayogo.crudfirebase.Model.Artist;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //we will use these constants later to pass the artist name and id to another activity
    public static final String ARTIST_NAME = "net.simplifiedcoding.firebasedatabaseexample.artistname";
    public static final String ARTIST_ID = "net.simplifiedcoding.firebasedatabaseexample.artistid";

    //view objects
    EditText editTextName;
    Spinner spinnerGenre;
    Button buttonAddArtist;
    ListView listViewArtists;

    //a list to store all the artist from firebase database
    List<Artist> artists;

    DatabaseReference   mDatabaseRefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getting views
        editTextName = (EditText) findViewById(R.id.editTextName);
        spinnerGenre = (Spinner) findViewById(R.id.spinnerGenres);
        listViewArtists = (ListView) findViewById(R.id.listViewArtists);

        buttonAddArtist = (Button) findViewById(R.id.buttonAddArtist);

        //Mendapatkan refrence ke node artis
        mDatabaseRefrence   = FirebaseDatabase.getInstance().getReference("artist");

        artists =   new ArrayList<>();

        buttonAddArtist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addArtist();
            }
        });

        listViewArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Mendapatkan artsti bedasarkan id (yang diklik)
                Artist  artist  =   artists.get(i);

                Intent intent   =   new Intent(MainActivity.this,ArtistActivity.class);

                intent.putExtra(ARTIST_ID,artist.getArtistId());
                intent.putExtra(ARTIST_NAME,artist.getArtistName());

                startActivity(intent);
            }
        });

        listViewArtists.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Artist  artist  =   artists.get(i);
                showUpdateDeleteDialog(artist.getArtistId(),artist.getArtistName());
                return true;
            };
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseRefrence.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Membersihkan value list sebelumnya
                artists.clear();

                //Iterasi melalui semua node
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    //Mendapatkan data artis
                    Artist  artist  =   postSnapshot.getValue(Artist.class);

                    artists.add(artist);
                }

                Log.d("DEBUG 1", String.valueOf(artists.size()));

                //Buat adapter
                ArtistList  artistAdapter   =   new ArtistList(MainActivity.this,artists);


                //Manyisipkan adapter ke listview
                listViewArtists.setAdapter(artistAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addArtist() {
        String name =   editTextName.getText().toString();
        String genre    =   spinnerGenre.getSelectedItem().toString();

        //periksa apakah nilai namenya gak kosong
        if (!TextUtils.isEmpty(name)){
            //mendaptkan unique id dengan getKey()
            //ini akan digunakan sebagai id pada node artist dan dijadikan sebagai primary keynya
            String id   =   mDatabaseRefrence.push().getKey();

            //Buat objek artis
            Artist artist   =   new Artist(id,name,genre);

            //Simpan artis
            mDatabaseRefrence.child(id).setValue(artist);

            editTextName.setText("");

            Toast.makeText(MainActivity.this,"Berhasil Tambah data artis",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(MainActivity.this,"Gagal Tambah data artis",Toast.LENGTH_SHORT).show();
        }
    }

    private void showUpdateDeleteDialog(final String artistId, String artistName) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.update_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextName = (EditText) dialogView.findViewById(R.id.editTextName);
        final Spinner spinnerGenre = (Spinner) dialogView.findViewById(R.id.spinnerGenres);
        final Button buttonUpdate = (Button) dialogView.findViewById(R.id.buttonUpdateArtist);
        final Button buttonDelete = (Button) dialogView.findViewById(R.id.buttonDeleteArtist);

        dialogBuilder.setTitle(artistName);
        final AlertDialog b = dialogBuilder.create();
        b.show();


        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String genre = spinnerGenre.getSelectedItem().toString();
                if (!TextUtils.isEmpty(name)) {
                    updateArtist(artistId, name, genre);
                    b.dismiss();
                }
            }
        });


        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                * we will code this method to delete the artist
                * */
                deleteArtist(artistId);
                b.dismiss();
            }
        });
    }

    private boolean updateArtist(String id, String name, String genre){
        DatabaseReference mDatabaseRefrences    =   FirebaseDatabase.getInstance().getReference("artist").child(id);

        //Update
        Artist  artist  =   new Artist(id, name, genre);

        mDatabaseRefrences.setValue(artist);

        Toast.makeText(getApplicationContext(), "Artist Updated", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean deleteArtist(String id){
        DatabaseReference mDatabaseRefrences    =   FirebaseDatabase.getInstance().getReference("artist").child(id);

        mDatabaseRefrences.removeValue();

        DatabaseReference mDatabaseRefrences2    =   FirebaseDatabase.getInstance().getReference("tracks").child(id);

        mDatabaseRefrences2.removeValue();

        Toast.makeText(getApplicationContext(), "Artist Deleted", Toast.LENGTH_LONG).show();
        return true;
    }
}
