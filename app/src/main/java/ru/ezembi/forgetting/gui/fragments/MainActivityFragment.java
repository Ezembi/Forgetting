package ru.ezembi.forgetting.gui.fragments;

import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ru.ezembi.forgetting.Job;
import ru.ezembi.forgetting.gui.adapters.JobListAdapter;
import ru.ezembi.forgetting.gui.dialogs.JobDialog;
import ru.ezembi.forgetting.R;
import ru.ezembi.forgetting.db.JobDBHelper;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends BaseFragment {

    public MainActivityFragment() {
    }

    private View view;
    private JobDBHelper db;
    private ListView lvMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);

        // находим список
        lvMain = (ListView) view.findViewById(R.id.jobList);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new AddJobClickListener());

        return view;
    }

    @Override
    public void onResume() {
        db = new JobDBHelper(getContext());
        updateView(db.select());
        super.onResume();
    }

    private void updateView(ArrayList<Job> list){
        // создаем адаптер
        ArrayAdapter<Job> adapter = new JobListAdapter(
                getActivity(),
                R.layout.job_list_item,
                list,
                new LocalDialogClickListener());

        // присваиваем адаптер списку
        lvMain.setAdapter(adapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clear) {
            clearDB();
            updateView(db.select());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void clearDB(){
        ArrayList<Job> list = db.select();

        for (Job job : list){
            if(job.isComplete()){
                db.delete(job);
            }
        }
    }

    private class LocalDialogClickListener implements JobDialog.ClickListener{

        @Override
        public void insert(Job job) {
            if(job.getJob().compareTo("") != 0) {
                db.insert(job);
                updateView(db.select());
            }
        }

        @Override
        public void update(Job job) {
            if(job.getJob().compareTo("") != 0) {
                db.update(job);
                updateView(db.select());
            }
        }

        @Override
        public void ignore() {

        }
    }

    private class AddJobClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            JobDialog dialog = new JobDialog(getContext(), null, new LocalDialogClickListener());
            dialog.show();
        }
    }
}
