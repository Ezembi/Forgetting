package ru.ezembi.forgetting.gui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.ezembi.forgetting.Job;
import ru.ezembi.forgetting.R;
import ru.ezembi.forgetting.gui.dialogs.JobDialog;

/**
 * Created by Victor on 06.11.2016.
 * заполнение листа дел
 */
public class JobListAdapter extends ArrayAdapter<Job> {

    private ArrayList<Job> list = new ArrayList<>();
    private Context context;
    private int resource;
    private JobDialog.ClickListener updateListener;

    public JobListAdapter(Context context, int resource, ArrayList<Job> objects, JobDialog.ClickListener updateListener) {
        super(context, resource, objects);

        this.updateListener = updateListener;

        this.context = context;
        this.list = objects;
        this.resource = resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Job curentJob = list.get(position);

        if(convertView != null) {
            rowView = convertView;
        } else {
            rowView = inflater.inflate(resource, parent, false);
        }

        TextView job = (TextView) rowView.findViewById(R.id.job);
        job.setText(curentJob.getJob());
        job.setOnClickListener(new LocalJobClickListener(position));

        ImageView complete = (ImageView) rowView.findViewById(R.id.completeImage);
        if(curentJob.isComplete()){
            complete.setImageResource(R.drawable.ic_check_box_black_24dp);
        } else {
            complete.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp);
        }
        complete.setOnClickListener(new LocalCompleteClickListener(position));

        return rowView;
    }

    private class LocalCompleteClickListener implements View.OnClickListener{

        private int position;

        public LocalCompleteClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Job gob = list.get(position);
            gob.setComplete(!gob.isComplete());
            updateListener.update(gob);
        }
    }

    private class LocalJobClickListener implements View.OnClickListener{

        private int position;

        public LocalJobClickListener(int position){
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            Job gob = list.get(position);
            JobDialog dialog = new JobDialog(getContext(), gob, updateListener);
            dialog.show();
        }
    }
}
