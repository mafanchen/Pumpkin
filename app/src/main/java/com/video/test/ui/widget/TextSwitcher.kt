package com.video.test.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher

class TextSwitcher : TextSwitcher {

    companion object {
        private const val SWITCH_DELAY: Long = 3000
    }


    var textBinder: TextBinder<*>? = null


    private val runnable = Runnable {
        if (textBinder != null) {
            if (textBinder!!.showNext(this)) {
                loop()
            }
        }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)


    private fun loop() {
        postDelayed(runnable, SWITCH_DELAY)
    }

    fun start() {
        stop()
        loop()
    }

    fun stop() {
        removeCallbacks(runnable)
    }

    abstract class TextBinder<T> {
        val list: ArrayList<T> = ArrayList()

        var currentItem: T? = null

        abstract fun bind(item: T, textView: TextView)

        fun getCurrentPosition(): Int = when {
            list.isEmpty() -> -1
            currentItem == null -> -1
            else -> list.indexOf(currentItem!!)
        }

        fun setData(list: List<T>) {
            this.list.clear()
            this.list.addAll(list)
        }

        fun setText(item: T, switcher: ViewSwitcher) {
            currentItem = item
            val text = switcher.nextView as TextView
            bind(item, text)
            switcher.showNext()
        }

        fun setText(position: Int, switcher: ViewSwitcher) {
            setText(list[position], switcher)
        }

        fun showNext(switcher: ViewSwitcher): Boolean {
            if (list.isNotEmpty()) {
                var currentPosition = getCurrentPosition()
                currentPosition = when {
                    currentPosition < 0 -> 0
                    currentPosition >= list.size - 1 -> 0
                    else -> currentPosition + 1
                }
                setText(currentPosition, switcher)
                return true
            }
            return false
        }
    }
}