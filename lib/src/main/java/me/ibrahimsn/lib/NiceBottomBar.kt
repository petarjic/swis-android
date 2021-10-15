package me.ibrahimsn.lib

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.animation.ValueAnimator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.view.animation.LinearInterpolator
import android.view.animation.AnticipateInterpolator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.MotionEvent
import android.annotation.SuppressLint
import android.animation.ArgbEvaluator
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.TextUtils
import android.util.TypedValue
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.abs

class NiceBottomBar : View {

    // Default attribute values
    private var barBackgroundColor = Color.parseColor("#ffffff")
    private var barIndicatorColor = Color.parseColor("#426dfe")
    private var barIndicatorInterpolator = 4
    private var barIndicatorWidth = d2p(50f)
    private var barIndicatorEnabled = true
    private var barIndicatorGravity = 1    // 0 -> TOP , 1 -> BOTTOM
    private var itemIconSize = d2p(18f)
    public var itemIconMargin = d2p(3f)
    private var itemTextColor = Color.parseColor("#444444")
    private var itemTextColorActive = Color.parseColor("#426dfe")
    private var itemTextSize = d2p(11.0f)
    private var itemBadgeColor = itemTextColorActive
    private var itemFontFamily = 0
    private var activeItem = 0
    private var prevOffset=0
    private var longPressTime = 500//represent long press time,when press time > longPressTime call the function callback.onItemLongClick

    private var items = listOf<BottomBarItem>()
    private var callback: BottomBarCallback? = null

    private var currentActiveItemColor = itemTextColor
    private var indicatorLocation = 0f

