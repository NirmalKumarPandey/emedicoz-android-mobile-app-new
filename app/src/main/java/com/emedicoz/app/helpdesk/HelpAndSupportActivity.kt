package com.emedicoz.app.helpdesk
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.R
import com.emedicoz.app.databinding.ActivityHelpAndSupportBinding

class HelpAndSupportActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding:ActivityHelpAndSupportBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =ActivityHelpAndSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    fun initViews(){
        binding.txtChooseTopic.text
        binding.crdLiveCourse.setOnClickListener(this)
        binding.crdClassRoom.setOnClickListener(this)
        binding.crdCredits.setOnClickListener(this)
        binding.crdGames.setOnClickListener(this)
    }
    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.crdLiveCourse -> {
                intent = Intent(this,HelpAndSupportSelectionsActivity::class.java)
                startActivity(intent)
            }
            R.id.crdClassRoom -> {
                intent = Intent(this,HelpAndSupportSelectionsActivity::class.java)
                startActivity(intent)
            }
            R.id.crdCredits -> {
                intent = Intent(this,HelpAndSupportSelectionsActivity::class.java)
                startActivity(intent)
            }
            R.id.crdGames -> {
                intent = Intent(this,HelpAndSupportSelectionsActivity::class.java)
                startActivity(intent)
            }
        }
    }
}
