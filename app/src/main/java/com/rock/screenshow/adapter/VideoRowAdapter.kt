package com.rock.screenshow.adapter

import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.bumptech.glide.Glide
import com.rock.screenshow.databinding.ItemCardBinding
import com.rock.screenshow.model.VideoItem
import com.rock.screenshow.model.VideoRow
import com.rock.screenshow.views.AnimUtils


class VideoRowAdapter(
    videoRow: VideoRow,
    var recyclerView: RecyclerView?,
    private val onClickListner: OnClickListener,
    private val sv: View?
) : RecyclerView.Adapter<VideoRowAdapter.VideoCardHolder>() {

    private val videoItems: MutableList<VideoItem> = mutableListOf()

    init {
        videoItems.addAll(videoRow.videos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoCardHolder {
        val binding: ItemCardBinding =
            ItemCardBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false)
        return VideoCardHolder(binding)
    }

    override fun onBindViewHolder(holder: VideoCardHolder, position: Int) {
        val videoItem = videoItems.get(position)
        holder.bind(videoItem)
        val binding: ItemCardBinding = holder.binding
        binding.getRoot().setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                onClickListner.onClick(videoItem)
            }
        })
        val ob = AnimUtils.blink(binding.focusedView)
        binding.getRoot().setOnFocusChangeListener({ view, hasFocus ->
            if (hasFocus) {
                binding.focusedView.setBackgroundColor(Color.WHITE)
                ob.start()
                smoothScrollToPosition(holder.getAdapterPosition())
            } else {
                ob.cancel()
                binding.focusedView.setBackgroundColor(Color.TRANSPARENT) // Remove highlight
            }
        })
    }

    override fun getItemCount(): Int {
        return videoItems.size
    }

    private fun smoothScrollToPosition(position: Int) {
        if (recyclerView != null) {
            val smoothScroller: SmoothScroller =
                object : LinearSmoothScroller(recyclerView!!.getContext()) {
                    override fun getHorizontalSnapPreference(): Int {
                        return SNAP_TO_START
                    }
                }
            smoothScroller.setTargetPosition(position)
            recyclerView!!.getLayoutManager()!!.startSmoothScroll(smoothScroller)
        }
        if (sv != null) {
            Log.e(
                " " + sv.getScrollY(),
                "smoothScrollToPosition: " + (recyclerView!!.getTop() - 100)
            )
            val animator = ValueAnimator.ofInt(sv.getScrollY(), recyclerView!!.getTop() - 100)
            animator.setDuration(200)
            animator.addUpdateListener(AnimatorUpdateListener { valueAnimator: ValueAnimator? ->
                val scrollY = valueAnimator!!.getAnimatedValue() as Int
                sv.scrollTo(0, scrollY)
            })
            animator.start()
        }
    }

    fun interface OnClickListener {
        fun onClick(postId: VideoItem)
    }

    class VideoCardHolder(itemViewBinding: ItemCardBinding) :
        RecyclerView.ViewHolder(itemViewBinding.getRoot()) {
        val binding: ItemCardBinding = itemViewBinding

        fun bind(videoItem: VideoItem) {
            Glide.with(binding.cardPoster)
                .load(videoItem.thumbnail)
                .into(binding.cardPoster)
        }
    }
}
