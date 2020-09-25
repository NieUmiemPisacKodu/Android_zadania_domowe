package com.pk.project_ptm;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pk.project_ptm.tasks.Phone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements
        TaskFragment.OnListFragmentInteractionListener,
        DeleteDialog.OnDeleteDialogInteractionListener {
    public static final String taskExtra = "taskExtra";
    private static final String TAG = "MainActivity";
    private int currentItemPosition = -1;
    // Access a Cloud Firestore instance from your Activity
    FirebaseFirestore db = FirebaseFirestore.getInstance();

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

    @Override
    protected void onResume() {
        super.onResume();
        showDatabaseContent();
    }

    public void addClick(View view) {

        EditText taskBrandEditTxt = findViewById(R.id.taskBrand);
        EditText taskModelEditTxt = findViewById(R.id.taskModel);
        EditText taskProdDateEditTxt = findViewById(R.id.taskProdDate);
        EditText taskPriceEditTxt = findViewById(R.id.taskPrice);
        String taskBrand = taskBrandEditTxt.getText().toString();
        String taskModel = taskModelEditTxt.getText().toString();
        String taskProdDate = taskProdDateEditTxt.getText().toString();
        String taskPrice = taskPriceEditTxt.getText().toString();

        if (!taskBrand.isEmpty() && !taskModel.isEmpty() && !taskProdDate.isEmpty() && !taskPrice.isEmpty()) {


            db.collection("telefony")
                    .add(new Phone.SinglePhone("",
                            taskBrand,
                            taskModel,
                            taskProdDate,
                            taskPrice))
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });


            ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void showDatabaseContent() {
        Phone.ITEMS.clear();
        Phone.ITEM_MAP.clear();
        db.collection("telefony")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Phone.SinglePhone phone = new Phone.SinglePhone(document.getId(), (String) document.getData().get("brand"), (String) document.getData().get("model"),
                                        (String) document.getData().get("prod_date"), (String) document.getData().get("price"));
                                Phone.addItem(phone);


                            }
                            ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onListFragmentClickInteraction(Phone.SinglePhone singlePhone, int position) {
        Toast.makeText(this, getString(R.string.item_selected_msg) + position, Toast.LENGTH_SHORT).show();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            displayTaskInFragment(singlePhone);
        } else {
            startSecondActivity(singlePhone, position);
        }
    }

    @Override
    public void OnListFragmentLongClickInteraction(Phone.SinglePhone mItem, int position) {
        showUpdateDialog(mItem);
        currentItemPosition = position;
    }

    @Override
    public void onDeleteIconClickInteraction(int position) {
        showDeleteDialog();
        currentItemPosition = position;
    }

    private void startSecondActivity(Phone.SinglePhone singlePhone, int position) {
        Intent intent = new Intent(this, TaskInfoActivity.class);
        intent.putExtra(taskExtra, singlePhone);
        startActivity(intent);
    }

    public void displayTaskInFragment(Phone.SinglePhone singlePhone) {
        TaskInfoFragment taskInfoFragment = ((TaskInfoFragment) getSupportFragmentManager().findFragmentById(R.id.displayFragment));
        if (taskInfoFragment != null) {
            taskInfoFragment.displayTask(singlePhone);
        }
    }

    private void showDeleteDialog() {
        DeleteDialog.newInstance().show(getSupportFragmentManager(), getString(R.string.delete_dialog_tag));
    }

    @Override
    public void onDeleteDialogPositiveClick(DialogFragment dialog) {
        if (currentItemPosition != -1 && currentItemPosition < Phone.ITEMS.size()) {
            Phone.removeItem(currentItemPosition);
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

    private void showUpdateDialog(Phone.SinglePhone phone) {
        setContentView(R.layout.activity_update_phone);
        EditText updateBrand = findViewById(R.id.taskBrand);
        EditText updateModel = findViewById(R.id.taskModel);
        EditText updateProdDate = findViewById(R.id.taskProdDate);
        EditText updatePrice = findViewById(R.id.taskPrice);

        updateBrand.setText(String.format("%s", phone.brand));
        updateModel.setText(String.format("%s", phone.model));
        updateProdDate.setText(String.format("%s", phone.prod_date));
        updatePrice.setText(String.format("%s", phone.price));

    }


    public void updateClick(View view) {
        String stringID = Phone.ITEMS.get(currentItemPosition).id;

        EditText taskBrandEditTxt = findViewById(R.id.taskBrand);
        EditText taskModelEditTxt = findViewById(R.id.taskModel);
        EditText taskProdDateEditTxt = findViewById(R.id.taskProdDate);
        EditText taskPriceEditTxt = findViewById(R.id.taskPrice);
        String taskBrand = taskBrandEditTxt.getText().toString();
        String taskModel = taskModelEditTxt.getText().toString();
        String taskProdDate = taskProdDateEditTxt.getText().toString();
        String taskPrice = taskPriceEditTxt.getText().toString();

        if (!taskBrand.isEmpty() && !taskModel.isEmpty() && !taskProdDate.isEmpty() && !taskPrice.isEmpty()) {

            db.collection("telefony").document(stringID)
                    .update("brand", taskBrand,
                            "model", taskModel,
                            "prod_date", taskProdDate,
                            "price", taskPrice)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });


            ((TaskFragment) getSupportFragmentManager().findFragmentById(R.id.taskFragment)).notifyDataChange();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}
