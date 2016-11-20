package ru.ezembi.forgetting.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import ru.ezembi.forgetting.Job;
import ru.ezembi.forgetting.R;
import ru.ezembi.forgetting.db.JobDBHelper;

/**
 * Created by Victor on 12.11.2016.
 */
public class ListWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context context;
    private ArrayList<Job> list;
    private JobDBHelper db = null;

    public ListWidgetFactory(Context context){
        this.context = context;
    }

    @Override
    public void onCreate() {
        db = new JobDBHelper(context);
        list = db.select();
    }
    @Override
    public void onDataSetChanged() {
        if(db == null) {
            db = new JobDBHelper(context);
        }
        list = db.select();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        RemoteViews rView = new RemoteViews(
                context.getPackageName(),
                R.layout.job_list_item);

        Job curentJob = list.get(position);

        rView.setTextViewText(R.id.job, curentJob.getJob());
        if(curentJob.isComplete()) {
            rView.setImageViewResource(R.id.completeImage, R.drawable.ic_check_box_white_24dp);
        } else {
            rView.setImageViewResource(R.id.completeImage, R.drawable.ic_check_box_outline_blank_white_24dp);
        }

        Intent clickIntent = new Intent();
        clickIntent.putExtra(JobWidget.ITEM_POSITION, curentJob);
        rView.setOnClickFillInIntent(R.id.jobItem, clickIntent);

        return rView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
