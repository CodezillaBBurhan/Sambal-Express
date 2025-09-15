package sambal.mydd.app.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import androidx.appcompat.widget.AppCompatImageView
import android.graphics.RectF
import android.util.AttributeSet

class ImageHe : AppCompatImageView {
    private val radius = 18.0f
    private var path: Path? = null
    private var rect: RectF? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle) {
        init()
    }

    private fun init() {
        path = Path()
    }

    override fun onDraw(canvas: Canvas) {
        rect = RectF(0F, 0F, this.width.toFloat(), this.height.toFloat())
        path!!.addRoundRect(rect!!, 0f, 0f, Path.Direction.CW)
        canvas.clipPath(path!!)
        super.onDraw(canvas)
    }
}