package com.emedicoz.app.courses.activity;

import android.text.TextUtils;
import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.emedicoz.app.common.BaseABNoNavActivity;
import com.emedicoz.app.courses.adapter.NavigationQuizAdapter;
import com.emedicoz.app.courses.fragment.GraphFragment;
import com.emedicoz.app.courses.fragment.LeaderBoardFragment;
import com.emedicoz.app.courses.fragment.QbankResult;
import com.emedicoz.app.courses.fragment.Quiz;
import com.emedicoz.app.courses.fragment.QuizResultAwait;
import com.emedicoz.app.courses.fragment.QuizResultBasic;
import com.emedicoz.app.courses.fragment.ResultAnalysisFragment;
import com.emedicoz.app.courses.fragment.SubjectWiseQuizResult;
import com.emedicoz.app.courses.fragment.ViewAnalysis;
import com.emedicoz.app.modelo.courses.quiz.QuizModel;
import com.emedicoz.app.modelo.courses.quiz.ResultTestSeries;
import com.emedicoz.app.testmodule.fragment.BookmarkedQuestionsList;
import com.emedicoz.app.testmodule.model.TestseriesBase;
import com.emedicoz.app.utilso.Const;
import com.emedicoz.app.utilso.Constants;

/**
 * Created by Cbc-03 on 10/12/17.
 */

public class QuizActivity extends BaseABNoNavActivity {
    public int DEFAULT_SPAN_COUNT = 4;
    public NavigationQuizAdapter navigationQuizAdapter;
    String fragType = "";
    String segmentId = "";
    String isComplete = "";
    String resultDate = "";
    String totalQuestion = "";
    String testSeriesId = "";
    String subjectId = "";
    QuizModel quiz;
    String subjectName = "";
    ResultTestSeries resultTestSeries;
    TestseriesBase testseriesBase;
    boolean solution = true;
    boolean custom = false;
    String testSeriesName = "";


    // To handle the Quiz Counter Navigation
    View.OnClickListener navigatorClickListener = view -> {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
        ((Quiz) mFragment).initalizeNavigationView();
        /*if (mFragment instanceof Quiz) {
            ((Quiz) mFragment).initalizeNavigationView();
        }
        else {
            ((QuizSolutions) mFragment).initalizeNavigationView();
        }*/
    };

    public void counterCallbackListener(int i) {
        ((Quiz) mFragment).controlQuizQuestion(i);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            drawer.openDrawer(GravityCompat.END);
        }
    }

    @Override
    protected void initViews() {
        if (getIntent().getExtras() != null) {
            fragType = getIntent().getExtras().getString(Const.FRAG_TYPE);
            quiz = (QuizModel) getIntent().getExtras().getSerializable(Const.TESTSERIES);
            resultTestSeries = (ResultTestSeries) getIntent().getExtras().getSerializable(Const.RESULT_SCREEN);
            segmentId = getIntent().getExtras().getString(Constants.ResultExtras.TEST_SEGMENT_ID);
            isComplete = getIntent().getExtras().getString(Const.IS_COMPLETE);
            if (fragType.equalsIgnoreCase(Const.RESULT_AWAIT)) {
                resultDate = getIntent().getExtras().getString(Constants.Extras.DATE);
            }
            testSeriesName = getIntent().getExtras().getString(Constants.Extras.NAME);
            totalQuestion = getIntent().getExtras().getString(Const.TOTAL_QUESTIONS);
            testSeriesId = getIntent().getExtras().getString(Const.TEST_SERIES_ID);
            testseriesBase = (TestseriesBase) getIntent().getExtras().getSerializable(Const.TESTSERIESBASE);
            subjectId = getIntent().getExtras().getString(Constants.Extras.SUBJECT_ID);
            subjectName = getIntent().getExtras().getString(Constants.Extras.SUBJECT_NAME);
            solution = getIntent().getBooleanExtra(Constants.Extras.SOLUTION, true);
            custom = getIntent().getBooleanExtra(Constants.Extras.CUSTOM, false);

        }
    }


    @Override
    protected Fragment getFragment() {
        quizNavigatorIV.setVisibility(View.GONE);
        switch (fragType) {
            case Const.TESTSERIES:

                setToolbarTitle("Quiz");
                if (quizNavigatorIV.getVisibility() == View.GONE)
                    quizNavigatorIV.setVisibility(View.VISIBLE);
                quizNavigatorIV.setOnClickListener(navigatorClickListener);
                return Quiz.newInstance(fragType, quiz);
            case Const.RESULT_SCREEN:
                setToolbarTitle(testSeriesName);
                if (!TextUtils.isEmpty(segmentId)) {
                    return ResultAnalysisFragment.newInstance(fragType, testSeriesId, segmentId, testSeriesName);
                } else {
                    return ResultAnalysisFragment.newInstance(fragType, testSeriesId, resultTestSeries, quiz, testSeriesName);
                }
            case Const.RESULT_AWAIT:
                setToolbarTitle("Result");
                return QuizResultAwait.newInstance(resultDate);
            case Const.LEADERBOARD:
                setToolbarTitle("Leaderboard");
                return LeaderBoardFragment.newInstance(fragType, segmentId, resultTestSeries);
            case Const.VIEW_ANALYSIS:
                setToolbarTitle("View Analysis");
                return ViewAnalysis.newInstance(segmentId, resultTestSeries);

            case "GRAPH":
                setToolbarTitle("Graph");
                return GraphFragment.newInstance(segmentId, resultTestSeries);
            case Const.RESULT_SUBJECT_WISE:
                setToolbarTitle("Subject Wise Result");
                return SubjectWiseQuizResult.newInstance(segmentId);
            case Const.RESULT_BASIC:
                if (testseriesBase == null) {
                    setToolbarTitle(resultTestSeries.getTestSeriesName());
                    return QuizResultBasic.newInstance(segmentId, resultTestSeries, type, subjectName);
                } else {
                    setToolbarTitle(resultTestSeries.getTestSeriesName());
                    return QuizResultBasic.newInstance(testSeriesId, resultTestSeries, testseriesBase, totalQuestion, type, subjectName);
                }

            case Const.QBANK_RESULT:
                if (!solution)
                    setToolbarTitle("Daily Quiz Analysis");
                else
                    setToolbarTitle("QBank Analysis");
                return QbankResult.newInstance(resultTestSeries, totalQuestion, segmentId, solution, custom);
            case Const.BOOKMARK_LIST:
                setToolbarTitle("Questions");
                return BookmarkedQuestionsList.newInstance(subjectId);
            default:
        }
        return null;
    }

    public void checkGraphVisibility() {
        if (this.mFragment instanceof ViewAnalysis) {

            this.graphTV.setVisibility(View.VISIBLE);

        }
    }

}
