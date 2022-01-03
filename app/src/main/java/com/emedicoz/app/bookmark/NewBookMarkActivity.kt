package com.emedicoz.app.bookmark

import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.dailychallenge.model.BookmarkQuestion
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.emedicoz.app.R
import com.emedicoz.app.utilso.GenericUtils
import com.emedicoz.app.feeds.fragment.SavedNotesFragment
import com.emedicoz.app.databinding.ActivityNewBookMarkBinding
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import java.io.Serializable
import java.util.*

class NewBookMarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewBookMarkBinding
    var bookmarkQuestionList: List<BookmarkQuestion>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GenericUtils.manageScreenShotFeature(this)
        binding = ActivityNewBookMarkBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        bookmarkQuestionList =
            intent.getSerializableExtra(Constants.Extras.QUESTION_LIST) as List<BookmarkQuestion>?
        if (intent.getStringExtra(Constants.Extras.NAME) != null) {
            binding.toolBarNewBookmark.toolbarTitleTV.text =
                intent.getStringExtra(Constants.Extras.NAME)
        }
        binding.toolBarNewBookmark.toolbarBackIV.setOnClickListener {
            onBackPressed()
        }
        if (intent.getStringExtra(Constants.Extras.NAME) != null && !Objects.requireNonNull(
                intent.getStringExtra(Constants.Extras.NAME)
            ).equals(Constants.TestType.TEST, ignoreCase = true)
        ) {
            val fragment: Fragment = SavedNotesFragment.newInstance()
            val bundle = Bundle()
            if (intent.getStringExtra(Constants.Extras.ID) != null) bundle.putString(
                Constants.Extras.TAG_ID, intent.getStringExtra(
                    Constants.Extras.ID
                )
            )
            bundle.putString(
                Constants.Extras.NAME_OF_TAB,
                intent.getStringExtra(Constants.Extras.NAME_OF_TAB)
            )
            bundle.putSerializable(
                Constants.Extras.QUESTION_LIST,
                bookmarkQuestionList as Serializable?
            )
            bundle.putString(Const.TESTSERIES_ID, intent.getStringExtra(Const.TESTSERIES_ID))
            bundle.putString(
                Constants.Extras.Q_TYPE_DQB,
                intent.getStringExtra(Constants.Extras.Q_TYPE_DQB)
            )
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        } else {
            val fragment: Fragment = TestAndQuizFrag()
            val bundle = Bundle()
            if (intent.getStringExtra(Constants.Extras.ID) != null) bundle.putString(
                Constants.Extras.TAG_ID, intent.getStringExtra(
                    Constants.Extras.ID
                )
            )
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
        }
    }
}