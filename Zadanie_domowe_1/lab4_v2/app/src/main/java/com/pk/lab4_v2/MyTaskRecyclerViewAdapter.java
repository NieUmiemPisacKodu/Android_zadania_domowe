package com.pk.lab4_v2;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.pk.lab4_v2.tasks.TaskListContent;


public class MyTaskRecyclerViewAdapter extends RecyclerView.Adapter<MyTaskRecyclerViewAdapter.ViewHolder> {

    private final List<TaskListContent.Task> mValues;
    public TaskFragment.OnListFragmentInteractionListener mListener;
    public MyTaskRecyclerViewAdapter(List<TaskListContent.Task> items, TaskFragment.OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        TaskListContent.Task task = mValues.get(position);
        holder.mItem = task;
        holder.mContentView.setText(task.name + " " + task.surname);
        final String picPath = task.id;

        Context context = holder.mView.getContext();

        Drawable taskDrawable;
        switch (picPath) {
            case "1":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_1);
                break;
            case "2":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_2);
                break;
            case "3":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_3);
                break;
            case "4":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_4);
                break;
            case "5":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_5);
                break;
            case "6":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_6);
                break;
            case "7":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_7);
                break;
            case "8":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_8);
                break;
            case "9":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_9);
                break;
            case "10":
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_10);
                break;
            default:
                taskDrawable = context.getResources().getDrawable(R.drawable.avatar_15);

        }
        holder.mItemImageView.setImageDrawable(taskDrawable);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentClickInteraction(holder.mItem, position);
                }
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mListener.OnListFragmentLongClickInteraction(position);
                return false;
            }
        });

        holder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onDeleteIconClickInteraction(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContentView;
        public final ImageView mItemImageView;
        public final ImageView mDeleteButton;
        public TaskListContent.Task mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemImageView = view.findViewById(R.id.item_image);
            mContentView = view.findViewById(R.id.content);
            mDeleteButton = view.findViewById(R.id.delete_button);
        }

        @Override
        public String toString() {

            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
