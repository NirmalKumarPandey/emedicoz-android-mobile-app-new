package com.emedicoz.app.courses.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.emedicoz.app.R;
import com.emedicoz.app.liveCourses.fragments.LiveCoursesFragment;
import com.emedicoz.app.utilso.GenericUtils;

public class LiveCourse extends AppCompatActivity {

    ImageButton backNewCuri;
    TextView toolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GenericUtils.manageScreenShotFeature(this);
        Log.e("onCreate: ", "onCreate method of Live Course");
        setContentView(R.layout.activity_main2);
        backNewCuri = findViewById(R.id.toolbarBackIV);
        toolbarTitle = findViewById(R.id.toolbarTitleTV);
        toolbarTitle.setText(getResources().getString(R.string.live_course));
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                LiveCoursesFragment.newInstance("Live Courses")).commit();
        backNewCuri.setOnClickListener((View view) -> finish());

    }
}
