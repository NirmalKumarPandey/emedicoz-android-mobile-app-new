package com.emedicoz.app.courses.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Constants;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuizResultAwait extends Fragment {

    TextView testArrivalDate;
    String resultDate;

    public QuizResultAwait() {
        // Required empty public constructor
    }

    public static QuizResultAwait newInstance(String resultDate) {
        QuizResultAwait quizResultAwait = new QuizResultAwait();
        Bundle args = new Bundle();
        args.putString(Constants.Extras.DATE, resultDate);
        quizResultAwait.setArguments(args);
        return quizResultAwait;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_result_await, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        testArrivalDate = view.findViewById(R.id.testArrivalDate);
        if (getArguments() != null) {
            resultDate = getArguments().getString(Constants.Extras.DATE);
        }
        Log.e("DATE", resultDate);

        Date d = new Date(Long.parseLong(resultDate) * 1000);
        DateFormat f = new SimpleDateFormat("dd:MM:yyyy hh.mm aa");
        String dateString = f.format(d);
        Date date;
        try {
            date = f.parse(f.format(d));


            String[] amPM = dateString.split("\\s+");

            String[] fullDate = String.valueOf(date).split("\\s+");
            String correctDateFormat = fullDate[0] + ", " + fullDate[1] + " " + fullDate[2] + ", " + fullDate[5] + " at " + amPM[1] + " " + amPM[2];
            testArrivalDate.setText(correctDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
