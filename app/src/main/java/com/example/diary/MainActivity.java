package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {
    TextView textDisplay;
    Button btnLogOut;
    ImageButton btnMenu;
    FloatingActionButton btnAdd;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Context context;
    RecyclerView recyclerView;
    NoteAdapter noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textDisplay = findViewById(R.id.TextDisplay);
        btnLogOut = findViewById(R.id.Logout);
        btnAdd = findViewById(R.id.floating_action);
        btnMenu = findViewById(R.id.menu_btn);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        recyclerView = findViewById(R.id.recyclerView);
        context = this;
        if(user!=null){
//            boolean emailVerified = user.isEmailVerified();
//            String uid = user.getUid();
//            String name = user.getDisplayName();
            String email = user.getEmail();

            textDisplay.setText(email);
        } else {
            Intent intent = new Intent(context, Login.class);
            startActivity(intent);
            finish();
        }
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), JournalControl.class);
                startActivity(intent);
            }
        });
        setupRecyclerView();
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMenu();
            }
        });
    }
    void setupRecyclerView(){
        Query query = Collection.getCollectionReferenceForNotes().orderBy("timestamp",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }
    void showMenu(){
        PopupMenu popupMenu = new PopupMenu(context, btnMenu);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
                return false;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }
    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}