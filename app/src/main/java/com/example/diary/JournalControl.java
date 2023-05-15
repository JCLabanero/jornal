package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.json.JSONException;
import org.json.JSONObject;

public class JournalControl extends AppCompatActivity {
    ImageView btnReturn,btnSaveNote,btnWeather,btnDeleteNote;
    EditText editTextTitle,editTextContent,editLocationSearch;
    TextView pageTitle, locationDisplay;
    Context c;
    String title,content,id;
    LinearLayout linearLayout;
    boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_control);
        btnReturn = findViewById(R.id.btnReturn);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextContent = findViewById(R.id.editTextContent);
        btnDeleteNote = findViewById(R.id.btnDeleteNote);
        pageTitle = findViewById(R.id.title);
        linearLayout = findViewById(R.id.weatherControl);
        editLocationSearch = findViewById(R.id.editLocationSearch);
        locationDisplay = findViewById(R.id.locationDisplay);
        btnWeather = findViewById(R.id.btnWeather);
        c = this;
        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        id = getIntent().getStringExtra("id");

        if(id!=null) {
            isEditing = true;
            editTextTitle.setText(title);
            editTextContent.setText(content);
            pageTitle.setText("Edit note");
            btnDeleteNote.setVisibility(View.VISIBLE);
        }
        linearLayout.setVisibility(View.VISIBLE);
        btnWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.setVisibility(View.VISIBLE);
                if(linearLayout.getVisibility()==View.VISIBLE){
                    getWeather();
                }
            }
        });
        btnDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteDeleteFromFirebasae();
            }
        });
        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noteSave();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String thisTitle = editTextTitle.getText().toString(),
                        thisContent = editTextContent.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(c);
                if((!thisTitle.equals(title) || !thisContent.equals(content))&&isEditing){
                    builder.setMessage("Note changes will not get save, are you sure?")
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
                } else if((title==null||content==null)&&!isEditing){
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
                } else {
                    onBackPressed();
                }
            }
        });
    }

    public void getWeather(){
        String apiKey = "7d87458008572b7c952310829ff3788d";
        String cityKey = editLocationSearch.getText().toString();
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?g=%s&appid=%s", cityKey, apiKey);
//        String url2 = "https://api.openweathermap.org/data/2.5/weather?g="+cityKey+"&appid="+apiKey;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest requestjson = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject country = response.getJSONObject("sys");
                    JSONObject currentWeather = response.getJSONObject("weather");
                    String countryConverted = country.getString("country");
                    String currentWeatherConverted = currentWeather.getString("description");
                    locationDisplay.setText("Country: " + countryConverted +"\n"+"Weather: " + currentWeatherConverted);
                } catch (JSONException e) {
                    Toast.makeText(c, "error!" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(c, "error: 69 city not found!", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(requestjson);
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

    void noteDeleteFromFirebasae(){
        DocumentReference documentReference;
        documentReference = Collection.getCollectionReferenceForNotes().document(id);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(c, "Note Deleted Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(c, "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void noteSaveToFirebasae(Note note){
        DocumentReference documentReference;
        if(!isEditing){
            documentReference = Collection.getCollectionReferenceForNotes().document();
        } else {
            documentReference = Collection.getCollectionReferenceForNotes().document(id);
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(!isEditing){
                        Toast.makeText(c, "Added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(c, "Edit successful", Toast.LENGTH_SHORT).show();
                    }
                    finish();
                } else {
                    if(!isEditing){
                        Toast.makeText(c, "Fail while adding", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(c, "Edit failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}