package com.example.carsharingapp

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.GradientDrawable.Orientation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView

class LinearLayoutManagerWithSmoothScroller(context:Context,orientation: Int,reverseLayout:Boolean):LinearLayoutManager(context,orientation,reverseLayout) {

    override fun smoothScrollToPosition(recyclerView: RecyclerView?, state: RecyclerView.State?, position: Int) {
        val smoothScroller = SnappedSmoothScroller(recyclerView?.context!!)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    private inner class SnappedSmoothScroller internal constructor(context: Context) : LinearSmoothScroller(context){

        override fun getHorizontalSnapPreference() = SNAP_TO_START

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return this@LinearLayoutManagerWithSmoothScroller.computeScrollVectorForPosition(targetPosition)
        }

        }
}