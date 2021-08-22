package com.yydcdut.sdlv;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.yydcdut.sdlv.utils.AttrsHolder;

/**
 * Created by yuyidong on 15/9/28.
 */
public class SlideAndDragListView1 extends ListView {
    /* item的btn的最大个数 */
    private static final int ITEM_BTN_NUMBER_MAX = 2;
    /* onTouch里面的状态 */
    private static final int STATE_NOTHING = -1;//抬起状态
    private static final int STATE_DOWN = 0;//按下状态
    private static final int STATE_LONG_CLICK = 1;//长点击状态
    private static final int STATE_SCROLL = 2;//SCROLL状态
    private static final int STATE_LONG_CLICK_FINISH = 3;//长点击已经触发完成
    private int mState = STATE_NOTHING;
    /* 手指放下的坐标 */
    private int mXDown;
    private int mYDown;
    /* Attrs */
    private AttrsHolder mAttrsHolder;
    /* WrapperAdapter */
    private WrapperAdapter mWrapperAdapter;


    public SlideAndDragListView1(Context context) {
        this(context, null);
    }

    public SlideAndDragListView1(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideAndDragListView1(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //-------------------------- attrs --------------------------
        mAttrsHolder = new AttrsHolder();
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.sdlv, defStyleAttr, 0);
        mAttrsHolder.itemHeight = a.getDimension(R.styleable.sdlv_item_height, getContext().getResources().getDimension(R.dimen.slv_item_height));
        mAttrsHolder.itemBackGroundDrawable = a.getDrawable(R.styleable.sdlv_item_background);
        mAttrsHolder.btnWidth = a.getDimension(R.styleable.sdlv_item_btn_width, getContext().getResources().getDimension(R.dimen.slv_item_bg_btn_width));
        mAttrsHolder.btnNumber = a.getInt(R.styleable.sdlv_item_btn_number, 2);
        if (mAttrsHolder.btnNumber > ITEM_BTN_NUMBER_MAX || mAttrsHolder.btnNumber < 0) {
            throw new IllegalArgumentException("The number of Item buttons should be in between 0 and 2 !");
        }
        mAttrsHolder.btn1Text = a.getString(R.styleable.sdlv_item_btn1_text);
        mAttrsHolder.btn2Text = a.getString(R.styleable.sdlv_item_btn2_text);
        if (!TextUtils.isEmpty(mAttrsHolder.btn2Text) && TextUtils.isEmpty(mAttrsHolder.btn1Text)) {
            throw new IllegalArgumentException("The \'item_btn2_text\' has value, but \'item_btn1_text\' dose not have value!");
        }
        mAttrsHolder.btn1Drawable = a.getDrawable(R.styleable.sdlv_item_btn1_background);
        mAttrsHolder.btn2Drawable = a.getDrawable(R.styleable.sdlv_item_btn2_background);
        mAttrsHolder.btnTextSize = a.getDimension(R.styleable.sdlv_item_btn_text_size, getContext().getResources().getDimension(R.dimen.txt_size));
        mAttrsHolder.btnTextColor = a.getColor(R.styleable.sdlv_item_btn_text_color, getContext().getResources().getColor(android.R.color.white));
        a.recycle();
        //-------------------------- attrs --------------------------
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //获取出坐标来
                mXDown = (int) ev.getX();
                mYDown = (int) ev.getY();
                //当前state状态为按下
                mState = STATE_DOWN;
                break;
            case MotionEvent.ACTION_MOVE:
                if (fingerNotMove(ev)) {//手指的范围在50以内
                    Log.i("yuyidong", "1111111111");
                } else if (fingerLeftAndRightMove(ev)) {//上下范围在50，主要检测左右滑动
                    Log.i("yuyidong", "2222222222");
                    return super.dispatchTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 上下左右不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerNotMove(MotionEvent ev) {
        return (mXDown - ev.getX() < 25 && mXDown - ev.getX() > -25 &&
                mYDown - ev.getY() < 25 && mYDown - ev.getY() > -25);
    }

    /**
     * 左右得超出50，上下不能超出50
     *
     * @param ev
     * @return
     */
    private boolean fingerLeftAndRightMove(MotionEvent ev) {
        return ((ev.getX() - mXDown > 25 || ev.getX() - mXDown < -25) &&
                ev.getY() - mYDown < 25 && ev.getY() - mYDown > -25);
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        mWrapperAdapter = new WrapperAdapter(getContext(), adapter, mAttrsHolder);
        super.setAdapter(mWrapperAdapter);
    }
}