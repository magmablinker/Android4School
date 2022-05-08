package com.example.redditdownloader

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.example.redditdownloader.core.enum.SubredditType
import com.example.redditdownloader.viewmodel.AddSubredditViewModel
import kotlinx.coroutines.*
import java.util.concurrent.Executors

class ActivityAddSubreddit : AppCompatActivity() {

    lateinit var addSubredditViewModel : AddSubredditViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subreddit)

        this.addSubredditViewModel = ViewModelProvider(this).get(AddSubredditViewModel::class.java)

        val inputSubreddit = findViewById<EditText>(R.id.inputSubreddit)
        inputSubreddit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                addSubredditViewModel.setName(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val btnAdd = findViewById<Button>(R.id.btnAdd)
        btnAdd.setOnClickListener {
            if(addSubredditViewModel.subredditDto.name.isNullOrEmpty()) {
                showToast("Name can't be empty")
                return@setOnClickListener;
            }

            Executors.newSingleThreadExecutor().execute {
                // TODO: HOW TO WAIT FOR THIS????
                kotlin.runCatching { addSubredditViewModel.save() }
                    .onFailure {
                        var error = if(it.message != null) it.message else ""

                        if (error != null) {
                            Looper.prepare()
                            showToast(error)
                        }
                    }
                    .onSuccess {
                        Looper.prepare()

                        Intent(this, MainActivity::class.java).apply {
                            startActivity(this);
                        }

                        showToast("Saved successfully")
                    }
            }
        }

        val selectType: Spinner = findViewById(R.id.selectSubredditType)
        val availableTypes: List<String> = listOf("Subreddit", "User")
        val selectAmountPostsAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availableTypes)
        selectType.adapter = selectAmountPostsAdapter
        selectType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val textViewSubredditType: TextView = findViewById(R.id.textViewSubredditType)

                var selectedType: SubredditType = SubredditType.SUBREDDIT

                if(availableTypes[position] == "User")
                {
                    selectedType = SubredditType.USER
                    textViewSubredditType.text =  "/u/"
                }
                else
                {
                    textViewSubredditType.text =  "/r/"
                }

                addSubredditViewModel.setType(selectedType)
            }
        }
        selectType.setSelection(0)
    }

    fun showToast(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }
}