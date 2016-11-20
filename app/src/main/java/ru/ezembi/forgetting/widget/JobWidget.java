package ru.ezembi.forgetting.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.job.*;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.util.ArrayList;

import ru.ezembi.forgetting.Job;
import ru.ezembi.forgetting.R;
import ru.ezembi.forgetting.db.JobDBHelper;
import ru.ezembi.forgetting.gui.dialogs.JobDialog;

/**
 * Implementation of App Widget functionality.
 */
public class JobWidget extends AppWidgetProvider {

    final String ACTION_ON_CLICK = "LIST_ITEM_CLICK";
    final String ACTION_CLEAR = "LIST_CLEAR";
    final static String ITEM_POSITION = "POSITION";
    public static String UPDATE_JOB_TIMER = "ActionUpdateJobTimer";
    public static String START = "start";
    public static String STOP = "stop";
    private int time = 1000; // время обновления инфы (1000 * 60 * 5 = 5 min)

    void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            // Construct the RemoteViews object
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.job_widget);



            Intent adapter = new Intent(context, JobService.class);
            adapter.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            views.setRemoteAdapter(R.id.jobList, adapter);

            setListClick(views, context, appWidgetId);
            setListClickClear(views, context, appWidgetId);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.jobList);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        updateAppWidget(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);

        Log.e("onReceive", intent +"");

        ComponentName thisAppWidget = new ComponentName(context.getPackageName(), getClass().getName());
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int ids[] = appWidgetManager.getAppWidgetIds(thisAppWidget);

        if (intent.getAction().equalsIgnoreCase(ACTION_ON_CLICK)) {
            Job job = (Job)intent.getSerializableExtra(ITEM_POSITION);
            if (job != null) {
                job.setComplete(!job.isComplete());
                new LocalDialogClickListener(context, appWidgetManager, ids).update(job);
            }
        }

        if (intent.getAction().equalsIgnoreCase(UPDATE_JOB_TIMER)) {
                updateAppWidget(context, appWidgetManager, ids);
        }

        if (intent.getAction().equalsIgnoreCase(ACTION_CLEAR)) {
            new LocalDialogClickListener(context, appWidgetManager, ids).clear();
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
        }
    }

    @Override
    public void onEnabled(Context context) {
        onOffTimer(context, START);
        super.onEnabled(context);
        // Enter relevant functionality for when the first widget is created
    }

    private void setListClick(RemoteViews rv, Context context, int appWidgetId) {
        Intent listClickIntent = new Intent(context, JobWidget.class);
        listClickIntent.setAction(ACTION_ON_CLICK);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                listClickIntent, 0);
        rv.setPendingIntentTemplate(R.id.jobList, listClickPIntent);
    }

    private void setListClickClear(RemoteViews rv, Context context, int appWidgetId) {
        Intent clearIntent = new Intent(context, JobWidget.class);
        clearIntent.setAction(ACTION_CLEAR);
        PendingIntent listClickPIntent = PendingIntent.getBroadcast(context, 0,
                clearIntent, 0);
        rv.setPendingIntentTemplate(R.id.top, listClickPIntent);
    }

    @Override
    public void onDisabled(Context context) {
        //отключам таймер, чтобы в фоне не работал
        onOffTimer(context, STOP);
        super.onDisabled(context);
    }

    private void onOffTimer(Context context, String action) {
        Intent timer = new Intent(context, JobWidget.class);
        timer.setAction(UPDATE_JOB_TIMER);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, timer, 0);
        AlarmManager aManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (action.equals(START)) {
            aManager.setRepeating(aManager.RTC, System.currentTimeMillis(), time, pIntent);
        } else if (action.equals(STOP)) {
            aManager.cancel(pIntent);
        }
    }

    private class LocalDialogClickListener implements JobDialog.ClickListener{

        private Context context;
        private JobDBHelper db;
        private AppWidgetManager appWidgetManager;
        private int ids[];

        public LocalDialogClickListener(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
            this.context = context;
            this.db = new JobDBHelper(context);
            this.appWidgetManager = appWidgetManager;
            this.ids = appWidgetIds;
        }

        @Override
        public void insert(Job job) {
            if(job.getJob().compareTo("") != 0) {
                db.insert(job);
                updateAppWidget(context, appWidgetManager, ids);
            }
        }

        @Override
        public void update(Job job) {
            if(job.getJob().compareTo("") != 0) {
                db.update(job);
                updateAppWidget(context, appWidgetManager, ids);
            }
        }

        @Override
        public void ignore() {

        }

        public void clear(){
            ArrayList<Job> list = db.select();

            for (Job job : list){
                if(job.isComplete()){
                    db.delete(job);
                }
            }
        }
    }
}

