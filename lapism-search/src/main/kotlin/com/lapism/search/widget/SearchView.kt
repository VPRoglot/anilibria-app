package com.lapism.search.widget

import android.animation.LayoutTransition
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.transition.*
import com.lapism.search.R
import com.lapism.search.SearchUtils
import com.lapism.search.behavior.SearchBehavior
import com.lapism.search.internal.SearchLayout
import dev.rx.iosanimation.ChangeElevation
import dev.rx.iosanimation.ChangeOutlineRadius


class SearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : SearchLayout(context, attrs, defStyleAttr, defStyleRes), CoordinatorLayout.AttachedBehavior {

    // *********************************************************************************************
    private var mBehavior: CoordinatorLayout.Behavior<*> = SearchBehavior<SearchView>()

    // *********************************************************************************************
    init {
        inflate(context, R.layout.search_view, this)
        init()

        val a = context.obtainStyledAttributes(
            attrs, R.styleable.SearchView, defStyleAttr, defStyleRes
        )
        navigationIconSupport =
            a.getInteger(
                R.styleable.SearchView_search_navigation_icon_support,
                SearchUtils.NavigationIconSupport.ANIMATION
            )
        a.recycle()

        // TODO - MORE ATTRIBUTTES IN THE FUTURE RELEASE
        setClearIconImageResource(R.drawable.search_ic_outline_clear_24px)
        mViewShadow?.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.search_shadow
            )
        )

        setDefault()

        val transition = LayoutTransition()
        transition.enableTransitionType(LayoutTransition.CHANGING)
        transition.setDuration(getAnimationDuration())
        transition.addTransitionListener(object : LayoutTransition.TransitionListener {
            override fun startTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }

            override fun endTransition(
                transition: LayoutTransition?,
                container: ViewGroup?,
                view: View?,
                transitionType: Int
            ) {

            }
        })

        val frameLayout = findViewById<FrameLayout>(R.id.search_frameLayout)
        this.layoutTransition = transition
    }

    // *********************************************************************************************
    fun setBehavior(behavior: CoordinatorLayout.Behavior<*>) {
        mBehavior = behavior
    }

    // *********************************************************************************************
    private fun setDefault() {
        margins = SearchUtils.Margins.TOOLBAR
        elevation =
            context.resources.getDimensionPixelSize(R.dimen.search_elevation).toFloat()
        setBackgroundRadius(resources.getDimensionPixelSize(R.dimen.search_shape_rounded).toFloat())
        setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height))
        mSearchEditText?.setPadding(0, 0, 0, 0)
    }

    class SuperTransition : TransitionSet() {
        init {
            //addTransition(Fade(Fade.OUT))
            addTransition(ChangeBounds())
            addTransition(ChangeTransform())
            addTransition(ChangeElevation())
            addTransition(ChangeOutlineRadius())
            //addTransition(Fade(Fade.IN))
            interpolator = AccelerateDecelerateInterpolator()
        }
    }

    // *********************************************************************************************
    override fun addFocus() {
        val frameLayout = findViewById<FrameLayout>(R.id.search_frameLayout)
        TransitionManager.beginDelayedTransition(frameLayout, SuperTransition())
        mOnFocusChangeListener?.onFocusChange(true)
        filter("")

        mViewShadow?.visibility = View.VISIBLE
        setBackgroundRadius(resources.getDimensionPixelSize(R.dimen.search_shape_none).toFloat())
        margins = SearchUtils.Margins.NONE_TOOLBAR
        mViewDivider?.visibility = View.VISIBLE
        animateHamburgerToArrow(false)
        elevation = context.resources.getDimensionPixelSize(R.dimen.search_elevation_focus).toFloat()
        val paddingLeftRight = context.resources.getDimensionPixelSize(R.dimen.search_key_line_16)
        val paddingTop = context.resources.getDimensionPixelSize(R.dimen.search_key_line_4)
        //mSearchEditText?.setPadding(paddingLeftRight, 0, paddingLeftRight, 0)
        mSearchEditText?.layoutParams =
            (mSearchEditText?.layoutParams as MarginLayoutParams?)?.apply {
                marginEnd = paddingLeftRight
                marginStart = paddingLeftRight
            }
        mLinearLayout?.layoutParams = (mLinearLayout?.layoutParams as MarginLayoutParams?)?.apply {
            topMargin = paddingTop
        }

        //setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height_focus))
        showAdapter()

        showKeyboard()
    }

    override fun removeFocus() {
        val frameLayout = findViewById<FrameLayout>(R.id.search_frameLayout)
        TransitionManager.beginDelayedTransition(frameLayout, SuperTransition())
        hideAdapter()

        mViewShadow?.visibility = View.GONE
        animateArrowToHamburger(false)

        mViewDivider?.visibility = View.GONE
        margins = SearchUtils.Margins.TOOLBAR

        setBackgroundRadius(resources.getDimensionPixelSize(R.dimen.search_shape_rounded).toFloat())
        //setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height))
        //mSearchEditText?.setPadding(0, 0, 0, 0)
        mSearchEditText?.layoutParams =
            (mSearchEditText?.layoutParams as MarginLayoutParams)?.apply {
                marginEnd = 0
                marginStart = 0
            }
        mLinearLayout?.layoutParams = (mLinearLayout?.layoutParams as MarginLayoutParams?)?.apply {
            topMargin = 0
        }


        mOnFocusChangeListener?.onFocusChange(false)

        elevation = context.resources.getDimensionPixelSize(R.dimen.search_elevation).toFloat()
        hideKeyboard()
    }

    private fun addFocus2() {
        mOnFocusChangeListener?.onFocusChange(true)
        filter("")

        mViewShadow?.visibility = View.VISIBLE
        animateHamburgerToArrow(false)
        mViewDivider?.visibility = View.VISIBLE

        val paddingLeftRight =
            context.resources.getDimensionPixelSize(R.dimen.search_key_line_8)
        mSearchEditText?.setPadding(paddingLeftRight, 0, paddingLeftRight, 0)
        setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height_focus))
        setBackgroundRadius(resources.getDimensionPixelSize(R.dimen.search_shape_none).toFloat())
        margins = SearchUtils.Margins.NONE_TOOLBAR
        elevation =
            context.resources.getDimensionPixelSize(R.dimen.search_elevation_focus).toFloat()

        showAdapter()
    }

    private fun removeFocus2() {
        mViewShadow?.visibility = View.GONE
        animateArrowToHamburger(false)
        hideKeyboard()
        hideAdapter()
        mViewDivider?.visibility = View.GONE
        margins = SearchUtils.Margins.TOOLBAR
        elevation = context.resources.getDimensionPixelSize(R.dimen.search_elevation).toFloat()
        setBackgroundRadius(resources.getDimensionPixelSize(R.dimen.search_shape_rounded).toFloat())
        setLayoutHeight(context.resources.getDimensionPixelSize(R.dimen.search_layout_height))
        mSearchEditText?.setPadding(0, 0, 0, 0)

        mOnFocusChangeListener?.onFocusChange(false)
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*> {
        return mBehavior
    }

}