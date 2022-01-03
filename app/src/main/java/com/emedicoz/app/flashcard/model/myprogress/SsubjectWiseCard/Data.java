package com.emedicoz.app.flashcard.model.myprogress.SsubjectWiseCard;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("total_card")
@Expose
private Integer totalCard;
@SerializedName("read_card")
@Expose
private Integer readCard;
@SerializedName("decks")
@Expose
private List<Deck> decks = null;

public Integer getTotalCard() {
return totalCard;
}

public void setTotalCard(Integer totalCard) {
this.totalCard = totalCard;
}

public Integer getReadCard() {
return readCard;
}

public void setReadCard(Integer readCard) {
this.readCard = readCard;
}

public List<Deck> getDecks() {
return decks;
}

public void setDecks(List<Deck> decks) {
this.decks = decks;
}

}