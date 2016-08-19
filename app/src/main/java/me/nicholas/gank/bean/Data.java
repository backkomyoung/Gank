package me.nicholas.gank.bean;

import java.util.List;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class Data<T> {

    private boolean error;

    private List<T> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }
}
