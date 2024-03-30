package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetailsActivity extends AppCompatActivity {

    EditText title,content;
    ImageButton saveNotebtn;
    TextView pageTitleView;
    String titleUpdate,contentUpdate,docId;
    boolean isEditMode = false;
    TextView deleteNoteTextbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        title=findViewById(R.id.notes_title_text);
        content=findViewById(R.id.notes_content_text);
        saveNotebtn=findViewById(R.id.save_note_btn);
        pageTitleView=findViewById(R.id.page_title);
        deleteNoteTextbtn=findViewById(R.id.delete_note_text_view);

        titleUpdate=getIntent().getStringExtra("title");
        contentUpdate=getIntent().getStringExtra("content");
        docId=getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEditMode=true;
        }

        title.setText(titleUpdate);
        content.setText(contentUpdate);

        if(isEditMode){
            pageTitleView.setText("Edit Your note");
            deleteNoteTextbtn.setVisibility(View.VISIBLE);
        }

        saveNotebtn.setOnClickListener((v)-> saveNote());

        deleteNoteTextbtn.setOnClickListener((v)->deleteNoteForFirebase());

    }



    private void saveNote() {
        String noteTitle = title.getText().toString();
        String noteContent = content.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            title.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        saveNoteToFirebase(note);
    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEditMode){
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
            documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Utility.showToast(NoteDetailsActivity.this ,"Note Edited Successfully");
                        finish();
                    }
                    else{
                        Utility.showToast(NoteDetailsActivity.this ,"Failed while Editing note");
                    }
                }
            });
        }else{
            documentReference = Utility.getCollectionReferenceForNotes().document();
            documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Utility.showToast(NoteDetailsActivity.this ,"Note Added Successfully");
                        finish();
                    }
                    else{
                        Utility.showToast(NoteDetailsActivity.this ,"Failed while adding note");
                    }
                }
            });
        }




    }
    private void deleteNoteForFirebase() {
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document(docId);

        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailsActivity.this ,"Note deleted Successfully");
                    finish();
                }
                else{
                    Utility.showToast(NoteDetailsActivity.this ,"Failed while deleting note");
                }
            }
        });
    }
}