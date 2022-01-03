package com.emedicoz.app.helpdesk
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.emedicoz.app.databinding.ActivityHelpAndSupportSelectionsBinding

class HelpAndSupportSelectionsActivity : AppCompatActivity() {
    lateinit var binding: ActivityHelpAndSupportSelectionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityHelpAndSupportSelectionsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }
    fun initViews(){
        val adapter = QuestionsAdapter(this)
        binding.recyclerQuestionsView.adapter = adapter
    }
}