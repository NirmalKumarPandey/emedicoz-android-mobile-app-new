package com.emedicoz.app.ui.podcast;

import java.util.List;

/**
 * Created by Shri rAm on 3/3/2016.
 */
public class StoriesList {
 private String title;
  private String id;
    private List<StoryName>  list;


    //  for stories
    private String result;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<StoryName> getList() {
        return list;
    }

    public void setList(List<StoryName> list) {
        this.list = list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
