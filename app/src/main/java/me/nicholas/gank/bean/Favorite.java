package me.nicholas.gank.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Nicholas on 2016/8/20.
 */
public class Favorite extends DataSupport {

    private String date;
    private String title;
    private String url;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
