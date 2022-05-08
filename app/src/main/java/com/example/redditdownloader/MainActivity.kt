package com.example.redditdownloader

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.redditdownloader.application.service.SubredditService
import com.example.redditdownloader.core.enum.SubredditType
import com.example.redditdownloader.viewmodel.MainActivityViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    lateinit var mainActivityViewModel: MainActivityViewModel
    var subredditService = SubredditService()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.mainActivityViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        val btnAdd = findViewById<FloatingActionButton>(R.id.btnAdd)

        btnAdd.setOnClickListener {
            Intent(this, ActivityAddSubreddit::class.java).apply {
                startActivity(this);
            }
        };

        val btnDownload = findViewById<Button>(R.id.btnDownload)
        btnDownload.setOnClickListener {
            Executors.newSingleThreadExecutor().execute {
                if(mainActivityViewModel.isDownloading.value == true) {
                    Looper.prepare()
                    Toast.makeText(applicationContext, "Please wait, download already in progress", Toast.LENGTH_SHORT).show()
                    return@execute
                }

                var progressBar = findViewById<ProgressBar>(R.id.progressBarDownload)
                var textViewDownloadInfo: TextView = findViewById(R.id.textViewDownloadInfo)

                mainActivityViewModel.subreddits.value?.let {
                    mainActivityViewModel.setDownloading(true)
                    
                    subredditService.download(applicationContext, it.toList(), progressBar, mainActivityViewModel.amountPosts, mainActivityViewModel.postType, textViewDownloadInfo)

                    mainActivityViewModel.setDownloading(false)
                }
            }
        }

        // Observe isDownloading to show/hide specific view components
        mainActivityViewModel.isDownloading.observe(this, Observer {
            var loadingSpinner: ProgressBar = findViewById(R.id.downloadIndicator)
            var progressBar: ProgressBar = findViewById(R.id.progressBarDownload)
            var btnDownload: Button = findViewById(R.id.btnDownload)
            var btnAdd: FloatingActionButton = findViewById(R.id.btnAdd)
            var textViewDownloadInfo: TextView = findViewById(R.id.textViewDownloadInfo)

            if(it == true) {
                progressBar.visibility = ProgressBar.VISIBLE
                loadingSpinner.visibility = ProgressBar.VISIBLE
                btnDownload.visibility = Button.GONE
                btnAdd.visibility = FloatingActionButton.GONE
                textViewDownloadInfo.visibility = TextView.VISIBLE
            }
            else {
                loadingSpinner.visibility = ProgressBar.GONE
                progressBar.visibility = ProgressBar.GONE
                btnDownload.visibility = Button.VISIBLE
                btnAdd.visibility = FloatingActionButton.VISIBLE
                textViewDownloadInfo.visibility = TextView.GONE
            }
        })

        val listViewSubreddits = findViewById<ListView>(R.id.listViewSubreddits)
        listViewSubreddits.choiceMode = android.widget.AbsListView.CHOICE_MODE_SINGLE
        listViewSubreddits.isLongClickable = true

        mainActivityViewModel.subreddits.observe(this, Observer {
            var subreddits = mutableListOf<String>()

            for(subreddit in mainActivityViewModel.subreddits.value.orEmpty()) {
                subreddits.add("%s - %s".format(subreddit.name, if(subreddit.type == SubredditType.SUBREDDIT) "subreddit" else "user"))
            }

            val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, subreddits)

            listViewSubreddits.adapter = adapter

            listViewSubreddits.setOnItemLongClickListener(AdapterView.OnItemLongClickListener() { adapterView: AdapterView<*>, view1: View, index: Int, l: Long ->
                val builder = AlertDialog.Builder(this@MainActivity)
                val subreddit = mainActivityViewModel.subreddits.value.orEmpty().get(index)

                builder.setMessage("Are you sure you want to delete %s %s?".format(if(subreddit.type == SubredditType.SUBREDDIT) "subreddit" else "user", subreddit.name))
                    .setCancelable(true)
                    .setPositiveButton("Yes") { dialogInterface: DialogInterface, i: Int ->
                        Executors.newSingleThreadExecutor().execute {
                            mainActivityViewModel.removeSubreddit(subreddit)

                            Looper.prepare()
                            Toast.makeText(this, "Successfully removed %s".format(subreddit.name), Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("No") { dialogInterface: DialogInterface, i: Int ->
                        dialogInterface.dismiss()
                    }

                var alert = builder.create()
                alert.show()

                return@OnItemLongClickListener true
            })
        })

        val selectType: Spinner = findViewById(R.id.selectType)
        val availableTypes: List<String> = listOf("hot", "top", "new")
        val selectTypesAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availableTypes)
        selectType.adapter = selectTypesAdapter
        selectType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mainActivityViewModel.postType = availableTypes[position]
            }
        }
        selectType.setSelection(0)

        val selectAmountPosts: Spinner = findViewById(R.id.selectAmountPosts)
        val availableAmountPosts: List<Int> = listOf(10, 25, 50, 75, 100)
        val selectAmountPostsAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, availableAmountPosts)
        selectAmountPosts.adapter = selectAmountPostsAdapter
        selectAmountPosts.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mainActivityViewModel.amountPosts = availableAmountPosts[position]
            }
        }
        selectType.setSelection(0)

    }

}