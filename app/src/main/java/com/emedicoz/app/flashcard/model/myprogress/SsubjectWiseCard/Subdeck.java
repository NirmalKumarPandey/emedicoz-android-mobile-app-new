package com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard;

import com.emedicoz.app.utilso.Constants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Subdeck {

@SerializedName(Constants.Extras.ID)
@Expose
private String id;
@SerializedName("title")
@Expose
private String title;
@SerializedName("deckId")
@Expose
private String deckId;
@SerializedName("total")
@Expose
private Integer total;
@SerializedName("read")
@Expose
private Integer read;

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

public String getDeckId() {
return deckId;
}

public void setDeckId(String deckId) {
this.deckId = deckId;
}

public Integer getTotal() {
return total;
}

public void setTotal(Integer total) {
this.total = total;
}

public Integer getRead() {
return read;
}

public void setRead(Integer read) {
this.read = read;
}

}