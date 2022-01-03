package com.emedicoz.app.epubear.view;

import java.util.Map;

/**
 * Created by amykh on 08.09.2016.
 */
public interface IDocumentView {
    public void onDisplayReady();

    public void onDocumentReady();

    public void onUrlClick(String url);

    public void setChapterCaption(String chapter);

    public void setCurrentPage(int page);

    public void setPageCount(int pageCount);

    public void setTitle(String title);

    public void setToCList(Map<String, String> tocList);

    public void invalidate();

    public void onError(String msg);
}
