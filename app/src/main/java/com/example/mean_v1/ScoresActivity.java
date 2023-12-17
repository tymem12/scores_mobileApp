package com.example.mean_v1;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mean_v1.db.AppContract;
import com.example.mean_v1.db.AppDbHelper;

import java.util.ArrayList;
import java.util.List;

public class ScoresActivity extends AppCompatActivity {

    List<Score> scoreList;
    private AppDbHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerView recyclerView;
    private Button button;

    private ScoreAdapter scoreAdapter;

    private void getAllScores(String subject_name) {

        String selection = AppContract.DictEntry.COLUMN_NAME_SUBJECT + " = ?";
        String[] selectionArgs = { subject_name };

        Cursor cursor = db.query(
                AppContract.DictEntry.TABLE_NAME,
                null, // null projections returns all columns
                selection,
                selectionArgs,
                null,
                null,
                null);

        while (cursor.moveToNext()) {
            float studentScore = cursor.getFloat(cursor.getColumnIndexOrThrow(AppContract.DictEntry.COLUMN_NAME_STUDENT_SCORE));
            float maxScore = cursor.getFloat(cursor.getColumnIndexOrThrow(AppContract.DictEntry.COLUMN_NAME_MAX_SCORE));
            float weight = cursor.getFloat(cursor.getColumnIndexOrThrow(AppContract.DictEntry.COLUMN_NAME_WEIGHT));

            Score score = new Score(studentScore, maxScore, weight);
            scoreList.add(score);
        }
        cursor.close();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scores_view);

        scoreList = new ArrayList<>();

        // Get the data passed from MainActivity
        String subjectName = getIntent().getStringExtra("SUBJECT_NAME");
        dbHelper = AppDbHelper.getInstance(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        recyclerView = findViewById(R.id.recyclerView);
        button = findViewById(R.id.button);
        //button.setOnClickListener(v -> showFormDialog());


        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        getAllScores(subjectName);
        scoreAdapter = new ScoreAdapter(scoreList);
        recyclerView.setAdapter(scoreAdapter);

    }


    private void showFormDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.pop_up_form, null);
        builder.setView(dialogView);

        EditText editTextYourScore = dialogView.findViewById(R.id.editTextYourScore);
        EditText editTextMaxScore = dialogView.findViewById(R.id.editTextMaxScore);
        EditText editTextWeight = dialogView.findViewById(R.id.editTextWeight);

        builder.setPositiveButton("Accept", (dialog, which) -> {
            float yourScore = Float.parseFloat(editTextYourScore.getText().toString());
            float maxScore = Float.parseFloat(editTextMaxScore.getText().toString());
            float weight = Float.parseFloat(editTextWeight.getText().toString());

            // Perform action with these values
            performAction(yourScore, maxScore, weight);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void performAction(float yourScore, float maxScore, float weight) {
        // Implement the action to be performed with the form values
    }

}