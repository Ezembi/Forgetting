package ru.ezembi.forgetting.widget;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

/**
 * Created by Victor on 12.11.2016.
 */
public class JobService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListWidgetFactory(getApplicationContext());
    }
}
