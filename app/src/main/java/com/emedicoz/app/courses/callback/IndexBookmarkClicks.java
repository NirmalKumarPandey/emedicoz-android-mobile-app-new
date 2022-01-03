package com.emedicoz.app.courses.callback;

import com.emedicoz.app.modelo.courses.Bookmark;

public interface IndexBookmarkClicks {

    void onSeek(Bookmark data);

    void onDelete(Bookmark data);

}