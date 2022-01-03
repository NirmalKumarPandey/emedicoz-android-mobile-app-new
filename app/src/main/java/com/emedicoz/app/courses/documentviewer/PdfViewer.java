package com.emedicoz.app.courses.documentviewer;


import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.emedicoz.app.R;
import com.emedicoz.app.utilso.Const;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PdfViewer extends Fragment {
//        implements OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener {

//    OnPageChangeListener, OnLoadCompleteListener, OnPageErrorListener

    //    PDFView pdfView;
    Activity activity;
    String frag_type = "", path = "";
    String SAMPLE_FILE = "sample.pdf";
    Integer pageNumber = 0;
    private ViewPager viewPager;
    private ArrayList<Bitmap> itemData;
//    private VigerAdapter adapter;
//    private VigerPDF vigerPDF;
//    private PDFViewPager pdfViewPager;


    public PdfViewer() {
        // Required empty public constructor
    }

    public static PdfViewer newInstance(String frag_type, String path) {
        PdfViewer pdfViewer = new PdfViewer();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Const.PATH, path);
        bundle.putString(Const.FRAG_TYPE, frag_type);
        pdfViewer.setArguments(bundle);
        return pdfViewer;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        pdfViewPager = new PDFViewPager(activity, path);
//        setContentView(pdfViewPager);

//        viewPager = view.findViewById(R.id.viewPager);
//        itemData = new ArrayList<>();
//        adapter = new VigerAdapter(activity.getApplicationContext(), itemData);
//        viewPager.setAdapter(adapter);
//
//        new VigerPDF(activity)
//                .initFromFile(new File(path), new OnResultListener() {
//                    @Override
//                    public void resultData(Bitmap bitmap) {
//                        itemData.add(bitmap);
//                        adapter.notifyDataSetChanged();
//                    }
//
//                    @Override
//                    public void progressData(int progress) {
//                        Log.e("data", "" + progress);
//                    }
//
//                    @Override
//                    public void failed(Throwable throwable) {
//
//                    }
//                });


//        pdfView = view.findViewById(R.id.pdfView);
//
//        pdfView.fromFile(new File(path))
//                .defaultPage(0)
//                .onPageChange(this)
//                .enableAnnotationRendering(true)
//                .onLoad(this)
//                .scrollHandle(new DefaultScrollHandle(activity))
//                .spacing(10) // in dp
//                .onPageError(this)
//                .load();
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            frag_type = getArguments().getString(Const.FRAG_TYPE);
            path = getArguments().getString(Const.PATH);
        }
        activity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pdf_viewer, container, false);
    }


//    @Override
//    public void onPageChanged(int page, int pageCount) {
//        Log.e("Page Changed", String.format("%s %s / %s", "Sample.pdf", page + 1, pageCount));
//        pageNumber = page;
//        activity.setTitle(String.format("%s %s / %s", "Sample.pdf", page + 1, pageCount));
//    }
//
//    @Override
//    public void loadComplete(int nbPages) {
//        PdfDocument.Meta meta = pdfView.getDocumentMeta();
//        Log.e("PdfViewer", "title = " + meta.getTitle());
//        Log.e("PdfViewer", "author = " + meta.getAuthor());
//        Log.e("PdfViewer", "subject = " + meta.getSubject());
//        Log.e("PdfViewer", "keywords = " + meta.getKeywords());
//        Log.e("PdfViewer", "creator = " + meta.getCreator());
//        Log.e("PdfViewer", "producer = " + meta.getProducer());
//        Log.e("PdfViewer", "creationDate = " + meta.getCreationDate());
//        Log.e("PdfViewer", "modDate = " + meta.getModDate());
//
//        printBookmarksTree(pdfView.getTableOfContents(), "-");
//    }
//
//    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
//        for (PdfDocument.Bookmark b : tree) {
//
//            Log.e("PdfViewer", String.format("%s %s, p %d", sep, b.getTitle(), b.getPageIdx()));
//
//            if (b.hasChildren()) {
//                printBookmarksTree(b.getChildren(), sep + "-");
//            }
//        }
//    }
//
//    @Override
//    public void onPageError(int page, Throwable t) {
//        Log.e("PdfViewer", "Cannot load page " + page);
//    }
}
