package com.emedicoz.app.feeds.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.emedicoz.app.R;
import com.emedicoz.app.login.fragment.AcademicFragment;
import com.emedicoz.app.login.fragment.ContactDetailFragment;
import com.emedicoz.app.modelo.Country;
import com.emedicoz.app.modelo.State;
import com.emedicoz.app.utilso.Const;

import java.util.List;

public class CountryStateCity extends RecyclerView.Adapter<CountryStateCity.MyViewHolder> {

    Context context;
    Fragment fragment;
    Country country;
    List<Country> countryArrayList;
    List<State> stateArrayList;
    State state;
    String searchType;

    public CountryStateCity(Context context, Fragment fragment, String searchType, List<State> stateArrayList) {
        this.context = context;
        this.fragment = fragment;
        this.searchType = searchType;
        this.stateArrayList = stateArrayList;
    }

    public CountryStateCity(Context context, Fragment fragment, List<Country> countryArrayList) {
        this.context = context;
        this.fragment = fragment;
        this.countryArrayList = countryArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.state_adapter_item, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if (fragment instanceof RegistrationFragment || fragment instanceof ContactDetailFragment || fragment instanceof AcademicFragment) {
            state = stateArrayList.get(i);
            switch (searchType) {
                case Const.COUNTRY:
                    myViewHolder.tvName.setText(state.getName());
                    break;
                case Const.STATE:
                    myViewHolder.tvName.setText(state.getStateName());
                    break;
                case Const.CITY:
                    myViewHolder.tvName.setText(state.getCityName());
                    break;
                case Const.COLLEGE:
                    myViewHolder.tvName.setText(state.getCollegeName());
                    break;
                default:
                    break;
            }
        } else {
            country = countryArrayList.get(i);
            myViewHolder.tvName.setText(country.getName());
        }

        myViewHolder.itemView.setOnClickListener(v -> {
            if (fragment instanceof RegistrationFragment) {
                ((RegistrationFragment) fragment).searchDialog.dismiss();
                switch (searchType) {
                    case Const.COUNTRY:
                        ((RegistrationFragment) fragment).getCountryData(searchType, stateArrayList.get(i).getId(), stateArrayList.get(i).getName());
                        break;
                    case Const.STATE:
                        ((RegistrationFragment) fragment).getStateData(searchType, stateArrayList.get(i).getId(), stateArrayList.get(i).getStateName());
                        break;
                    case Const.CITY:
                        ((RegistrationFragment) fragment).getCityData(searchType, stateArrayList.get(i).getStateId(), stateArrayList.get(i).getId(), stateArrayList.get(i).getCityName());
                        break;
                    case Const.COLLEGE:
                        ((RegistrationFragment) fragment).getCollegeData(searchType, stateArrayList.get(i).getStateId(), stateArrayList.get(i).getCityId(), stateArrayList.get(i).getId(), stateArrayList.get(i).getCollegeName());
                        break;
                    default:
                        break;
                }
            }else if (fragment instanceof ContactDetailFragment){
                ((ContactDetailFragment) fragment).searchDialog.dismiss();
                switch (searchType) {
                    case Const.COUNTRY:
                        ((ContactDetailFragment) fragment).getCountryData(searchType, stateArrayList.get(i).getId(), stateArrayList.get(i).getName());
                        break;
                    case Const.STATE:
                        ((ContactDetailFragment) fragment).getStateData(searchType, stateArrayList.get(i).getId(), stateArrayList.get(i).getStateName());
                        break;
                    case Const.CITY:
                        ((ContactDetailFragment) fragment).getCityData(searchType, stateArrayList.get(i).getStateId(), stateArrayList.get(i).getId(), stateArrayList.get(i).getCityName());
                        break;
                    case Const.COLLEGE:
                        ((ContactDetailFragment) fragment).getCollegeData(searchType, stateArrayList.get(i).getStateId(), stateArrayList.get(i).getCityId(), stateArrayList.get(i).getId(), stateArrayList.get(i).getCollegeName());
                        break;
                    default:
                        break;
                }
            }
            else if (fragment instanceof AcademicFragment){
                ((AcademicFragment) fragment).searchDialog.dismiss();
                switch (searchType) {
                    case Const.COUNTRY:
                        ((AcademicFragment) fragment).getCountryData(searchType, stateArrayList.get(i).getId(), stateArrayList.get(i).getName());
                        break;
                    case Const.STATE:
                        ((AcademicFragment) fragment).getStateData(searchType, stateArrayList.get(i).getId(), stateArrayList.get(i).getStateName());
                        break;
                    case Const.CITY:
                        ((AcademicFragment) fragment).getCityData(searchType, stateArrayList.get(i).getStateId(), stateArrayList.get(i).getId(), stateArrayList.get(i).getCityName());
                        break;
                    case Const.COLLEGE:
                        ((AcademicFragment) fragment).getCollegeData(searchType, stateArrayList.get(i).getStateId(), stateArrayList.get(i).getCityId(), stateArrayList.get(i).getId(), stateArrayList.get(i).getCollegeName());
                        break;
                    default:
                        break;
                }
            }
            else {
                ((CollegeFragment) fragment).searchDialog.dismiss();
                ((CollegeFragment) fragment).getCountryData(countryArrayList.get(i).getId(), countryArrayList.get(i).getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (fragment instanceof RegistrationFragment || fragment instanceof ContactDetailFragment || fragment instanceof AcademicFragment)
            return stateArrayList.size();
        else
            return countryArrayList.size();
    }

    public void filterList(List<State> newemployeeList) {
        stateArrayList = newemployeeList;
        notifyDataSetChanged();
    }

    public void filterCountryList(List<Country> newCountryArrayList) {
        countryArrayList = newCountryArrayList;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.nameTv);
        }
    }
}

