package com.pk.lab4_v2;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pk.lab4_v2.tasks.TaskListContent;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskInfoFragment extends Fragment {

    public TaskInfoFragment() {
        // Required empty public constructor
    }

    public void displayTask(TaskListContent.Task task){
        FragmentActivity activity = getActivity();

        TextView taskInfoTitle = activity.findViewById(R.id.taskInfoName);
        ImageView taskInfoImage = activity.findViewById(R.id.taskInfoImage);
        TextView taskBirthday = activity.findViewById(R.id.taskInfoBirthday);
        TextView taskPhoneNumber = activity.findViewById(R.id.taskInfoPhoneNumber);
        final String picPath = task.id;

        taskInfoTitle.setText(task.name + " " + task.surname);
        taskBirthday.setText(task.birthday);
        taskPhoneNumber.setText(task.phoneNumber);

        Drawable taskDrawable;
        switch (picPath) {
            case "1":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_1);
                break;
            case "2":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_2);
                break;
            case "3":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_3);
                break;
            case "4":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_4);
                break;
            case "5":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_5);
                break;
            case "6":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_6);
                break;
            case "7":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_7);
                break;
            case "8":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_8);
                break;
            case "9":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_9);
                break;
            case "10":
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_10);
                break;
            default:
                taskDrawable = activity.getResources().getDrawable(R.drawable.avatar_15);
        }
        taskInfoImage.setImageDrawable(taskDrawable);

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
            TaskListContent.Task receivedTask = intent.getParcelableExtra(MainActivity.taskExtra);
            if (receivedTask != null) {
                displayTask(receivedTask);
            }
        }
    }
}
