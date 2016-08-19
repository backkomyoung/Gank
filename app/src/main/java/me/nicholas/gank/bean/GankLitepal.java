package me.nicholas.gank.bean;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicholas on 2016/8/17.
 */
public class GankLitepal extends DataSupport {

    private String date;
    private String url;

    private List<Gank> gankList=new ArrayList<>();

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Gank> getGankList() {
        return DataSupport.findAll(Gank.class);
    }

    public void setGankList(List<Gank> gankList) {
        this.gankList = gankList;
    }
}
