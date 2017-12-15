package com.aromero.app.view.note;

import android.annotation.SuppressLint;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aromero.app.api.Api;
import com.aromero.app.R;
import com.aromero.app.Note;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {

    private final NoteOptionCallback callback;
    private List<Note> notes;
    private Comparator<Note> notesComparator
            = new Comparator<Note>() {
        @Override
        public int compare(Note note1, Note note2) {
            long t1 = note1.getTimestamp();
            long t2 = note2.getTimestamp();
            if (t1 > t2)
                return -1;
            else if (t1 < t2)
                return 1;
            return 0;
        }
    };

    NotesRecyclerAdapter(NoteOptionCallback callback) {
        this.notes = new ArrayList<>();
        this.callback = callback;
    }

    void removeNote(Note note) {
        Note noteToRemove = null;
        for (Note n : notes) {
            if (note.getTimestamp() == n.getTimestamp()) {
                noteToRemove = n;
                break;
            }
        }

        if (noteToRemove != null) {
            notes.remove(noteToRemove);
            notifyDataSetChanged();
        }
    }

    void addNotes(List<Note> notes) {
        for (Note note : notes) {
            addNote(note);
        }
        Collections.sort(notes, notesComparator);
        notifyDataSetChanged();
    }

    void addNote(Note note) {
        for (Note n : notes) {
            if (note.getTimestamp() == n.getTimestamp()) {
                updateNote(note);
                return;
            }
        }
        this.notes.add(note);
        Collections.sort(notes, notesComparator);
        notifyDataSetChanged();
    }


    void updateNote(Note note) {
        Note noteToUpdate = null;
        for (Note n : notes) {
            if (note.getTimestamp() == n.getTimestamp()) {
                noteToUpdate = n;
                break;
            }
        }

        if (noteToUpdate != null) {
            notes.set(notes.indexOf(noteToUpdate), note);
            Collections.sort(notes, notesComparator);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView image;
        private TextView content;
        private TextView description;
        private TextView date;
        private ImageView more;


        ViewHolder(View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.note_image);
            date = (TextView) itemView.findViewById(R.id.note_date);
            description = (TextView) itemView.findViewById(R.id.note_description);
            content = (TextView) itemView.findViewById(R.id.note_content);
            more = (ImageView) itemView.findViewById(R.id.note_more);

            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showNoteModal(notes.get(getAdapterPosition()));
                }
            });
        }

        private void showNoteModal(final Note note) {
            final BottomSheetDialog dialog = new BottomSheetDialog(itemView.getContext());
            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);

            @SuppressLint("InflateParams") View noteModal = LayoutInflater.from(itemView.getContext())
                    .inflate(R.layout.sheet_note, null);

            View editArea = noteModal.findViewById(R.id.sheet_edit);
            View deleteArea = noteModal.findViewById(R.id.sheet_delete);
            editArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.editOption(note);
                    dialog.cancel();
                }
            });
            deleteArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.deleteOption(note);
                    dialog.cancel();
                }
            });

            dialog.setContentView(noteModal);
            dialog.show();
        }

        private static final String DATE_PATTERN = "dd.MM HH:mm";
        private SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN, Locale.getDefault());

        void bind(Note note) {
            content.setText(note.getTitle());

            description.setText(note.getDescription());
            date.setText(dateFormat.format(new Date(note.getTimestamp())));

            if (note.getPhotoBase64() != null) {
                try {
                    image.setImageBitmap(Api.base64ToBitmap(note.getPhotoBase64()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setVisibility(View.VISIBLE);
            } else {
                image.setVisibility(View.GONE);
            }
        }
    }
}
