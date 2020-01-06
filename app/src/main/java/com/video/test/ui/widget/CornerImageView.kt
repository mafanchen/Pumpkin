package com.video.test.ui.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.video.test.R
import kotlin.math.max

/**
 * 带圆角的ImageView
 */
class CornerImageView : ImageView {

    /**
     * 圆角半径
     */
    private var mCornerRadius: Float = 0f
    /**
     * 矩阵
     */
    private val mMatrix: Matrix = Matrix()

    private val mPaint: Paint = Paint()

    init {
        mPaint.isAntiAlias = true
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        context ?: return
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CornerImageView)
        mCornerRadius = typedArray.getFloat(R.styleable.CornerImageView_radius, 0f)
        if (mCornerRadius < 0) mCornerRadius = 0f
        typedArray.recycle()
    }

    //todo 未曾考虑到内边距，使用此类内边距将会无效
    override fun onDraw(canvas: Canvas?) {
        drawable ?: return
        canvas ?: return
        val bitmap = drawableToBitmap(drawable)
        //对bitmap进行缩放，默认为原本大小
        var scale = 1f
        //如果宽高有所不同，则计算缩放比例
        if (!(bitmap.width == width && bitmap.height == height)) {
            //缩放后的图片的宽高，一定要大于我们view的宽高,所以我们这里取大值
            scale = max(width.toFloat() / bitmap.width, height.toFloat() / bitmap.height)
        }
        //shader变换矩阵，用于放大缩小
        mMatrix.setScale(scale, scale)
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        shader.setLocalMatrix(matrix)
        //设置shader
        mPaint.shader = shader
        canvas.drawRoundRect(RectF(0f, 0f, width.toFloat(), height.toFloat()), mCornerRadius, mCornerRadius, mPaint)
    }

    /**
     * drawable转换为bitmap
     */
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        //本身就是bitmap
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        //如果是单色图片的drawable，则宽度小于等于0，此时返回bitmap的大小为此控件的大小
        val w: Int = if (drawable.intrinsicWidth <= 0) width else drawable.intrinsicWidth
        val h: Int = if (drawable.intrinsicHeight <= 0) height else drawable.intrinsicHeight
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)
        return bitmap
    }

}