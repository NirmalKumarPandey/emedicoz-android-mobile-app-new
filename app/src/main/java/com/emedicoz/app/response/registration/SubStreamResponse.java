package com.emedicoz.app.response.registration;

/**
 * Created by Cbc-03 on 06/07/17.
 */

public class SubStreamResponse extends StreamResponse {
    private String parent_id;

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
}
