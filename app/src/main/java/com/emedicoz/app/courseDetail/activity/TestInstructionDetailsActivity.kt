package com.emedicoz.app.courseDetail.activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.R
import com.emedicoz.app.databinding.TestActivityInstructionBinding
import com.emedicoz.app.testmodule.activity.TestBaseActivity
import com.emedicoz.app.utilso.Const
import com.emedicoz.app.utilso.Constants
import com.emedicoz.app.utilso.SharedPreference


class TestInstructionDetailsActivity : AppCompatActivity(), View.OnClickListener{
    lateinit var binding: TestActivityInstructionBinding
    var testSeriesId: String? =null
    var totalQuestion: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TestActivityInstructionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    fun initViews(){
        binding.statusTV.setOnClickListener(this)
        totalQuestion =intent.extras?.getString(Constants.Extras.TOTAL_QUESTIONS).toString()
        testSeriesId =intent.extras?.getString(Const.TEST_SERIES_ID).toString()
        binding.txtTotalQuestions.text =totalQuestion
        binding.txtTotalTime.text
    }
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.statusTV->{
                intent =  Intent(this, TestBaseActivity::class.java)
                intent.putExtra(Const.STATUS, false);
                intent.putExtra(Const.TEST_SERIES_ID, testSeriesId);
                SharedPreference.getInstance().saveTestSerieId(Const.QBANK_TEST_SERIES_ID, testSeriesId);
                this.startActivity(intent);
                finish()

            }
        }
    }
}












//lateinit var binding: ActivityPauseTestBinding
// lateinit var binding: ActiviityAbortQuestionsBinding
// binding = ActivityPauseTestBinding.inflate(layoutInflater)
// binding = ActiviityAbortQuestionsBinding.inflate(layoutInflater)