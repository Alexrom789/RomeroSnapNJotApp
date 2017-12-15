package com.aromero.app.api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.aromero.app.Note;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.util.Base64.DEFAULT;
import static android.util.Base64.encodeToString;

public class Api {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public Api() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    private DatabaseReference getNotesReference() {
        String userId = auth.getUid();
        if (userId == null) return null;

        return database.getReference("notes").child(userId);
    }

    public void loadNotes(final ApiCallbacks.LoadCallback callback) {
        final DatabaseReference reference = getNotesReference();
        if (reference == null) {
            callback.onFailure();
            return;
        }

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reference.removeEventListener(this);

                List<Note> notes = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Note note = child.getValue(Note.class);
                    if (note != null)
                        notes.add(note);
                }
                callback.onSuccess(notes);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onFailure();
            }
        });
    }



    public void createNote(final ApiCallbacks.CreateCallback callback, final Note note) {
        final DatabaseReference reference = getNotesReference();
        if (reference == null) {
            callback.onFailure();
            return;
        }

        reference.child(Long.toString(note.getTimestamp()))
                .setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess(note);
                } else callback.onFailure();
            }
        });
    }

    public void updateNote(final ApiCallbacks.UpdateCallback callback, final Note note) {
        final DatabaseReference reference = getNotesReference();
        if (reference == null) {
            callback.onFailure();
            return;
        }

        reference.child(Long.toString(note.getTimestamp()))
                .setValue(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess(note);
                } else callback.onFailure();
            }
        });
    }

    public void removeNote(final ApiCallbacks.RemoveCallback callback, final Note note) {
        final DatabaseReference reference = getNotesReference();
        if (reference == null) {
            callback.onFailure();
            return;
        }

        reference.child(Long.toString(note.getTimestamp()))
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    callback.onSuccess(note);
                } else callback.onFailure();
            }
        });
    }

    public static Bitmap base64ToBitmap(String base64) throws IOException {
        byte[] decodedByteArray = android.util.Base64.decode(base64, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.length);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        if (bitmap == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return encodeToString(baos.toByteArray(), DEFAULT);
    }
}
