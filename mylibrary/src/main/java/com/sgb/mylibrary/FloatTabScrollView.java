package com.sgb.mylibrary;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * Created by panda on 16/10/10 下午2:36.
 */
public class FloatTabScrollView extends NestedScrollView implements View.OnClickListener {

    private static final int DEFAULT_DURATION = 300;
    private static final int OVERFLING_SLOP = 5;
    private static final int MIN_ALPHA = 0;
    private static final int MAX_ALPHA = 255;

    private Context mContext;

    private View mToolbar;
    /**
     * 开启toolbar渐变模式
     */
    private boolean bGradient;

    //custom attrs
    private String mTabTitle1;
    private String mTabTitle2;
    private String mTabTitle3;

    private int mTopLayoutId;
    private int mPage1LayoutId;
    private int mPage2LayoutId;
    private int mPage3LayoutId;
    private int mBottomLayoutId;

    private int mSelectedTabColor;
    private int mUnselectedTabColor;

    //
    private FrameLayout mTopContainerFL;
    private FrameLayout mPage1ContainerFL;
    private FrameLayout mPage2ContainerFL;
    private FrameLayout mPage3ContainerFL;
    private FrameLayout mBottomContainerFL;

    private View mTabHolderView;
    private LinearLayout mTabLL;
    private View mTabLine;
    private TextView mTabTV1;
    private TextView mTabTV2;
    private TextView mTabTV3;

    private ValueAnimator mTabLineAnimator;

    public FloatTabScrollView(Context context) {
        this(context, null);
    }

