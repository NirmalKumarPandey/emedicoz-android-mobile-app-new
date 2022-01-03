package com.emedicoz.app.customviews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatRatingBar;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.emedicoz.app.R;
import com.emedicoz.app.courses.fragment.CourseReviews;

public class ReviewDialog extends Dialog {

    AppCompatRatingBar ratingBar;
    TextView ratingComment;
    EditText et_review;
    Button btn_submit, btn_cancel;
    Float ratingStar;
    Context context;
    String review;
    CourseReviews courseReviews;

    public ReviewDialog(@NonNull Context context, CourseReviews courseReviews) {
        super(context);
        this.context = context;
        this.courseReviews = courseReviews;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_review);
        setCancelable(true);
        ratingBar = findViewById(R.id.ratingBar);
        ratingComment = findViewById(R.id.ratingtextTV);
        et_review = findViewById(R.id.writereviewET);
        btn_submit = findViewById(R.id.btn_submit);
        btn_cancel = findViewById(R.id.btn_cancel);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ratingStar = ratingBar.getRating();
                review = et_review.getText().toString();
                if (ratingStar == 0) {

                    Toast.makeText(context, "Please choose rating", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    courseReviews.addReviews(ratingStar);
                    dismiss();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                courseReviews.clearText();
            }
        });

    }
}
