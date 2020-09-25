package com.pk.project_ptm.tasks;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class Phone {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<SinglePhone> ITEMS = new ArrayList<SinglePhone>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, SinglePhone> ITEM_MAP = new HashMap<String, SinglePhone>();

    private static final String TAG = "deletePhone";


    public static void addItem(SinglePhone item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    public static void removeItem(int position) {
        String itemID = ITEMS.get(position).id;
        ITEMS.remove(position);
        ITEM_MAP.remove(itemID);
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("telefony").document(itemID)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class SinglePhone implements Parcelable {
        public String id;
        public String brand;
        public String model;
        public String prod_date;
        public String price;


        public SinglePhone(String id, String brand, String model, String prod_date, String price) {
            this.id = id;
            this.brand = brand;
            this.model = model;
            this.prod_date = prod_date;
            this.price = price;

        }

        protected SinglePhone(Parcel in) {
            id = in.readString();
            brand = in.readString();
            model = in.readString();
            prod_date = in.readString();
            price = in.readString();
        }

        public static final Creator<SinglePhone> CREATOR = new Creator<SinglePhone>() {
            @Override
            public SinglePhone createFromParcel(Parcel in) {
                return new SinglePhone(in);
            }

            @Override
            public SinglePhone[] newArray(int size) {
                return new SinglePhone[size];
            }
        };

        @Override
        public String toString() {
            return brand;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(id);
            dest.writeString(brand);
            dest.writeString(model);
            dest.writeString(prod_date);
            dest.writeString(price);
        }
    }
}
