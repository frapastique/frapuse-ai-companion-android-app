package com.back.frapuse.util.layoutmanager

import android.content.Context
import androidx.recyclerview.widget.GridLayoutManager

class NoScrollLayoutManager(context: Context, spanCount: Int) : GridLayoutManager(context, spanCount) {

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return false
    }
}