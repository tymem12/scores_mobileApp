package com.example.mean_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mean_v1.db.AppContract;
import com.example.mean_v1.db.AppDbHelper;

import java.util.ArrayList;
import java.util.List;

//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public class MainActivity extends AppCompatActivity {
    private static final String[] subjects = {
            "Engineria Web",
            "Aplicaciones Mobile"
    };

    private static final float[] scores = {
            0.5f, 0.4f,1.5f, 2.0f
    };

    private static final float[] max_score = {
            1.0f, 2.0f,2.0f, 2.0f
    };
    private static final float[] weight = {
            0.2f, 0.35f, 0.35f, 0.5f
    };

    private AppDbHelper dbHelper;
    private SQLiteDatabase db;
    private RecyclerView recyclerView_subjects;
    private EditText editText;

    private SubjectAdapter subjectAdapter;
    private List<Subject> subjectList;

    private void initDb() {
        ContentValues values = new ContentValues();
        for (int i = 0; (i < scores.length - 1) ; ++i) {
            values.put(AppContract.DictEntry.COLUMN_NAME_SUBJECT, subjects[0]);
            values.put(AppContract.DictEntry.COLUMN_NAME_STUDENT_SCORE, scores[i]);
            values.put(AppContract.DictEntry.COLUMN_NAME_MAX_SCORE, max_score[i]);
            values.put(AppContract.DictEntry.COLUMN_NAME_WEIGHT, weight[i]);
            db.insert(AppContract.DictEntry.TABLE_NAME, null, values);

        }
        values.put(AppContract.DictEntry.COLUMN_NAME_SUBJECT, subjects[1]);
        values.put(AppContract.DictEntry.COLUMN_NAME_STUDENT_SCORE, scores[3]);
        values.put(AppContract.DictEntry.COLUMN_NAME_MAX_SCORE, max_score[3]);
        values.put(AppContract.DictEntry.COLUMN_NAME_WEIGHT, weight[3]);
        db.insert(AppContract.DictEntry.TABLE_NAME, null, values);

    }

    private void hideSoftKeyboard(View v) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private void getAllSubjects() {
        String[] projection = {
                AppContract.DictEntry.COLUMN_NAME_SUBJECT,
                "SUM(" + AppContract.DictEntry.COLUMN_NAME_STUDENT_SCORE + " * " +
                        AppContract.DictEntry.COLUMN_NAME_WEIGHT + ") AS TotalScore",
                "SUM(" + AppContract.DictEntry.COLUMN_NAME_MAX_SCORE + " * " +
                        AppContract.DictEntry.COLUMN_NAME_WEIGHT + ") AS TotalMaxScore"
        };

        // No selection string or arguments needed for all subjects
        String groupBy = AppContract.DictEntry.COLUMN_NAME_SUBJECT;

        Cursor cursor = db.query(
                AppContract.DictEntry.TABLE_NAME,
                projection,
                null, // No selection string for all subjects
                null, // No selection arguments for all subjects
                groupBy,
                null,
                null);

        while (cursor.moveToNext()) {
            String subject_str = cursor.getString(
                    cursor.getColumnIndexOrThrow(AppContract.DictEntry.COLUMN_NAME_SUBJECT));
            float totalScore = cursor.getFloat(cursor.getColumnIndexOrThrow("TotalScore"));
            float totalMaxScore = cursor.getFloat(cursor.getColumnIndexOrThrow("TotalMaxScore"));

            Subject subject = new Subject(subject_str, totalScore, totalMaxScore);
            subjectList.add(subject);

            // Use these values as needed, e.g., storing in a list or displaying
        }
        cursor.close();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        subjectList = new ArrayList<>();



        dbHelper = AppDbHelper.getInstance(getApplicationContext());
        db = dbHelper.getWritableDatabase();

        recyclerView_subjects = findViewById(R.id.RecyclerView_subjects);
        editText = findViewById(R.id.editText);

        recyclerView_subjects.setLayoutManager(new LinearLayoutManager(this));


//        textView1 = findViewById(R.id.textView1);
//        textView2 = findViewById(R.id.textView2);


        initDb();
        getAllSubjects();
        subjectAdapter = new SubjectAdapter(subjectList);
        recyclerView_subjects.setAdapter(subjectAdapter);
        subjectAdapter.setOnItemClickListener(new SubjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Subject subject) {
                Intent intent = new Intent(MainActivity.this, ScoresActivity.class);

                // Assuming Subject class has methods to get its properties
                intent.putExtra("SUBJECT_NAME", subject.getName());

                startActivity(intent);
            }
        });






    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}