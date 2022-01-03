package com.emedicoz.app.pubnub;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.emedicoz.app.R;

public class PubSubTabContentFragment extends Fragment {
    private PubSubListAdapter psAdapter;
    //RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pubsub, container, false);
        ListView listView = view.findViewById(R.id.message_list);
        listView.setAdapter(psAdapter);
        return view;
    }

    public void setAdapter(PubSubListAdapter psAdapter) {
        this.psAdapter = psAdapter;
    }
}
