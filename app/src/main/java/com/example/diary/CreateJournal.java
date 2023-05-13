package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateJournal extends AppCompatActivity {
    ImageView btnReturn,btnSaveNote,btnLocation;
    EditText editTextTitle,editTextContent;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_journal);
        btnReturn = findViewById(R.id.btnReturn);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        c = this;

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteSave();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString(),
                        content = editTextContent.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                if(!title.isEmpty()||!content.isEmpty()){
                    builder.setMessage("Note will not get save, are you sure?")
                            .setTitle("Confirm before leaving")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    onBackPressed();
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });
    }
    void noteSave(){
        String nodeTitle = editTextTitle.getText().toString();
        String nodeContent = editTextContent.getText().toString();
        if(nodeTitle.isEmpty()){
            editTextTitle.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(nodeTitle);
        note.setContent(nodeContent);
        note.setTimestamp(Timestamp.now());

        noteSaveToFirebasae(note);
    }

    void noteSaveToFirebasae(Note note){
        DocumentReference documentReference;
        documentReference = Collection.getCollectionReferenceForNotes().document();

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(c, "Added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(c, "Fail while adding", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}