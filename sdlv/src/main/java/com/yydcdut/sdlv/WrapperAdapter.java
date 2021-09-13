package com.yydcdut.sdlv;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;

/**
 * Created by yuyidong on 15/9/28.
 */
abstract class WrapperAdapter implements WrapperListAdapter, ItemMainLayout.OnItemSlideListenerProxy, View.OnClickListener,
        AbsListView.OnScrollListener {
    private static final int TAG_Left = 3 << 24;
    private static final int TAG_RIGHT = 4 << 24;
    /* 上下文 */
    private Context mContext;
    /* 适配器 */
    private ListAdapter mAdapter;
    /* 用户自定义参数 */
    private Menu mMenu;
    /* SDLV */
    private SlideAndDragListView mListView;
    /* 当前滑动的item的位置 */
    private int mSlideItemPosition = -1;
    /* 监听器 */
    private OnAdapterSlideListenerProxy mOnAdapterSlideListenerProxy;
    private OnAdapterButtonClickListenerProxy mOnAdapterButtonClickListenerProxy;

    public WrapperAdapter(Context context, SlideAndDragListView listView, ListAdapter adapter, Menu menu) {
        mContext = context;
        mListView = listView;
        mListView.setOnScrollListener(this);
        mAdapter = adapter;
        mMenu = menu;
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemMainLayout itemMainLayout = null;
        if (convertView == null) {
            View contentView = mAdapter.getView(position, convertView, parent);
            itemMainLayout = new ItemMainLayout(mContext);
            itemMainLayout.setParams(mMenu.getItemHeight(), mMenu.getTotalBtnLength(MenuItem.DERACTION_LEFT),
                    mMenu.getTotalBtnLength(MenuItem.DERACTION_RIGHT), mMenu.isWannaOver());
            if (mMenu.getTotalBtnLength(MenuItem.DERACTION_LEFT) > 0) {
                itemMainLayout.getItemLeftBackGroundLayout().getBackGroundImage().setBackgroundDrawable(mMenu.getItemBackGroundDrawable());
                for (int i = 0; i < mMenu.getMenuItems(MenuItem.DERACTION_LEFT).size(); i++) {
                    View v = itemMainLayout.getItemLeftBackGroundLayout().addMenuItem(mMenu.getMenuItems(MenuItem.DERACTION_LEFT).get(i));
                    v.setOnClickListener(this);
                    v.setClickable(false);
                    v.setTag(TAG_Left, i);
                }
            } else {
                itemMainLayout.getItemLeftBackGroundLayout().setVisibility(View.GONE);
            }
            if (mMenu.getTotalBtnLength(MenuItem.DERACTION_RIGHT) > 0) {
                itemMainLayout.getItemRightBackGroundLayout().getBackGroundImage().setBackgroundDrawable(mMenu.getItemBackGroundDrawable());
                for (int i = 0; i < mMenu.getMenuItems(MenuItem.DERACTION_RIGHT).size(); i++) {
                    View v = itemMainLayout.getItemRightBackGroundLayout().addMenuItem(mMenu.getMenuItems(MenuItem.DERACTION_RIGHT).get(i));
                    v.setOnClickListener(this);
                    v.setClickable(false);
                    v.setTag(TAG_RIGHT, i);
                }
            } else {
                itemMainLayout.getItemRightBackGroundLayout().setVisibility(View.GONE);
            }
            itemMainLayout.getItemCustomLayout().getBackGroundImage().setBackgroundDrawable(mMenu.getItemBackGroundDrawable());
            itemMainLayout.setOnItemSlideListenerProxy(this);
            itemMainLayout.getItemCustomLayout().addCustomView(contentView);
        } else {
            itemMainLayout = (ItemMainLayout) convertView;
            View contentView = mAdapter.getView(position, itemMainLayout.getItemCustomLayout().getCustomView(), parent);
        }
        return itemMainLayout;
    }


    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    /**
     * 设置slide滑开的item的位置
     *
     * @param position
     */
    protected void setSlideItemPosition(int position) {
        if (mSlideItemPosition != -1 && mSlideItemPosition != position) {
            returnSlideItemPosition();
        }
        if (mSlideItemPosition == position) {//已经执行过下面的操作了，就不要再去操作了。
            return;
        }
        mSlideItemPosition = position;
        ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
        for (View v : itemMainLayout.getItemLeftBackGroundLayout().getBtnViews()) {
            v.setClickable(true);
        }
    }

    /**
     * 得到当前滑开的item的位置
     *
     * @return
     */
    protected int getSlideItemPosition() {
        return mSlideItemPosition;
    }

    /**
     * 归位mSlideItemPosition，button不可点击
     */
    public void returnSlideItemPosition() {
        if (mSlideItemPosition != -1) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                itemMainLayout.scrollBack();
                for (View v : itemMainLayout.getItemLeftBackGroundLayout().getBtnViews()) {
                    v.setClickable(false);
                }
            }
            mSlideItemPosition = -1;
        }
    }

    /**
     * 通过点击位置来判断是点击到的哪个位置
     *
     * @param x
     * @return
     */
    public boolean isTriggerButtonClick(float x) {
        if (mSlideItemPosition != -1) {
            ItemMainLayout itemMainLayout = (ItemMainLayout) mListView.getChildAt(mSlideItemPosition - mListView.getFirstVisiblePosition());
            if (itemMainLayout != null) {
                int scrollX = -itemMainLayout.getItemCustomLayout().getScrollX();
                return x < scrollX ? true : false;
            }
        }
        return false;
    }

    /**
     * 设置监听器
     *
     * @param onAdapterSlideListenerProxy
     */
    public void setOnAdapterSlideListenerProxy(OnAdapterSlideListenerProxy onAdapterSlideListenerProxy) {
        mOnAdapterSlideListenerProxy = onAdapterSlideListenerProxy;
    }

    @Override
    public void onSlideOpen(View view) {
        if (mOnAdapterSlideListenerProxy != null) {
            mOnAdapterSlideListenerProxy.onSlideOpen(view, mSlideItemPosition);
        }
    }

    @Override
    public void onSlideClose(View view) {
        if (mOnAdapterSlideListenerProxy != null) {
            mOnAdapterSlideListenerProxy.onSlideClose(view, mSlideItemPosition);
        }
        //归位
        returnSlideItemPosition();
    }

    /**
     * 设置监听器
     *
     * @param onAdapterButtonClickListenerProxy
     */
    public void setOnAdapterButtonClickListenerProxy(OnAdapterButtonClickListenerProxy onAdapterButtonClickListenerProxy) {
        mOnAdapterButtonClickListenerProxy = onAdapterButtonClickListenerProxy;
    }

    @Override
    public void onClick(View v) {
        if (mOnAdapterButtonClickListenerProxy != null) {
            mOnAdapterButtonClickListenerProxy.onClick(v, mSlideItemPosition,
                    (Integer) (v.getTag(TAG_Left) != null ? v.getTag(TAG_Left) : v.getTag(TAG_RIGHT)),
                    v.getTag(TAG_Left) != null ? MenuItem.DERACTION_LEFT : MenuItem.DERACTION_RIGHT);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        //当发生滑动的时候归位
        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
            returnSlideItemPosition();
        }
        onScrollStateChangedProxy(view, scrollState);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        onScrollProxy(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    public interface OnAdapterButtonClickListenerProxy {
        void onClick(View v, int itemPosition, int buttonPosition, int direction);
    }

    public interface OnAdapterSlideListenerProxy {
        void onSlideOpen(View view, int position);

        void onSlideClose(View view, int position);
    }

    public abstract void onScrollStateChangedProxy(AbsListView view, int scrollState);

    public abstract void onScrollProxy(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);

}
