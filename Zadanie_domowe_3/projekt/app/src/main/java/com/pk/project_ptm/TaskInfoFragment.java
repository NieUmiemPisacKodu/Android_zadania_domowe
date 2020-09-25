package com.pk.project_ptm;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.pk.project_ptm.tasks.Phone;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskInfoFragment extends Fragment {

    public TaskInfoFragment() {
        // Required empty public constructor
    }

    public void displayTask(Phone.SinglePhone singlePhone) {
        FragmentActivity activity = getActivity();

        TextView taskInfoBrand = activity.findViewById(R.id.taskInfoBrand);
        TextView taskInfoProdDate = activity.findViewById(R.id.taskInfoProdDate);
        TextView taskInfoPrice = activity.findViewById(R.id.taskInfoPrice);

        taskInfoBrand.setText(singlePhone.brand + " " + singlePhone.model);
        taskInfoProdDate.setText(singlePhone.prod_date);
        taskInfoPrice.setText(singlePhone.price);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_info, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Intent intent = getActivity().getIntent();
        if (intent != null) {
            Phone.SinglePhone receivedSinglePhone = intent.getParcelableExtra(MainActivity.taskExtra);
            if (receivedSinglePhone != null) {
                displayTask(receivedSinglePhone);
            }
        }
    }
}
