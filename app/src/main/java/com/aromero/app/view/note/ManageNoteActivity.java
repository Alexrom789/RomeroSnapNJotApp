package com.aromero.app.view.note;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aromero.app.App;
import com.aromero.app.R;
import com.aromero.app.api.Api;
import com.aromero.app.api.ApiCallbacks;
import com.aromero.app.Note;

import java.io.IOException;

public class ManageNoteActivity extends AppCompatActivity {

    public static final String EXTRA_NOTE = "extra_note";
    private static final int RC_IMAGE_CAPTURE = 102;

    private Api api;

    TextView mContent;
    TextView mDescription;
    TextView mAddImage;

    Button mSave;
    Button mCancel;

    ImageView mImage;

    private Note note;

    private Bitmap lastBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage);
        api = ((App) getApplication()).getApi();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImage = (ImageView) findViewById(R.id.manage_image);
        mAddImage = (TextView) findViewById(R.id.manage_photoCaption);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        mContent = (TextView) findViewById(R.id.manage_content);
        mDescription = (TextView) findViewById(R.id.manage_description);

        mSave = (Button) findViewById(R.id.manage_save);
        mCancel = (Button) findViewById(R.id.manage_cancel);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_NOTE)) {
            note = (Note) intent.getSerializableExtra(EXTRA_NOTE);
            populateViews(note);
        }

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSaveClick();
            }
        });
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void onSaveClick() {
        if (!checkValidity()) return;

        String content = mContent.getText().toString();
        String description = mDescription.getText().toString();

        Note localNote;
        if (note != null) {
            localNote = new Note(content, description, Api.bitmapToBase64(lastBitmap), note.getTimestamp());
            api.updateNote(updateCallback, localNote);
        } else {
            localNote = new Note(content, description, Api.bitmapToBase64(lastBitmap), System.currentTimeMillis());
            api.createNote(createCallback, localNote);
        }
        mSave.setEnabled(false);
    }


    public void launchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, RC_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                lastBitmap = (Bitmap) extras.get("data");

                mAddImage.setVisibility(View.GONE);
                mImage.setImageBitmap(lastBitmap);
            }
        }
    }


    private boolean checkValidity() {
        if (mContent.length() < 1) {
            mContent.setError(getString(R.string.cant_be_empty));
            return false;
        }

        if (mDescription.length() < 1) {
            mDescription.setError(getString(R.string.cant_be_empty));
            return false;
        }

        return true;
    }


    private void populateViews(Note note) {
        mContent.setText(note.getTitle());
        mDescription.setText(note.getDescription());

        if (note.getPhotoBase64() != null) {
            try {
                lastBitmap = Api.base64ToBitmap(note.getPhotoBase64());
                mAddImage.setVisibility(View.GONE);
                mImage.setImageBitmap(lastBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private ApiCallbacks.UpdateCallback updateCallback = new ApiCallbacks.UpdateCallback() {
        @Override
        public void onSuccess(Note note) {
            mSave.setEnabled(true);

            Intent intent = new Intent(NotesFragment.ACTION_NOTE_UPDATE);
            intent.putExtra(EXTRA_NOTE, note);

            sendBroadcast(intent);
            finish();
        }

        @Override
        public void onFailure() {
            mSave.setEnabled(true);
        }
    };

    private ApiCallbacks.CreateCallback createCallback = new ApiCallbacks.CreateCallback() {
        @Override
        public void onSuccess(Note note) {
            mSave.setEnabled(true);

            Intent intent = new Intent(NotesFragment.ACTION_NOTE_CREATE);
            intent.putExtra(EXTRA_NOTE, note);

            sendBroadcast(intent);
            finish();
        }

        @Override
        public void onFailure() {
            mSave.setEnabled(true);
        }
    };

}
