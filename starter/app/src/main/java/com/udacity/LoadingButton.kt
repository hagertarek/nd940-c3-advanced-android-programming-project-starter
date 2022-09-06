package com.udacity

import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var loadingProgress = 0f

    private var valueAnimator = ValueAnimator()

    private var buttonColor = 0
    private var loadingButtonColor = 0
    private var circleColor = 0
    private var textColor = 0
    private var buttonText = ""
    private var loadingText = ""
    private var textToDraw = ""

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->

        when (new) {
            ButtonState.Clicked -> {
            }
            ButtonState.Completed -> {
                textToDraw =
                    buttonText
                stopButtonAnimation()
                isEnabled = true
            }
            ButtonState.Loading -> {
                textToDraw = loadingText
                startButtonAnimation()
                isEnabled = false
            }
        }
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 60.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val circlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            loadingButtonColor = getColor(R.styleable.LoadingButton_loadingButtonColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            buttonText = getString(R.styleable.LoadingButton_buttonText).toString()
            loadingText = getString(R.styleable.LoadingButton_loadingText).toString()

        }
        textToDraw = buttonText
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawButton(canvas)

        drawText(canvas)

        drawCircle(canvas)

        drawLoading(canvas)
    }

    private fun drawLoading(canvas: Canvas) {
        backgroundPaint.color = loadingButtonColor

        canvas.drawRect(
            0f,
            0f,
            widthSize.toFloat() * loadingProgress,
            heightSize.toFloat(),
            backgroundPaint
        )
    }

    private fun drawCircle(canvas: Canvas) {
        val circleLeft =
            (widthSize.toFloat() + textPaint.measureText(textToDraw)) / 2 + heightSize.toFloat() * 0.1f
        val circleTop = heightSize.toFloat() * 0.3f
        val circleRight =
            circleLeft + heightSize.toFloat() * 0.4f
        val circleBottom = heightSize.toFloat() - heightSize.toFloat() * 0.3f

        circlePaint.color = circleColor
        canvas.drawArc(
            circleLeft,
            circleTop,
            circleRight,
            circleBottom,
            0F,
            360F * loadingProgress,
            true,
            circlePaint
        )
    }

    private fun drawText(canvas: Canvas) {
        val y = (heightSize.toFloat() - (textPaint.descent() + textPaint.ascent())) / 2

        textPaint.color = textColor
        canvas.drawText(textToDraw, widthSize.toFloat() / 2, y, textPaint)
    }

    private fun drawButton(canvas: Canvas) {
        backgroundPaint.color = buttonColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), backgroundPaint)
    }

    private fun startButtonAnimation() {

        valueAnimator = ValueAnimator.ofFloat(0f, 1f)
        valueAnimator.duration = 2000
        valueAnimator.addUpdateListener {
            loadingProgress = it.animatedValue as Float
            invalidate()
        }

        valueAnimator.repeatCount = INFINITE
        valueAnimator.start()
    }

    private fun stopButtonAnimation(){
        valueAnimator.cancel()
        loadingProgress = 0f
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}