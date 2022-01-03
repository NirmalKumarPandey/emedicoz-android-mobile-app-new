package com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Deck {

    @SerializedName(Constants.Extras.ID)
    @Expose
    private String id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("subdeck")
@Expose
private List<Subdeck> subdeck = null;

public String getId() {
return id;
}

public void setId(String id) {
this.id = id;
}

public String getTitle() {
return title;
}

public void setTitle(String title) {
this.title = title;
}

public List<Subdeck> getSubdeck() {
return subdeck;
}

public void setSubdeck(List<Subdeck> subdeck) {
this.subdeck = subdeck;
}

}