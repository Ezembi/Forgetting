package ru.ezembi.forgetting;

import java.io.Serializable;

/**
 * Created by Victor on 06.11.2016.
 */
public class Job implements Serializable {

    private int id;
    private String job;
    private boolean complete;

    public Job(int id, String job, boolean complete) {
        this.id = id;
        this.complete = complete;
        this.job = job;
    }

    public int getId() {
        return id;
    }

    public String getJob() {
        return job;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void setJob(String job) {
        this.job = job;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", job='" + job + '\'' +
                ", complete=" + complete +
                '}';
    }
}
