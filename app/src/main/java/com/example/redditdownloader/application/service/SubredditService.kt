package com.example.redditdownloader.application.service

import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.redditdownloader.application.datatransferobjects.SubredditDto
import com.example.redditdownloader.core.enum.SubredditType
import com.google.common.collect.Lists
import khttp.responses.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SubredditService {

    fun download(context: Context, subreddits : List<SubredditDto>, progressBar: ProgressBar, amountPosts: Int, postType: String, textViewDownloadInfo: TextView) {
        Looper.prepare()

        var imagesToDownload: HashMap<String, MutableList<String>> = hashMapOf()

        // First load all image links
        for(subreddit in subreddits) {
            imagesToDownload[subreddit.name] = getImageLinks(subreddit, amountPosts, postType)
        }

        // Then create threads to download all pictures of the subreddits
        for(subreddit in imagesToDownload) {
            if(subreddit.value.count() < 1) continue

            textViewDownloadInfo.post(Runnable {
                textViewDownloadInfo.text = "Downloading subreddit /r/%s please be patient...".format(subreddit.key)
            })

            progressBar.progress = 0
            progressBar.max = subreddit.value.count()

            // Partition list into batches to avoid issues
            var batches = Lists.partition(subreddit.value, 5)

            for(batch in batches) {
                try {
                    val threads: MutableList<Thread> = mutableListOf()

                    for(imageLink in batch) {
                        val thread = Thread {
                            downloadImage(context, subreddit.key, imageLink, progressBar)
                        }

                        thread.start()

                        threads.add(thread)
                    }

                    // Wait for download threads to finish
                    threads.forEach {
                        try {
                            it.join()
                        } catch (e: Exception) { }
                    }
                } catch (e: Exception) {
                    continue
                }
            }
        }

        progressBar.progress = 0
    }

    private fun getImageLinks(subreddit: SubredditDto, count: Int, postType: String) : MutableList<String> {
        var subredditLinks: MutableList<String> = mutableListOf()

        var url = "https://api.reddit.com/r/%s/%s?limit=%d".format(subreddit.name, postType, count)

        if(subreddit.type == SubredditType.USER)
            url = "https://api.reddit.com/u/%s/%s?limit=%d".format(subreddit.name, postType, count)

        val response : Response = khttp.get(url)
        val subredditResponse : JSONObject = response.jsonObject

        try {
            val data: JSONObject = subredditResponse.get("data") as JSONObject;
            val children: JSONArray = data.get("children") as JSONArray

            // Invalid subreddit
            if(children.length() < 1) return subredditLinks

            for(i in 0..children.length() - 1) {
                var child: JSONObject = children.get(i) as JSONObject
                var childData: JSONObject = child.get("data") as JSONObject
                var url: String = childData.get("url") as String

                // Skip non-image posts (for now only download images hosted on reddit)
                if(!url.contains("i.redd.it")) continue

                subredditLinks.add(url)
            }
        }
        catch (e: Exception) {}

        return subredditLinks
    }

    private fun downloadImage(context: Context, fileDirectory: String, fileUrl: String, progressBar: ProgressBar) {
        val fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1)

        // Deprecated but at least it works like this
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)?.absolutePath + "/" + fileDirectory

        val file = File(path, fileName)

        Looper.prepare()

        // Skip files that are already downloaded
        if(file.exists()) {
            progressBar.incrementProgressBy(1)
            return
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(fileUrl)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes((DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE))
                .setAllowedOverRoaming(false)
                .setTitle(fileName)
                .setDescription("")
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "%s/%s".format(fileDirectory, fileName))
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)

        var downloading = true
        var count = 0
        var guard = 5000

        while(downloading && count < guard) {
            try {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()

                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))

                if(status == DownloadManager.STATUS_SUCCESSFUL || status == DownloadManager.STATUS_FAILED) break
            }
            catch (e: Exception) {
                break
            }

            count++
        }

        progressBar.incrementProgressBy(1)
    }

}