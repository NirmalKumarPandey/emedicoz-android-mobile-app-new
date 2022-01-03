package com.emedicoz.app.testmodule.adapter;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.testmodule.activity.TestBaseActivity;
import com.emedicoz.app.testmodule.model.TrueFalse;

import java.util.ArrayList;

public class TrueFalseAdapter extends RecyclerView.Adapter<TrueFalseAdapter.TrueFalseViewHolder> {

    Activity activity;
    ArrayList<TrueFalse> trueFalseArrayList;
    ArrayList<String> answerArrayList;
    RadioClickedInterface radioClickedInterface;
    int pagerPosition;

    /*String str="";*/
    public TrueFalseAdapter(Activity activity, ArrayList<TrueFalse> trueFalseArrayList, RadioClickedInterface radioClickedInterface, int pagerPosition, ArrayList<String> tfResponseArrayList) {
        this.activity = activity;
        this.trueFalseArrayList = trueFalseArrayList;
        this.radioClickedInterface = radioClickedInterface;
        this.pagerPosition = pagerPosition;
        if (tfResponseArrayList != null && tfResponseArrayList.size() > 0) {
            this.answerArrayList = tfResponseArrayList;
        } else {
            answerArrayList = new ArrayList<>();
            for (int i = 0; i < trueFalseArrayList.size(); i++) {
                answerArrayList.add("-");
            }
        }
    }

    @NonNull
    @Override
    public TrueFalseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_option_radio_view, viewGroup, false);
        return new TrueFalseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrueFalseViewHolder holder, final int position) {

        holder.textOptionRadio.setText(trueFalseArrayList.get(position).getOptionSerial() + ". " + trueFalseArrayList.get(position).getOptionText());

        if (answerArrayList.get(position).equals("0")) {
            holder.falseRB.setChecked(true);
        } else if (answerArrayList.get(position).equals("1")) {
            holder.trueRB.setChecked(true);
        } else {
            holder.trueRB.setChecked(false);
            holder.falseRB.setChecked(false);
        }

        holder.myRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {

                RadioButton checkedRadioButton = group.findViewById(checkedId);
                boolean isChecked = checkedRadioButton.isChecked();
                switch (position) {
                    case 0:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(0, "1");
                            } else {
                                answerArrayList.set(0, "0");
                            }
                        }
                        break;

                    case 1:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(1, "1");
                            } else {
                                answerArrayList.set(1, "0");
                            }
                        }
                        break;

                    case 2:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(2, "1");
                            } else {
                                answerArrayList.set(2, "0");
                            }
                        }
                        break;

                    case 3:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(3, "1");
                            } else {
                                answerArrayList.set(3, "0");
                            }
                        }
                        break;

                    case 4:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(4, "1");
                            } else {
                                answerArrayList.set(4, "0");
                            }
                        }
                        break;
                    case 5:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(5, "1");
                            } else {
                                answerArrayList.set(5, "0");
                            }
                        }
                        break;
                    case 6:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(6, "1");
                            } else {
                                answerArrayList.set(6, "0");
                            }
                        }
                        break;
                    case 7:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(7, "1");
                            } else {
                                answerArrayList.set(7, "0");
                            }
                        }
                        break;
                    case 8:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(8, "1");
                            } else {
                                answerArrayList.set(8, "0");
                            }
                        }
                        break;
                    case 9:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(9, "1");
                            } else {
                                answerArrayList.set(9, "0");
                            }
                        }
                        break;
                    case 10:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(10, "1");
                            } else {
                                answerArrayList.set(10, "0");
                            }
                        }
                        break;
                    case 11:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(11, "1");
                            } else {
                                answerArrayList.set(11, "0");
                            }
                        }
                        break;
                    case 12:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(12, "1");
                            } else {
                                answerArrayList.set(12, "0");
                            }
                        }
                        break;
                    case 13:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(13, "1");
                            } else {
                                answerArrayList.set(13, "0");
                            }
                        }
                        break;
                    case 14:
                        if (isChecked) {
                            if (checkedRadioButton.getText().equals("True")) {
                                answerArrayList.set(14, "1");
                            } else {
                                answerArrayList.set(14, "0");
                            }
                        }
                        break;
                    default:
                        break;
                }

                String answerPosition = "";
                for (int i = 0; i < answerArrayList.size(); i++) {
                    answerPosition = answerPosition + answerArrayList.get(i) + ",";
                }
                ((TestBaseActivity) activity).questionBankList.get(pagerPosition).setTfAnswerArrayList(answerArrayList);
                ((TestBaseActivity) activity).questionBankList.get(pagerPosition).setIsMultipleAnswer(true, answerPosition);
        });
    }

    @Override
    public int getItemCount() {
        return trueFalseArrayList.size();
    }

    public interface RadioClickedInterface {
        void onRadioClicked(RadioGroup radioGroup, int checkedId);
    }

    public class TrueFalseViewHolder extends RecyclerView.ViewHolder {

        TextView textOptionRadio;
        RadioGroup myRadioGroup;
        RadioButton trueRB, falseRB;

        public TrueFalseViewHolder(@NonNull View itemView) {
            super(itemView);
            textOptionRadio = itemView.findViewById(R.id.textOptionRadio);
            myRadioGroup = itemView.findViewById(R.id.myRadioGroup);
            trueRB = itemView.findViewById(R.id.trueRB);
            falseRB = itemView.findViewById(R.id.falseRB);
        }
    }
}
