package com.emedicoz.app.modelo.courses;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by app on 23/11/17.
 */

public class File_Meta_Type implements Serializable {

    String filetype;
    ArrayList<Curriculam.File_meta> fileMetaArrayList;

    public String getFiletype() {
        return filetype;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public ArrayList<Curriculam.File_meta> getFileMetaArrayList() {
        return fileMetaArrayList;
    }

    public void setFileMetaArrayList(ArrayList<Curriculam.File_meta> fileMetaArrayList) {
        this.fileMetaArrayList = fileMetaArrayList;
    }

    public void addFileMetaType(Curriculam.File_meta fileMeta) {
        if (fileMetaArrayList != null && fileMetaArrayList.size() > 0)
            fileMetaArrayList.add(fileMeta);
        else {
            fileMetaArrayList = new ArrayList<>();
            fileMetaArrayList.add(fileMeta);
        }
    }
}
