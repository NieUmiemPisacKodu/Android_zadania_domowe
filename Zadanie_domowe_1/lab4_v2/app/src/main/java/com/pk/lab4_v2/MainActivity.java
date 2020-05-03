package com.pk.lab4_v2;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pk.lab4_v2.tasks.TaskListContent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements
        TaskFragment.OnListFragmentInteractionListener,
        DeleteDialog.OnDeleteDialogInteractionListener,
        CallDialog.OnCallDialogInteractionListener
{
    public static final String taskExtra = "taskExtra";
    private int currentItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.add_contact_view);
            }
        });
    }

    //public void goToNewContact

    public void addClick(View view) {
        EditText taskTitleEditTxt = findViewById(R.id.taskName);
        EditText taskDescriptionEditTxt = findViewById(R.id.taskSurname);
        EditText taskBirthdayTxt = findViewById(R.id.taskBirthday);
        EditText taskPhoneNumberTxt = findViewById(R.id.taskPhone);
        String taskTitle = taskTitleEditTxt.getText().toString();
        String taskDescription = taskDescriptionEditTxt.getText().toString();
        String taskBirthday = taskBirthdayTxt.getText().toString();
        String taskPhoneNumber = taskPhoneNumberTxt.getText().toString();

        if(!taskTitle.isEmpty() && !taskDescription.isEmpty() && !taskBirthday.isEmpty() && !taskPhoneNumber.isEmpty() &&
        taskPhoneNumber.length() == 9 && taskBirthday.matches("[0-9][0-9]/[0-9][0-9]/[0-9][0-9][0-9][0-9]")){
            TaskListContent.addItem(new TaskListContent.Task(String.valueOf(TaskListContent.ITEMS.size() + 1),
                    taskTitle,
                    taskDescription,
                    taskBirthday,
                    taskPhoneNumber));
        }

        ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();

        taskTitleEditTxt.setText("");
        taskDescriptionEditTxt.setText("");

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onListFragmentClickInteraction(TaskListContent.Task task, int position) {
        Toast.makeText(this, getString(R.string.item_selected_msg) + position, Toast.LENGTH_SHORT).show();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            displayTaskInFragment(task);
        }else {
            startSecondActivity(task, position);
        }
    }

    @Override
    public void OnListFragmentLongClickInteraction(int position) {
        Toast.makeText(this, getString(R.string.long_click_msg) + position, Toast.LENGTH_SHORT).show();
        showCallDialog();
        currentItemPosition = position;
    }

    @Override
    public void onDeleteIconClickInteraction(int position) {
        showDeleteDialog();
        currentItemPosition = position;
    }

    private void startSecondActivity(TaskListContent.Task task, int position){
        Intent intent = new Intent(this, TaskInfoActivity.class);
        intent.putExtra(taskExtra, task);
        startActivity(intent);
    }

    public void displayTaskInFragment(TaskListContent.Task task) {
        TaskInfoFragment taskInfoFragment = ((TaskInfoFragment) getSupportFragmentManager().findFragmentById(R.id.displayFragment));
        if (taskInfoFragment != null) {
            taskInfoFragment.displayTask(task);
        }
    }

    private void showDeleteDialog(){
        DeleteDialog.newInstance().show(getSupportFragmentManager(),getString(R.string.delete_dialog_tag));
    }

    @Override
    public void onDeleteDialogPositiveClick(DialogFragment dialog) {
        if (currentItemPosition != -1 && currentItemPosition < TaskListContent.ITEMS.size()){
            TaskListContent.removeItem(currentItemPosition);
            ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();
        }
    }

    @Override
    public void onDeleteDialogNegativeClick(DialogFragment dialog) {
        View v = findViewById(R.id.addButton);
        if (v != null) {
            Snackbar.make(v, getString(R.string.delete_cancel_msg), Snackbar.LENGTH_LONG).setAction(getString(R.string.retry_msg), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeleteDialog();
                }
            }).show();
        }
    }

    private void showCallDialog(){
        CallDialog.newInstance().show(getSupportFragmentManager(),getString(R.string.call_dialog_tag));
    }

    @Override
    public void onCallDialogPositiveClick(DialogFragment dialog) {
        View v = findViewById(R.id.fab);
        Snackbar.make(v ,getString(R.string.calling_msg), Snackbar.LENGTH_LONG).setAction(getString(R.string.deny_msg), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        }).show();
    }

    @Override
    public void onCallDialogNegativeClick(DialogFragment dialog) {
        View v = findViewById(R.id.fab);
        Snackbar.make(v, getString(R.string.decline_msg), Snackbar.LENGTH_LONG).setAction(getString(R.string.try_again_msg), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCallDialog();
            }
        }).show();
    }


}
