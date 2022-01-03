import com.google.gson.annotations.SerializedName

/*
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


public data class Books (
		 @SerializedName("id") val id: Int,
          @SerializedName("title") val title: String,
          @SerializedName("description") val description: String,
          @SerializedName("mrp") val mrp: Int,
          @SerializedName("for_dams") val for_dams: Int,
          @SerializedName("non_dams") val non_dams: Int,
          @SerializedName("rating") val rating: Double,
          @SerializedName("course_review_count") val course_review_count: Int,
          @SerializedName("book_type") val book_type: String,
          @SerializedName("brand_name") val brand_name: String,
          @SerializedName("model_name") val model_name: String,
          @SerializedName("article_id") val article_id: Int,
          @SerializedName("image") val image: String,
          @SerializedName("creation_date") val creation_date: Int,
          @SerializedName("created_by") val created_by: String,
          @SerializedName("title_uniqid") val title_uniqid: Int,
          @SerializedName("content") val content: String,
          @SerializedName("subject_tags") val subject_tags: Int,
          @SerializedName("topic_tags") val topic_tags: String,
          @SerializedName("other_tags") val other_tags: String,
          @SerializedName("category") val category: String,


		 @SerializedName("book_tag") val book_tag : Int,
		 @SerializedName("top_trending") val top_trending : Int,
		 @SerializedName("best_seller") val best_seller : Int,
		 @SerializedName("featured_image") val featured_image : String
)