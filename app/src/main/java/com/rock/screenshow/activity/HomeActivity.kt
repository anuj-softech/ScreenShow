package com.rock.screenshow.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rock.screenshow.activity.viewmodel.HomeViewModel
import com.rock.screenshow.adapter.VideoRowAdapter
import com.rock.screenshow.databinding.ActivityHomeBinding
import com.rock.screenshow.model.VideoItem
import com.rock.screenshow.model.VideoRow

class HomeActivity : AppCompatActivity() {
    lateinit var lb: ActivityHomeBinding
    private lateinit var exoPlayer: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lb = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(lb.getRoot())
        loadHome()
    }

    private fun playSplash() {
        lb.loader.setVisibility(View.GONE)
        lb.splash.visibility = View.VISIBLE
        exoPlayer = ExoPlayer.Builder(this).build()
        lb.splashPlayer.setPlayer(exoPlayer)

        val mediaItem = MediaItem.fromUri("asset:///splash.mp4")
        exoPlayer.setMediaItem(mediaItem)

        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    stopSplash()
                }
            }
        })

        exoPlayer.prepare()
        lb.splashPlayer.postDelayed({
            exoPlayer.play()
        }, 100)
    }

    fun stopSplash() {
        lb.splash.setAlpha(1f)
        lb.splashPlayer.animate().alpha(0F).setDuration(600)
            .withEndAction {
                lb.splash.removeAllViews()
                lb.splash.visibility = View.GONE
                exoPlayer.stop()
                exoPlayer.release()
                lb.parentLinearLayout.setAlpha(0F)
                lb.parentLinearLayout.animate().alpha(1f).setDuration(300).start()
            }.start()
    }

    private fun loadHome() {
        playSplash()
        val viewModel = HomeViewModel(this)
        viewModel.rows.observe(this) { rows ->
            stopSplash()
            updateUI(rows)
        }
        viewModel.loadHomeRows()
    }

    private fun updateUI(rows: List<VideoRow>) {
        lb.parentLinearLayout.removeAllViews()
        showHistory()
        addShadowListener()
        for (videoRow in rows) {
            val titleView = TextView(this)
            titleView.text = videoRow.title
            titleView.textSize = 18f
            titleView.setPadding(16, 16, 16, 8)
            lb.parentLinearLayout.addView(titleView)

            val recyclerView = RecyclerView(this)
            recyclerView.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            recyclerView.setLayoutManager(
                LinearLayoutManager(
                    this,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
            )
            val onClickListener = VideoRowAdapter.OnClickListener { videoItem: VideoItem ->
                play(videoItem)
            }

            recyclerView.setAdapter(
                VideoRowAdapter(
                    videoRow,
                    recyclerView,
                    onClickListener,
                    lb.scrollView
                )
            )

            lb.parentLinearLayout.addView(recyclerView)
        }
    }

    private fun addShadowListener() {
        lb.scrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY > 100) {
                lb.topshadow.visibility = View.VISIBLE
            } else {
                lb.topshadow.visibility = View.GONE
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showHistory() {
        val videoItems: List<VideoItem> = mutableListOf()
        Log.e("History trying", "History: " + videoItems.size)
        val titleView = TextView(this)
        titleView.text = "Keep Watching"
        titleView.textSize = 18f
        titleView.setPadding(16, 16, 16, 8) // Add padding
        lb.parentLinearLayout.addView(titleView)
        val recyclerView = RecyclerView(this)
        recyclerView.setLayoutParams(
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        )
        recyclerView.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        val onClickListener = VideoRowAdapter.OnClickListener { videoItem: VideoItem ->
            play(videoItem)
        }
        val videoRow = VideoRow("Keep Watching", videoItems)
        recyclerView.setAdapter(
            VideoRowAdapter(
                videoRow,
                recyclerView,
                onClickListener,
                lb.scrollView
            )
        )
        lb.parentLinearLayout.addView(recyclerView)
    }

    private fun play(item: VideoItem) {

    }

}


