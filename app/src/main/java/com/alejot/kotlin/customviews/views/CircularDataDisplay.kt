package com.alejot.kotlin.customviews.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.support.annotation.Dimension
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import com.alejot.kotlin.customviews.R

/**
 * Custom view consisting of text data and a label.
 * The data and label are encapsulated in a circle
 *
 * @author Alejandro Torroella
 */
class CircularDataDisplay : View
{
    // Constants
    private val DATA_LABEL_SIZE_RATIO = 0.45f
    private val LABEL_YPOS_ADJUSTION = 1.1f
    private val DEFAULT_TEXT_SIZE = 60

    // Display fields
    private var displayPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    // Data text fields
    private var data: CharSequence = ""
    private var dataLayout: StaticLayout? = null
    private var dataPosition: Point = Point(0 , 0)
    private var dataPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    // Label text fields
    private var label: CharSequence = ""
    private var labelLayout: StaticLayout? = null
    private var labelPosition: Point = Point(0 , 0)
    private var labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    // Constructors
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleRes: Int) : super(context, attrs, defStyleRes)
    {
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularDataDisplay, 0, defStyleRes)

        val dColor: Int = a.getColor(R.styleable.CircularDataDisplay_displayColor, Color.BLACK)
        setDisplayColor(dColor)

        val textColor = a.getColor(R.styleable.CircularDataDisplay_textColor, Color.WHITE)
        setTextColors(textColor)

        val rawSize: Int = a.getDimensionPixelSize(R.styleable.CircularDataDisplay_textSize, Dimension.SP.plus(DEFAULT_TEXT_SIZE))
        setTextSizes(rawSize.toFloat())

        var text: CharSequence? = a.getText(R.styleable.CircularDataDisplay_dataText)
        setDataText(text)

        text = a.getText(R.styleable.CircularDataDisplay_labelText)
        setLabelText(text)

        updateBounds()
        a.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (w != oldw || h != oldh) {
            updateBounds()
        }
    }

    override fun onDraw(canvas: Canvas?)
    {
        if (canvas != null)
        {
            // Draw display area
            drawDisplayArea(canvas)

            // Draw data and label text
            drawText(canvas)
        }
    }

    fun setLabelText(text: CharSequence?)
    {
        if (text != null && !TextUtils.equals(label, text))
        {
            label = text
            updateBounds()
            invalidate()
        }
    }

    fun setDataText(text: CharSequence?)
    {
        if (text != null && !TextUtils.equals(data, text))
        {
            data = text
            updateBounds()
            invalidate()
        }
    }

    fun setTextSizes(size: Float)
    {
        dataPaint.textSize = size
        labelPaint.textSize = dataPaint.textSize * DATA_LABEL_SIZE_RATIO
    }

    fun setTextColors(color: Int)
    {
        dataPaint.color = color
        labelPaint.color = color
    }

    fun setDisplayColor(color: Int)
    {
        displayPaint.color = color
    }

    private fun updateBounds()
    {
        val w = width
        val h = height

        // Create the text layout for the data text
        val dWidth = dataPaint.measureText(data, 0, data.length).toInt()
        dataLayout = StaticLayout(data, dataPaint, dWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, true)

        // Position data text in the center of the view
        val dHeight = dataLayout?.height ?: 0
        val dXPos: Int = (w - dWidth) / 2
        val dYPos: Int = (h - dHeight) / 2
        dataPosition.set(dXPos, dYPos)

        // Create the text layout for the label text
        val lWidth = labelPaint.measureText(label, 0, label.length).toInt()
        labelLayout = StaticLayout(label, labelPaint, lWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, true)

        // Position the label text centered and just below the data text
        val lHeight = labelLayout?.height ?: 0
        val lXPos: Int = (w - lWidth) / 2
        val lYPos: Int = (h + lHeight) / 2
        labelPosition.set(lXPos, (lYPos * LABEL_YPOS_ADJUSTION).toInt())
    }

    private fun drawDisplayArea(canvas: Canvas)
    {
        val centerX: Float = width.toFloat() / 2
        val centerY: Float = height.toFloat() / 2
        val radius: Float = height.toFloat() / 2
        canvas.drawCircle(centerX, centerY, radius, displayPaint)
    }

    private fun drawText(canvas: Canvas)
    {
        // Translate and restore the canvas drawing position after drawing each text field
        canvas.save()
        canvas.translate(dataPosition.x.toFloat(), dataPosition.y.toFloat())
        dataLayout?.draw(canvas)
        canvas.restore()

        canvas.save()
        canvas.translate(labelPosition.x.toFloat(), labelPosition.y.toFloat())
        labelLayout?.draw(canvas)
        canvas.restore()
    }
}