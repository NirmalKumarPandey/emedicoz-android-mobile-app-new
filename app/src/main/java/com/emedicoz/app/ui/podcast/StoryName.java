package com.emedicoz.app.ui.podcast;



import java.util.List;

/**
 * Created by Shri rAm on 3/3/2016.
 */
public class StoryName {
   private String type;
    private String text;
    private String id;
    private String color;

    private List<SalesItem> salesItemList;

    //  for stories  paragrapgh
    private String content;

      //   for type  image
    private String image;

       //  for type  story_title
    private  String title;


  //  private boolean isAnswered=false;
    private String selectedOption;



private boolean isAnswered= false;


    // for  game


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }




    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public List<SalesItem> getSalesItemList() {
        return salesItemList;
    }

    public void setSalesItemList(List<SalesItem> salesItemList) {
        this.salesItemList = salesItemList;
    }

    public boolean isAnswered() {
        return isAnswered;
    }

    public void setIsAnswered(boolean answered) {
        isAnswered = answered;
    }
}
