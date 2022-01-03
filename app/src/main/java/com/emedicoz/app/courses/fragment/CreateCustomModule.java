package com.emedicoz.app.courses.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.R;
import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.activity.CourseActivity;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

import java.util.HashMap;

public class CreateCustomModule extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Activity activity;
    View view;
    Spinner spinner;
    // number of questions user can select while creating custom module
    String[] s = {"10", "25", "50", "100"};
    Button allLevelButton;
    Button chooseLevelButton;
    CardView questionTitle1;
    CardView questionTitle2;
    CardView questionTitle3;
    CardView questionTitle4;
    CardView questionTitle5;
    RadioButton radio1;
    RadioButton radio2;
    RadioButton radio3;
    RadioButton radio4;
    RadioButton radio5;
    RadioGroup radioGroup;
    LinearLayout linearRadioGroup;
    HashMap<String, String> finalResponse = new HashMap<>();
    String noOfQues = "";
    String defficultyLvl = "";
    String quesFrom = "";
    TextView text1;
    TextView text2;
    TextView text3;
    TextView text4;
    TextView text5;

    public static CreateCustomModule newInstance() {
        return new CreateCustomModule();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.create_custom_module, container, false);
        initView();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity,
                android.R.layout.simple_spinner_item, s);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        allLevelButton.setSelected(true);
        // difficulty level 1 for easy, 2 for medium, 3 for hard. if "1,2,3" means questions will be in mixture of easy,medium and hard.
        defficultyLvl = "1,2,3";
        allLevelButton.setOnClickListener(this);
        radio1.setChecked(true);
        quesFrom = "1";
        chooseLevelButton.setOnClickListener(this);
        questionTitle1.setOnClickListener(this);
        questionTitle2.setOnClickListener(this);
        questionTitle3.setOnClickListener(this);
        questionTitle4.setOnClickListener(this);
        questionTitle5.setOnClickListener(this);

        ((BaseABNoNavActivity) activity).nextButton.setVisibility(View.VISIBLE);
        ((BaseABNoNavActivity) activity).nextButton.setOnClickListener(v -> {
                if (defficultyLvl.equals(""))
                    Toast.makeText(activity, activity.getResources().getString(R.string.select_difficulty_level), Toast.LENGTH_SHORT).show();
                else {
                    noOfQues = spinner.getSelectedItem().toString();
                    finalResponse.put(Constants.CustomModuleExtras.NUMBER_OF_QUESTION, noOfQues);
                    finalResponse.put(Constants.CustomModuleExtras.DIFFICULTY_LEVEL, defficultyLvl);
                    finalResponse.put(Constants.CustomModuleExtras.QUES_FROM, quesFrom);
                    Log.e("Response", finalResponse.toString());
                    Intent conceIntent = new Intent(activity, CourseActivity.class);
                    conceIntent.putExtra(Const.FRAG_TYPE, Const.SELECT_SUBJECT);
                    conceIntent.putExtra(Const.finalResponse, finalResponse);
                    activity.startActivity(conceIntent);
                }
        });

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.easy:
                        defficultyLvl = "1";
                        break;
                    case R.id.medium:
                        defficultyLvl = "2";
                        break;
                    case R.id.hard:
                        defficultyLvl = "3";
                        break;
                    default:
                        break;
                }
        });
        return view;
    }

    private void initView() {
        spinner = view.findViewById(R.id.spinner);
        allLevelButton = view.findViewById(R.id.all_level_button);
        chooseLevelButton = view.findViewById(R.id.choose_level_button);
        questionTitle1 = view.findViewById(R.id.question_title1);
        questionTitle2 = view.findViewById(R.id.question_title2);
        questionTitle3 = view.findViewById(R.id.question_title3);
        questionTitle4 = view.findViewById(R.id.question_title4);
        questionTitle5 = view.findViewById(R.id.question_title5);
        radio1 = view.findViewById(R.id.radio1);
        radio2 = view.findViewById(R.id.radio2);
        radio3 = view.findViewById(R.id.radio3);
        radio4 = view.findViewById(R.id.radio4);
        radio5 = view.findViewById(R.id.radio5);
        text1 = view.findViewById(R.id.text1);
        text2 = view.findViewById(R.id.text2);
        text3 = view.findViewById(R.id.text3);
        text4 = view.findViewById(R.id.text4);
        text5 = view.findViewById(R.id.text5);

        radioGroup = view.findViewById(R.id.radioGroup);

        linearRadioGroup = view.findViewById(R.id.linear_radio_group);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.all_level_button:
                allLevelButton.setSelected(true);
                chooseLevelButton.setSelected(false);
                linearRadioGroup.setVisibility(View.GONE);
                defficultyLvl = "1,2,3";
                radioGroup.clearCheck();
                break;
            case R.id.choose_level_button:
                allLevelButton.setSelected(false);
                chooseLevelButton.setSelected(true);
                linearRadioGroup.setVisibility(View.VISIBLE);
                defficultyLvl = "";

                break;

            case R.id.question_title1:
                radio1.setChecked(true);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(false);
                quesFrom = "1";

                break;
            case R.id.question_title2:
                radio1.setChecked(false);
                radio2.setChecked(true);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(false);
                quesFrom = "2";

                break;
            case R.id.question_title3:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(true);
                radio4.setChecked(false);
                radio5.setChecked(false);
                quesFrom = "3";
                break;
            case R.id.question_title4:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(true);
                radio5.setChecked(false);
                quesFrom = "4";
                break;
            case R.id.question_title5:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(true);
                quesFrom = "5";
                break;
            default:
                break;

        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.radio1:
                radio1.setChecked(true);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(false);
                break;
            case R.id.radio2:
                radio1.setChecked(false);
                radio2.setChecked(true);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(false);
                break;
            case R.id.radio3:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(true);
                radio4.setChecked(false);
                radio5.setChecked(false);
                break;
            case R.id.radio4:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(true);
                radio5.setChecked(false);
                break;
            case R.id.radio5:
                radio1.setChecked(false);
                radio2.setChecked(false);
                radio3.setChecked(false);
                radio4.setChecked(false);
                radio5.setChecked(true);
                break;
            default:
                break;
        }
    }
}