    private val paintIndicator = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        strokeWidth = 10f
        color = barIndicatorColor
        strokeCap = Paint.Cap.SQUARE
    }

    private val paintText = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = itemTextColor
        textSize = itemTextSize
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val paintBadge = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        color = itemBadgeColor
        strokeWidth = 4f
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.NiceBottomBar, 0, 0)
        barBackgroundColor = typedArray.getColor(R.styleable.NiceBottomBar_backgroundColor, this.barBackgroundColor)
        barIndicatorColor = typedArray.getColor(R.styleable.NiceBottomBar_indicatorColor, this.barIndicatorColor)
        barIndicatorWidth = typedArray.getDimension(R.styleable.NiceBottomBar_indicatorWidth, this.barIndicatorWidth)
        barIndicatorEnabled = typedArray.getBoolean(R.styleable.NiceBottomBar_indicatorEnabled, this.barIndicatorEnabled)
        itemTextColor = typedArray.getColor(R.styleable.NiceBottomBar_textColor, this.itemTextColor)
        itemTextColorActive = typedArray.getColor(R.styleable.NiceBottomBar_textColorActive, this.itemTextColorActive)
        itemTextSize = typedArray.getDimension(R.styleable.NiceBottomBar_textSize, this.itemTextSize)
        itemIconSize = typedArray.getDimension(R.styleable.NiceBottomBar_iconSize, this.itemIconSize)
        itemIconMargin = typedArray.getDimension(R.styleable.NiceBottomBar_iconMargin, this.itemIconMargin)
        activeItem = typedArray.getInt(R.styleable.NiceBottomBar_activeItem, this.activeItem)
        barIndicatorInterpolator = typedArray.getInt(R.styleable.NiceBottomBar_indicatorInterpolator, this.barIndicatorInterpolator)
        barIndicatorGravity = typedArray.getInt(R.styleable.NiceBottomBar_indicatorGravity, this.barIndicatorGravity)
        itemBadgeColor = typedArray.getColor(R.styleable.NiceBottomBar_badgeColor, this.itemBadgeColor)
        itemFontFamily = typedArray.getResourceId(R.styleable.NiceBottomBar_itemFontFamily, this.itemFontFamily)
        items = BottomBarParser(context, typedArray.getResourceId(R.styleable.NiceBottomBar_menu, 0)).parse()
        typedArray.recycle()

        setBackgroundColor(barBackgroundColor)

        // Update default attribute values
        paintIndicator.color = barIndicatorColor
        paintText.color = itemTextColor
        paintText.textSize = itemTextSize
        paintBadge.color = itemBadgeColor

        if (itemFontFamily != 0)
            paintText.typeface = ResourcesCompat.getFont(context, itemFontFamily)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        var lastX = 0f
        val itemWidth = width / items.size

        for (item in items) {
            item.rect = RectF(lastX, 0f, itemWidth + lastX, height.toFloat())
            lastX += itemWidth
        }

        // Set initial active item
        setActiveItem(activeItem)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val textHeight = (paintText.descent() + paintText.ascent()) / 2
        val additionalTopMargin = if (barIndicatorGravity == 1) 0f else 10f // push the item components from the top a bit if the indicator is at the top
        canvas.translate(0f, 20f);
        for ((i, item) in items.withIndex()) {
            item.icon.mutate()
            var offset = (item.icon.intrinsicHeight - item.icon.intrinsicWidth)/4;
            if(i==4)
                item.icon.setBounds(item.rect.centerX().toInt() - itemIconSize.toInt() / 2,
                        height / 2 - itemIconSize.toInt() - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt(),
                        item.rect.centerX().toInt() + itemIconSize.toInt() / 2,
                        height/2  - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt())
            else if(i==1)
                item.icon.setBounds(item.rect.centerX().toInt()+3 - itemIconSize.toInt() / 2 + offset,
                        height / 2 - itemIconSize.toInt() - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt(),
                        item.rect.centerX().toInt()-6 + itemIconSize.toInt() / 2,
                        height/2  - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt())
            else
                item.icon.setBounds(item.rect.centerX().toInt() - itemIconSize.toInt() / 2 + offset,
                    height / 2 - itemIconSize.toInt() - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt(),
                    item.rect.centerX().toInt() + itemIconSize.toInt() / 2,
                    height/2  - itemIconMargin.toInt() / 2 + additionalTopMargin.toInt())

              if(i!=4)
            DrawableCompat.setTint(item.icon, if (i == activeItem) currentActiveItemColor else itemTextColor)
            item.icon.draw(canvas)

            // Draw item title
            this.paintText.color = if (i == activeItem) currentActiveItemColor else itemTextColor

            if (!TextUtils.isEmpty(item.title) ) {
                canvas.drawText(item.title, item.rect.centerX(),
                        item.rect.centerY() - textHeight + itemIconSize / 2 + (this.itemIconMargin / 2) + additionalTopMargin, paintText)
            }

            // Draw item badge
            if (item.badgeSize > 0)
                drawBadge(canvas, item)
        }

        // Draw indicator
        if (barIndicatorEnabled){
            var offset=5

            canvas.drawLine(indicatorLocation - barIndicatorWidth / 2 + offset, (if (barIndicatorGravity == 1) height - 22.0f else 22f),
                    indicatorLocation + barIndicatorWidth / 2 - offset, (if (barIndicatorGravity == 1) height - 22.0f else 22f), paintIndicator)

        }
    }

    public fun changeIcon(pos: Int, icon: Drawable) {
        items[pos].icon = icon;
        invalidate();
    }

    // Handle item clicks
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP && abs(event.downTime - event.eventTime) < longPressTime)
            for ((i, item) in items.withIndex())
                if (item.rect.contains(event.x, event.y))
                    if (i != this.activeItem) {
                        setActiveItem(i)
                        if (callback != null)
                            callback!!.onItemSelect(i)
                    } else if (callback != null)
                        callback!!.onItemReselect(i)
        if (event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_UP) {
            if (abs(event.downTime - event.eventTime) > longPressTime) {
                for ((i, item) in items.withIndex())
                    if (item.rect.contains(event.x, event.y))
                        if (callback != null)
                            callback!!.onItemLongClick(i)
            }
        }
        return true
    }


    // Draw item badge
    private fun drawBadge(canvas: Canvas, item: BottomBarItem) {
        paintBadge.style = Paint.Style.FILL
        paintBadge.color = itemTextColorActive
        canvas.drawCircle(item.rect.centerX() + itemIconSize / 2 - 4,
                (height / 2).toFloat() - itemIconSize - itemIconMargin / 2 + 10, item.badgeSize, paintBadge)

        paintBadge.style = Paint.Style.STROKE
        paintBadge.color = barBackgroundColor
        canvas.drawCircle(item.rect.centerX() + itemIconSize / 2 - 4,
                (height / 2).toFloat() - itemIconSize - itemIconMargin / 2 + 10, item.badgeSize, paintBadge)
    }

    // Add item badge
    fun setBadge(pos: Int) {
        if (pos > 0 && pos < items.size && items[pos].badgeSize == 0f) {
            val animator = ValueAnimator.ofFloat(0f, 15f)
            animator.duration = 100
            animator.addUpdateListener { animation ->
                items[pos].badgeSize = animation.animatedValue as Float
                invalidate()
            }
            animator.start()
        }
    }

    // Remove item badge
    fun removeBadge(pos: Int) {
        if (pos > 0 && pos < items.size && items[pos].badgeSize > 0f) {
            val animator = ValueAnimator.ofFloat(items[pos].badgeSize, 0f)
            animator.duration = 100
            animator.addUpdateListener { animation ->
                items[pos].badgeSize = animation.animatedValue as Float
                invalidate()
            }
            animator.start()
        }
    }

    fun setActiveItem(pos: Int) {
        activeItem = pos

        animateIndicator(pos)
        setItemColors()
    }

    private fun animateIndicator(pos: Int) {
        val animator = ValueAnimator.ofFloat(indicatorLocation, items[pos].rect.centerX())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            animator.interpolator = when (this.barIndicatorInterpolator) {
                0 -> AccelerateInterpolator()
                1 -> DecelerateInterpolator()
                2 -> AccelerateDecelerateInterpolator()
                3 -> AnticipateInterpolator()
                4 -> AnticipateOvershootInterpolator()
                5 -> LinearInterpolator()
                6 -> OvershootInterpolator()
                else -> AnticipateOvershootInterpolator()
            }
        }

        animator.addUpdateListener { animation ->
            indicatorLocation = animation.animatedValue as Float
            invalidate()
        }

        animator.start()
    }

    // Apply transition animation to item color
    private fun setItemColors() {
        val animator = ValueAnimator.ofObject(ArgbEvaluator(), itemTextColor, itemTextColorActive)
        animator.addUpdateListener { currentActiveItemColor = it.animatedValue as Int }
        animator.start()
    }

    private fun d2p(dp: Float): Float {
        return resources.displayMetrics.densityDpi.toFloat() / 160.toFloat() * dp
    }

    fun setBottomBarCallback(callback: BottomBarCallback) {
        this.callback = callback
    }

    fun setLongPressTime(time: Int) {
        longPressTime = time
    }


    interface BottomBarCallback {
        fun onItemSelect(pos: Int)
        fun onItemReselect(pos: Int)
        fun onItemLongClick(pos: Int)
    }
}