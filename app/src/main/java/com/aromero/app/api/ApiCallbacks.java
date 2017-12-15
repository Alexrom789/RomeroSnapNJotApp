package com.aromero.app.api;

import com.aromero.app.Note;

import java.util.List;

public interface ApiCallbacks {
    interface LoadCallback {
        void onSuccess(List<Note> notes);

        void onFailure();
    }

    interface CreateCallback {
        void onSuccess(Note note);

        void onFailure();
    }
    interface UpdateCallback {
        void onSuccess(Note note);

        void onFailure();
    }
    interface RemoveCallback {
        void onSuccess(Note note);

        void onFailure();
    }

}
