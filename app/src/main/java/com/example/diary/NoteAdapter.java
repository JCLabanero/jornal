package com.example.diary;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    Context context;

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note model) {
        holder.timestampText.setText(Collection.timestampToString(model.timestamp));
        holder.contentText.setText(model.content);
        holder.titleText.setText(model.title);
        holder.itemView.setOnClickListener((view -> {
            Intent intent = new Intent(context,JournalControl.class);
            intent.putExtra("title",model.title);
            intent.putExtra("content",model.content);
            String id = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("id",id);
            context.startActivity(intent);
        }));
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder{
        TextView titleText, contentText, timestampText;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.noteTitle);
            contentText = itemView.findViewById(R.id.noteContent);
            timestampText = itemView.findViewById(R.id.noteTime);
        }
    }
}
