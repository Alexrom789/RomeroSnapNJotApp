package com.aromero.app.view.note;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aromero.app.App;
import com.aromero.app.R;
import com.aromero.app.api.Api;
import com.aromero.app.api.ApiCallbacks;
import com.aromero.app.Note;

import java.util.List;

import static com.aromero.app.view.note.ManageNoteActivity.EXTRA_NOTE;

public class NotesFragment extends Fragment implements NoteOptionCallback {

    public static final String ACTION_NOTE_UPDATE = "action_update";
    public static final String ACTION_NOTE_REMOVE = "action_remove";
    public static final String ACTION_NOTE_CREATE = "action_create";

    private Api api;

    private RecyclerView mNotesList;

    private NotesRecyclerAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        api = ((App) context.getApplicationContext()).getApi();


        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_NOTE_CREATE);
        filter.addAction(ACTION_NOTE_REMOVE);
        filter.addAction(ACTION_NOTE_UPDATE);

        Activity activity = getActivity();
        if (activity != null) {
            activity.registerReceiver(notesBroadcastReceiver, filter);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        mNotesList = (RecyclerView) view.findViewById(R.id.notes_recycler);


        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if (activity != null) {
            activity.unregisterReceiver(notesBroadcastReceiver);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAdapter = new NotesRecyclerAdapter(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                false);

        mNotesList.setLayoutManager(layoutManager);
        mNotesList.setAdapter(mAdapter);

        load();
    }

    private void load() {
        api.loadNotes(loadCallback);
    }

    private ApiCallbacks.LoadCallback loadCallback = new ApiCallbacks.LoadCallback() {
        @Override
        public void onSuccess(List<Note> notes) {
            mAdapter.addNotes(notes);
        }

        @Override
        public void onFailure() {

        }
    };

    private ApiCallbacks.RemoveCallback removeCallback = new ApiCallbacks.RemoveCallback() {
        @Override
        public void onSuccess(Note note) {
            Intent intent = new Intent(ACTION_NOTE_REMOVE);
            intent.putExtra(EXTRA_NOTE, note);

            Activity activity = getActivity();
            if (activity != null) {
                activity.sendBroadcast(intent);
            }
        }

        @Override
        public void onFailure() {

        }
    };

    private BroadcastReceiver notesBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null || mAdapter == null) return;
            Note note = (Note) intent.getSerializableExtra(EXTRA_NOTE);
            switch (intent.getAction()) {
                case ACTION_NOTE_CREATE:
                    mAdapter.addNote(note);
                    break;
                case ACTION_NOTE_REMOVE:
                    mAdapter.removeNote(note);
                    break;
                case ACTION_NOTE_UPDATE:
                    mAdapter.updateNote(note);
                    break;
            }
        }
    };

    @Override
    public void editOption(Note note) {
        Intent intent = new Intent(getActivity(), ManageNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);

        startActivity(intent);
    }

    @Override
    public void deleteOption(Note note) {
        api.removeNote(removeCallback, note);
    }
}
