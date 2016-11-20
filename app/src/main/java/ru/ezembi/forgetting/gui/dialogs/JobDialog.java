package ru.ezembi.forgetting.gui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;

import ru.ezembi.forgetting.Job;
import ru.ezembi.forgetting.R;

/**
 * Created by Victor on 06.11.2016.
 */
public class JobDialog extends AlertDialog {

    public interface ClickListener {
        void insert(Job job);   //добавление

        void update(Job job);   //изменене

        void ignore();          //ничего не делать
    }

    private AlertDialog.Builder builder;
    final EditText nameJob;

    public JobDialog(Context context, final Job job, final ClickListener click) {

        super(context);
        builder = new AlertDialog.Builder(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final View promptView = layoutInflater.inflate(R.layout.add_job_dialog, null);

        final CheckBox completeBox = (CheckBox) promptView.findViewById(R.id.complete);
        nameJob = (EditText) promptView.findViewById(R.id.jobEditText);

        if (job != null) {
            completeBox.setChecked(job.isComplete());
            nameJob.setText(job.getJob());
            nameJob.setSelection(job.getJob().length());
        } else {
            completeBox.setVisibility(View.GONE);
        }

        builder.setView(promptView);
        builder.setCancelable(false)
                .setPositiveButton(context.getText(R.string.dialog_add_job),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                if (job != null) {
                                    job.setJob(nameJob.getText().toString());
                                    job.setComplete(completeBox.isChecked());
                                    click.update(job);
                                } else {
                                    Job updJob = new Job(
                                            -1,
                                            nameJob.getText().toString(),
                                            completeBox.isChecked()
                                    );
                                    click.insert(updJob);
                                }
                                dialog.cancel();
                            }
                        })
                .setNegativeButton(context.getText(R.string.dialog_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                click.ignore();
                                dialog.cancel();
                            }
                        });
    }

    @Override
    public void show() {
        nameJob.post(new Runnable() {
            @Override
            public void run() {
                nameJob.requestFocus();
                InputMethodManager keyboard = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.showSoftInput(nameJob, 0);
            }
        });

        builder.show();
    }
}
