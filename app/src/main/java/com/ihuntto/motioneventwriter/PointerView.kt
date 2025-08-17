package com.ihuntto.motioneventwriter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class PointerView : View {

    data class Point(val x: Float = 0f, val y: Float = 0f, val time: Long = 0)

    private val pointerArray = arrayOfNulls<ArrayList<Point>>(20)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        paint.color = Color.RED
        paint.style = Paint.Style.FILL
    }

    override fun onDraw(canvas: Canvas) {
        for (pointer in pointerArray) {
            pointer?.let {
                for (point in pointer) {
                    canvas.drawCircle(point.x, point.y, 10f, paint)
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            when (it.actionMasked) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    if (it.actionMasked == MotionEvent.ACTION_DOWN) {
                        pointerArray.fill(null)
                    }

                    val point = Point(
                        event.getX(event.actionIndex),
                        event.getY(event.actionIndex),
                        event.eventTime
                    )
                    val pointerId = event.getPointerId(event.actionIndex)
                    pointerArray[pointerId] = ArrayList<Point>()
                    pointerArray[pointerId]?.add(point)
                }

                MotionEvent.ACTION_MOVE -> {
                    for (pos in 0 until event.historySize) {
                        for (index in 0 until event.pointerCount) {
                            val point = Point(
                                event.getHistoricalX(index, pos),
                                event.getHistoricalY(index, pos),
                                event.getHistoricalEventTime(pos)
                            )
                            val pointerId = event.getPointerId(index)
                            pointerArray[pointerId]?.add(point)
                        }
                    }
                    for (index in 0 until event.pointerCount) {
                        val point = Point(
                            event.getX(index),
                            event.getY(index),
                            event.eventTime
                        )
                        val pointerId = event.getPointerId(index)
                        pointerArray[pointerId]?.add(point)
                    }
                }

                MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_UP -> {
                    val point = Point(
                        event.getX(event.actionIndex),
                        event.getY(event.actionIndex),
                        event.eventTime
                    )
                    val pointerId = event.getPointerId(event.actionIndex)
                    pointerArray[pointerId]?.add(point)
                }

                else -> {}
            }
        }
        invalidate()
        return true
    }

    fun getPointers(): HashMap<Int, ArrayList<Point>> {
        val pointers = HashMap<Int, ArrayList<Point>>()
        for (id in 0 until pointerArray.size) {
            pointerArray[id]?.let {
                pointers.put(id, it)
            }
        }
        return pointers
    }

    fun isPointersEmpty(): Boolean {
        for (id in 0 until pointerArray.size) {
            pointerArray[id]?.let {
                return false
            }
        }
        return true
    }
}