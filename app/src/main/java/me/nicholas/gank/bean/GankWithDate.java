package me.nicholas.gank.bean;

import java.util.List;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class GankWithDate {

    private String date;
    private String url;
    private List<Gank> ganks;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Gank> getGanks() {
        return ganks;
    }

    public void setGanks(List<Gank> ganks) {
        this.ganks = ganks;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
