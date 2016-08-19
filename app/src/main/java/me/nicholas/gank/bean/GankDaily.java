package me.nicholas.gank.bean;

import java.util.List;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class GankDaily {

    private boolean error;
    private GankList results;
    private List<String> category;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public GankList getResults() {
        return results;
    }

    public void setResults(GankList results) {
        this.results = results;
    }

    public List<String> getCategory() {
        return category;
    }

    public void setCategory(List<String> category) {
        this.category = category;
    }
}
