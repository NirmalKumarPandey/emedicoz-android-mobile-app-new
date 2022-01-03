package com.emedicoz.app.customviews;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emedicoz.app.R;
import com.emedicoz.app.modelo.courses.Review;

import java.util.ArrayList;


/**
 * Created by surya on 2/2/18.
 */

public class SharePopup {
    Context ctx;
    Dialog dialog;
    PopupWindow popupWindow;
    ShareBookingCallback callback;
    ArrayList<View> bookingsView;
    private LinearLayout bookingsLay;
    private ArrayList<Review> trenReviewArrayList;

    public SharePopup(Context ctx, ArrayList<Review> appointmentDetailData, ShareBookingCallback callBack) {
        this.ctx = ctx;
        this.callback = callBack;
        this.trenReviewArrayList = appointmentDetailData;
        bookingsView = new ArrayList<>();
    }

    public void showPopup() {
        // custom dialog
//        dialog = new Dialog(ctx);
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_co_passengers;
//        dialog.setContentView(R.layout.trending_dialog_lay);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);


        // set the custom dialog components - text, image and button
        bookingsLay = dialog.findViewById(R.id.bookingsLay);

        addBookings();

        dialog.show();
    }

    private void addBookings() {

        for (int i = 0; i < trenReviewArrayList.size(); i++) {
            LayoutInflater inflater = ((Activity) ctx).getLayoutInflater();
            View view = inflater.inflate(R.layout.single_row_tag, null);

//            Typeface latoRegular = Typeface.createFromAsset(ctx.getAssets(), "fonts/Lato-Regular.ttf");

            RelativeLayout bookingLay;
            TextView trendingText;

            bookingLay = view.findViewById(R.id.parentLL);
            trendingText = view.findViewById(R.id.nameTV);
            bookingLay.setTag(i);

            trendingText.setText(trenReviewArrayList.get(i).getText());
            final int finalI = i;
            bookingLay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    callback.BookingSelectedCallBack(trenReviewArrayList.get(finalI));
                    dialog.dismiss();
                }
            });

            bookingsLay.addView(view);
            bookingsView.add(view);
        }

    }

    public interface ShareBookingCallback {
        void BookingSelectedCallBack(Review trendingText);

        void CancelCallBack();
    }
}
