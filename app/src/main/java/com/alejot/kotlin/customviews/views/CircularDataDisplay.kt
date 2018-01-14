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
    private val DATA_TO_LABEL_SIZE_RATIO = 0.4f
    private val DATA_LABEL_SPACING_TO_LABEL_HEIGHT_RATIO = 0.2f
    private val DEFAULT_TEXT_SIZE_SP = 100
    private val CIRCUMSCRIBED_PADDING = 20
    private val MAX_CHARACTERS_SUPPORTED = "XXXXX"

    // View fields
    private var viewWidth = 0
    private var viewHeight = 0

    // Display fields
    private val displayPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var displayRadius: Float = 0f

    // Data text fields
    private var data: CharSequence = ""
    private var dataLayout: StaticLayout? = null
    private val dataPosition: Point = Point(0 , 0)
    private val dataPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    // Label text fields
    private var label: CharSequence = ""
    private var labelLayout: StaticLayout? = null
    private val labelPosition: Point = Point(0 , 0)
    private val labelPaint: TextPaint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    // Constructors
    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleRes: Int) : super(context, attrs, defStyleRes)
    {
        // Get layout params
        val a: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularDataDisplay, 0, defStyleRes)
        val rawSize = a.getDimensionPixelSize(R.styleable.CircularDataDisplay_textSize, Dimension.SP.plus(DEFAULT_TEXT_SIZE_SP)).toFloat()
        val textColor = a.getColor(R.styleable.CircularDataDisplay_textColor, Color.WHITE)
        val dataText: CharSequence = a.getText(R.styleable.CircularDataDisplay_dataText)
        val labelText: CharSequence = a.getText(R.styleable.CircularDataDisplay_labelText)
        val dColor: Int = a.getColor(R.styleable.CircularDataDisplay_displayColor, Color.BLACK)
        a.recycle()

        // Initialize text paints
        dataPaint.color = textColor
        labelPaint.color = textColor
        setTextSizes(rawSize)

        // Initialize text
        data = dataText
        label = labelText

        // Initialize display paint
        displayPaint.color = dColor

        // Initialize view boundaries.
        setDisplayedData()
        updateViewBounds()
    }

    fun setLabelText(text: CharSequence?)
    {
        if (text != null && !TextUtils.equals(label, text))
        {
            label = text
            updateTextPosition()
            invalidate()
        }
    }

    fun setDataText(text: CharSequence?)
    {
        if (text != null && !TextUtils.equals(data, text))
        {
            data = text
            setDisplayedData()
            updateTextPosition()
            invalidate()
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int)
    {
        // Only update if anything actually changed
        if (w != oldw || h != oldh)
        {
            updateViewBounds()
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas != null)
        {
            // Draw display area
            drawDisplayArea(canvas)

            // Draw data and label text
            drawText(canvas)
        }
    }

    private fun setTextSizes(size: Float)
    {
        // The size of the label is proportional to the size of the data
        dataPaint.textSize = size
        labelPaint.textSize = dataPaint.textSize * DATA_TO_LABEL_SIZE_RATIO
    }

    private fun updateViewBounds()
    {
        // Update stored view dimensions
        viewWidth = width
        viewHeight = height

        // Update text positioning
        updateTextPosition()

        updateDisplaySize()
    }

    private fun setDisplayedData()
    {
        // Clip the displayed data so that it
        // doesn't bleed outside of the circle display
        val dataBuilder = StringBuilder()
        if (data.length < MAX_CHARACTERS_SUPPORTED.length)
        {
            dataBuilder.append(data)
        }
        else
        {
            dataBuilder.append(data.slice(1..3))
            dataBuilder.append('+')
        }

        data = dataBuilder.toString()
    }

    private fun updateDisplaySize()
    {
        // Circumference of circular dependent on size of text + padding
        val textWidth = dataPaint.measureText(MAX_CHARACTERS_SUPPORTED, 0, MAX_CHARACTERS_SUPPORTED.length)
        val displayCirc = textWidth + (CIRCUMSCRIBED_PADDING * 2)
        displayRadius = Dimension.PX.plus(displayCirc / 2)
    }

    private fun updateTextPosition()
    {
        // Create the text layout for the data text
        val dWidth = dataPaint.measureText(data, 0, data.length).toInt()
        dataLayout = StaticLayout(data, dataPaint, dWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, true)

        // Position data text in the center of the view
        val dHeight = dataLayout?.height ?: 0
        val dXPos: Int = (viewWidth - dWidth) / 2
        val dYPos: Int = (viewHeight - dHeight) / 2
        dataPosition.set(dXPos, dYPos)

        // Create the text layout for the label text
        val lWidth = labelPaint.measureText(label, 0, label.length).toInt()
        labelLayout = StaticLayout(label, labelPaint, lWidth, Layout.Alignment.ALIGN_CENTER, 1f, 0f, true)

        // Position the label text centered and just below the data text
        val lHeight = labelLayout?.height ?: 0
        val lXPos: Int = (viewWidth - lWidth) / 2
        val lYPos: Int = (viewHeight + lHeight) / 2 + (lHeight * DATA_LABEL_SPACING_TO_LABEL_HEIGHT_RATIO).toInt()
        labelPosition.set(lXPos, lYPos)
    }

    private fun drawDisplayArea(canvas: Canvas)
    {
        val centerX: Float = viewWidth / 2f
        val centerY: Float = viewHeight / 2f
        canvas.drawCircle(centerX, centerY, displayRadius, displayPaint)
    }

    private fun drawText(canvas: Canvas)
    {
        // Translate, draw, and restore the canvas for each text field
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