    public FloatTabScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatTabScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        inflate(context, R.layout.layout_float_tab_scrollview, this);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FloatTabScrollView, 0, 0);
        if (ta != null) {
            if (ta.hasValue(R.styleable.FloatTabScrollView_tab1_title)) {
                mTabTitle1 = ta.getString(R.styleable.FloatTabScrollView_tab1_title);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_tab2_title)) {
                mTabTitle2 = ta.getString(R.styleable.FloatTabScrollView_tab2_title);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_tab3_title)) {
                mTabTitle3 = ta.getString(R.styleable.FloatTabScrollView_tab3_title);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_top_layout)) {
                mTopLayoutId = ta.getResourceId(R.styleable.FloatTabScrollView_top_layout, 0);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_page1_layout)) {
                mPage1LayoutId = ta.getResourceId(R.styleable.FloatTabScrollView_page1_layout, 0);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_page2_layout)) {
                mPage2LayoutId = ta.getResourceId(R.styleable.FloatTabScrollView_page2_layout, 0);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_page3_layout)) {
                mPage3LayoutId = ta.getResourceId(R.styleable.FloatTabScrollView_page3_layout, 0);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_bottom_layout)) {
                mBottomLayoutId = ta.getResourceId(R.styleable.FloatTabScrollView_bottom_layout, 0);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_selected_tab_color)) {
                mSelectedTabColor = ta.getColor(R.styleable.FloatTabScrollView_selected_tab_color, 0xFFFF4081);
            }
            if (ta.hasValue(R.styleable.FloatTabScrollView_unselected_tab_color)) {
                mUnselectedTabColor = ta.getColor(R.styleable.FloatTabScrollView_unselected_tab_color, 0xFF000000);
            }

            ta.recycle();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTopContainerFL = (FrameLayout) findViewById(R.id.fl_top);
        mPage1ContainerFL = (FrameLayout) findViewById(R.id.fl_page1);
        mPage2ContainerFL = (FrameLayout) findViewById(R.id.fl_page2);
        mPage3ContainerFL = (FrameLayout) findViewById(R.id.fl_page3);
        mBottomContainerFL = (FrameLayout) findViewById(R.id.fl_bottom);

        inflate(mContext, mTopLayoutId, mTopContainerFL);
        inflate(mContext, mPage1LayoutId, mPage1ContainerFL);
        inflate(mContext, mPage2LayoutId, mPage2ContainerFL);
        inflate(mContext, mPage3LayoutId, mPage3ContainerFL);
        inflate(mContext, mBottomLayoutId, mBottomContainerFL);

        mTabHolderView = findViewById(R.id.view_tab_holder);
        mTabLL = (LinearLayout) findViewById(R.id.ll_tab);
        mTabLine = findViewById(R.id.view_tab_line);
        mTabLine.setBackgroundColor(mSelectedTabColor);

        mTabTV1 = (TextView) findViewById(R.id.tv_tab1);
        mTabTV2 = (TextView) findViewById(R.id.tv_tab2);
        mTabTV3 = (TextView) findViewById(R.id.tv_tab3);
        mTabTV1.setText(mTabTitle1);
        mTabTV2.setText(mTabTitle2);
        mTabTV3.setText(mTabTitle3);

        mTabTV1.setOnClickListener(this);
        mTabTV2.setOnClickListener(this);
        mTabTV3.setOnClickListener(this);

        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                setTabViewPosition(0);
                if (bGradient) {
                    mToolbar.getBackground().setAlpha(MIN_ALPHA);
                }
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    @Override
    protected void onScrollChanged(int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        super.onScrollChanged(scrollX, scrollY, oldScrollX, oldScrollY);

        setTabViewPosition(scrollY);

        if (bGradient) {
            float alpha = scrollY / (float) (mTabHolderView.getTop() - mToolbar.getHeight()) * MAX_ALPHA;
            mToolbar.getBackground().setAlpha(Math.min((int) alpha, MAX_ALPHA));
        }
    }

    private void setTabViewPosition(int scrollY) {

        if (scrollY <= mPage3ContainerFL.getTop() + mPage3ContainerFL.getHeight() - mTabLL.getHeight()) {
            //设置tabview在scrollview中的位置
            mTabLL.setTranslationY(Math.max(scrollY + (mToolbar != null ? mToolbar.getHeight() : 0), mTabHolderView.getTop()));

            //滚动过程中切换tab的选中状态
            if (scrollY < mPage2ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0)) {
                selectTab(0);
            } else if (scrollY >= mPage2ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0)
                    && scrollY < mPage3ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0)) {
                selectTab(1);
            } else if (scrollY >= mPage3ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0)) {
                selectTab(2);
            }
        } else {
            //超过最大高度之后,把tabview设置在原始位置,,,,,,也可以不设置
            mTabLL.setTranslationY(mTabHolderView.getTop());
        }
    }

    private void selectTab(int index) {

        if (mTabLineAnimator == null) {
            mTabLineAnimator = new ValueAnimator();
            mTabLineAnimator.setDuration(DEFAULT_DURATION);
            mTabLineAnimator.setInterpolator(new FastOutSlowInInterpolator());
            mTabLineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mTabLine.setTranslationX((int) animation.getAnimatedValue());
                }
            });
        }

        int curTabLineX = (int) mTabLine.getX();
        int needTabLineX = 0;

        switch (index) {
            case 0:
                needTabLineX = 0;
                mTabTV1.setTextColor(mSelectedTabColor);
                mTabTV2.setTextColor(mUnselectedTabColor);
                mTabTV3.setTextColor(mUnselectedTabColor);
                break;
            case 1:
                needTabLineX = mTabLL.getWidth() / 3;
                mTabTV1.setTextColor(mUnselectedTabColor);
                mTabTV2.setTextColor(mSelectedTabColor);
                mTabTV3.setTextColor(mUnselectedTabColor);
                break;
            case 2:
                needTabLineX = mTabLL.getWidth() / 3 * 2;
                mTabTV1.setTextColor(mUnselectedTabColor);
                mTabTV2.setTextColor(mUnselectedTabColor);
                mTabTV3.setTextColor(mSelectedTabColor);
                break;
            default:
                break;
        }

        if (curTabLineX == needTabLineX) {
            return;
        }
        mTabLineAnimator.setIntValues(curTabLineX, needTabLineX);
        if (!mTabLineAnimator.isRunning()) {
            mTabLineAnimator.start();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mTabTV1) {
            smoothScrollTo(0, mPage1ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0) + OVERFLING_SLOP);
        } else if (v == mTabTV2) {
            smoothScrollTo(0, mPage2ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0) + OVERFLING_SLOP);
        } else if (v == mTabTV3) {
            smoothScrollTo(0, mPage3ContainerFL.getTop() - mTabLL.getHeight() - (mToolbar != null ? mToolbar.getHeight() : 0) + OVERFLING_SLOP);
        }
    }

    public void setToolbar(View toolbar, boolean isGradient) {
        this.mToolbar = toolbar;
        this.bGradient = isGradient;
    }
}
