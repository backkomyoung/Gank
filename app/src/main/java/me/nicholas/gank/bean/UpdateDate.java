package me.nicholas.gank.bean;

import java.util.List;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class UpdateDate {

    private boolean error;
    private List<String> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }
}
