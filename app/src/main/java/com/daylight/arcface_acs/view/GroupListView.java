package com.daylight.arcface_acs.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.widget.LinearLayout;

import com.qmuiteam.qmui.util.QMUIResHelper;
import com.qmuiteam.qmui.util.QMUIViewHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListSectionHeaderFooterView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * Created by Daylight on 2018/1/22.
 */

public class GroupListView extends LinearLayout {
    public static final int SEPARATOR_STYLE_NORMAL = 0;
    public static final int SEPARATOR_STYLE_NONE = 1;
    private int mSeparatorStyle;
    private SparseArray<GroupListView.Section> mSections;

    public GroupListView(Context context) {
        this(context, null, com.qmuiteam.qmui.R.attr.QMUIGroupListViewStyle);
    }

    public GroupListView(Context context, AttributeSet attrs) {
        this(context, attrs, com.qmuiteam.qmui.R.attr.QMUIGroupListViewStyle);
    }

    public GroupListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, com.qmuiteam.qmui.R.styleable.QMUIGroupListView, defStyleAttr, 0);
        mSeparatorStyle = array.getInt(com.qmuiteam.qmui.R.styleable.QMUIGroupListView_separatorStyle, SEPARATOR_STYLE_NORMAL);
        array.recycle();

        mSections = new SparseArray<>();
        setOrientation(LinearLayout.VERTICAL);
    }

    /**
     * 创建一个 Section。
     *
     * @return 返回新创建的 Section。
     */
    public static GroupListView.Section newSection(Context context) {
        return new GroupListView.Section(context);
    }

    public
    @GroupListView.SeparatorStyle
    int getSeparatorStyle() {
        return mSeparatorStyle;
    }

    /**
     * 设置分割线风格，具体风格可以在 {@link GroupListView.SeparatorStyle} 中选择。
     *
     * @param separatorStyle {@link #SEPARATOR_STYLE_NORMAL} 或 {@link #SEPARATOR_STYLE_NONE} 其中一个值。
     */
    public void setSeparatorStyle(@GroupListView.SeparatorStyle int separatorStyle) {
        mSeparatorStyle = separatorStyle;
    }

    public int getSectionCount() {
        return mSections.size();
    }

    public void showItemView(int sectionIndex,int itemIndex){
        mSections.get(sectionIndex).showItem(itemIndex);
    }
    public void hideItemView(int sectionIndex,int itemIndex){
        mSections.get(sectionIndex).hideItem(itemIndex);
    }

    public QMUICommonListItemView createItemView(Drawable imageDrawable, CharSequence titleText, String detailText, int orientation, int accessoryType, int height) {
        QMUICommonListItemView itemView = new QMUICommonListItemView(getContext());
        itemView.setOrientation(orientation);
        itemView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
        itemView.setImageDrawable(imageDrawable);
        itemView.setText(titleText);
        itemView.setDetailText(detailText);
        itemView.setAccessoryType(accessoryType);
        return itemView;
    }

    public QMUICommonListItemView createItemView(Drawable imageDrawable, CharSequence titleText, String detailText, int orientation, int accessoryType) {
        int height;
        if (orientation == QMUICommonListItemView.VERTICAL) {
            height = QMUIResHelper.getAttrDimen(getContext(), com.qmuiteam.qmui.R.attr.qmui_list_item_height_higher);
            return createItemView(imageDrawable, titleText, detailText, orientation, accessoryType, height);
        } else {
            height = QMUIResHelper.getAttrDimen(getContext(), com.qmuiteam.qmui.R.attr.qmui_list_item_height);
            return createItemView(imageDrawable, titleText, detailText, orientation, accessoryType, height);
        }
    }

    public QMUICommonListItemView createItemView(CharSequence titleText) {
        return createItemView(null, titleText, null, QMUICommonListItemView.HORIZONTAL, QMUICommonListItemView.ACCESSORY_TYPE_NONE);
    }

    public QMUICommonListItemView createItemView(int orientation) {
        return createItemView(null, null, null, orientation, QMUICommonListItemView.ACCESSORY_TYPE_NONE);
    }

    /**
     * private, use {@link GroupListView.Section#addTo(GroupListView)}
     * <p>这里只是把section记录到数组里面而已</p>
     */
    private void addSection(GroupListView.Section section) {
        mSections.append(mSections.size(), section);
    }

    /**
     * private，use {@link GroupListView.Section#removeFrom(GroupListView)}
     * <p>这里只是把section从记录的数组里移除而已</p>
     */
    private void removeSection(GroupListView.Section section) {
        for (int i = 0; i < mSections.size(); i++) {
            GroupListView.Section each = mSections.valueAt(i);
            if (each == section) {
                mSections.remove(i);
            }
        }
    }

    public GroupListView.Section getSection(int index) {
        return mSections.get(index);
    }

    @IntDef({SEPARATOR_STYLE_NORMAL, SEPARATOR_STYLE_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface SeparatorStyle {
    }

    /**
     * Section 是组成 {@link GroupListView} 的部分。
     * <ul>
     * <li>每个 Section 可以有多个 item, 通过 {@link #addItemView(QMUICommonListItemView, OnClickListener)} 添加。</li>
     * <li>Section 还可以有自己的一个顶部 title 和一个底部 description, 通过 {@link #setTitle(CharSequence)} 和 {@link #setDescription(CharSequence)} 设置。</li>
     * </ul>
     */
    public static class Section {
        private Context mContext;
        private QMUIGroupListSectionHeaderFooterView mTitleView;
        private QMUIGroupListSectionHeaderFooterView mDescriptionView;
        private SparseArray<QMUICommonListItemView> mItemViews;
        private boolean mUseDefaultTitleIfNone;
        private boolean mUseTitleViewForSectionSpace = true;

        private int mSeparatorDrawableForSingle = 0;
        private int mSeparatorDrawableForTop = 0;
        private int mSeparatorDrawableForBottom = 0;
        private int mSeparatorDrawableForMiddle = 0;

        public Section(Context context) {
            mContext = context;
            mItemViews = new SparseArray<>();
        }

        /**
         * 对 Section 添加一个 {@link QMUICommonListItemView}
         *
         * @param itemView        要添加的 ItemView
         * @param onClickListener ItemView 的点击事件
         * @return Section 本身,支持链式调用
         */
        public GroupListView.Section addItemView(QMUICommonListItemView itemView, OnClickListener onClickListener) {
            return addItemView(itemView, onClickListener, null);
        }

        /**
         * 对 Section 添加一个 {@link QMUICommonListItemView}
         *
         * @param itemView            要添加的 ItemView
         * @param onClickListener     ItemView 的点击事件
         * @param onLongClickListener ItemView 的长按事件
         * @return Section 本身, 支持链式调用
         */
        public GroupListView.Section addItemView(final QMUICommonListItemView itemView, OnClickListener onClickListener, OnLongClickListener onLongClickListener) {
            // 如果本身带有开关控件，点击item时要改变开关控件的状态（开关控件本身已经disable掉）
//            if (itemView.getAccessoryType() == QMUICommonListItemView.ACCESSORY_TYPE_SWITCH) {
//                itemView.setOnClickListener(v -> itemView.getSwitch().toggle());
//            } else
            if (onClickListener != null) {
                itemView.setOnClickListener(onClickListener);
            }

            if (onLongClickListener != null) {
                itemView.setOnLongClickListener(onLongClickListener);
            }

            mItemViews.append(mItemViews.size(), itemView);
            return this;
        }

        /**
         * 设置 Section 的 title
         *
         * @return Section 本身, 支持链式调用
         */
        public GroupListView.Section setTitle(CharSequence title) {
            mTitleView = createSectionHeader(title);
            return this;
        }

        /**
         * 设置 Section 的 description
         *
         * @return Section 本身, 支持链式调用
         */
        public GroupListView.Section setDescription(CharSequence description) {
            mDescriptionView = createSectionFooter(description);
            return this;
        }

        public GroupListView.Section setUseDefaultTitleIfNone(boolean useDefaultTitleIfNone) {
            mUseDefaultTitleIfNone = useDefaultTitleIfNone;
            return this;
        }

        public GroupListView.Section setUseTitleViewForSectionSpace(boolean useTitleViewForSectionSpace) {
            mUseTitleViewForSectionSpace = useTitleViewForSectionSpace;
            return this;
        }

        public GroupListView.Section setSeparatorDrawableRes(int single, int top, int bottom, int middle) {
            mSeparatorDrawableForSingle = single;
            mSeparatorDrawableForTop = top;
            mSeparatorDrawableForBottom = bottom;
            mSeparatorDrawableForMiddle = middle;
            return this;
        }

        public GroupListView.Section setSeparatorDrawableRes(int middle) {
            mSeparatorDrawableForMiddle = middle;
            return this;
        }

        /**
         * 将 Section 添加到 {@link GroupListView} 上
         */
        public void addTo(GroupListView groupListView) {
            if (mTitleView == null) {
                if (mUseDefaultTitleIfNone) {
                    setTitle("Section " + groupListView.getSectionCount());
                } else if (mUseTitleViewForSectionSpace) {
                    setTitle("");
                }
            }
            if (mTitleView != null) {
                groupListView.addView(mTitleView);
            }

            if (groupListView.getSeparatorStyle() == SEPARATOR_STYLE_NORMAL) {
                if (mSeparatorDrawableForSingle == 0) {
                    mSeparatorDrawableForSingle = com.qmuiteam.qmui.R.drawable.qmui_s_list_item_bg_with_border_double;
                }

                if (mSeparatorDrawableForTop == 0) {
                    mSeparatorDrawableForTop = com.qmuiteam.qmui.R.drawable.qmui_s_list_item_bg_with_border_double;
                }

                if (mSeparatorDrawableForBottom == 0) {
                    mSeparatorDrawableForBottom = com.qmuiteam.qmui.R.drawable.qmui_s_list_item_bg_with_border_bottom;
                }

                if (mSeparatorDrawableForMiddle == 0) {
                    mSeparatorDrawableForMiddle = com.qmuiteam.qmui.R.drawable.qmui_s_list_item_bg_with_border_bottom;
                }
            }

            final int itemViewCount = mItemViews.size();
            for (int i = 0; i < itemViewCount; i++) {
                QMUICommonListItemView itemView = mItemViews.get(i);
                int resDrawableId;
                if (groupListView.getSeparatorStyle() == SEPARATOR_STYLE_NORMAL) {
                    if (itemViewCount == 1) {
                        resDrawableId = mSeparatorDrawableForSingle;
                    } else if (i == 0) {
                        resDrawableId = mSeparatorDrawableForTop;
                    } else if (i == itemViewCount - 1) {
                        resDrawableId = mSeparatorDrawableForBottom;
                    } else {
                        resDrawableId = mSeparatorDrawableForMiddle;
                    }
                } else {
                    resDrawableId = com.qmuiteam.qmui.R.drawable.qmui_s_list_item_bg_with_border_none;
                }
                QMUIViewHelper.setBackgroundKeepingPadding(itemView, resDrawableId);
                groupListView.addView(itemView);
            }

            if (mDescriptionView != null) {
                groupListView.addView(mDescriptionView);
            }
            groupListView.addSection(this);
        }

        public void removeFrom(GroupListView parent) {
            if (mTitleView != null && mTitleView.getParent() == parent) {
                parent.removeView(mTitleView);
            }
            for (int i = 0; i < mItemViews.size(); i++) {
                QMUICommonListItemView itemView = mItemViews.get(i);
                parent.removeView(itemView);
            }
            if (mDescriptionView != null && mDescriptionView.getParent() == parent) {
                parent.removeView(mDescriptionView);
            }
            parent.removeSection(this);
        }

        public void hideItem(int index){
            mItemViews.get(index).setVisibility(GONE);
        }

        public void showItem(int index){
            mItemViews.get(index).setVisibility(VISIBLE);
        }
        /**
         * 创建 Section Header，每个 Section 都会被创建一个 Header，有 title 时会显示 title，没有 title 时会利用 header 的上下 padding 充当 Section 分隔条
         */
        public QMUIGroupListSectionHeaderFooterView createSectionHeader(CharSequence titleText) {
            return new QMUIGroupListSectionHeaderFooterView(mContext, titleText);
        }

        /**
         * Section 的 Footer，形式与 Header 相似，都是显示一段文字
         */
        public QMUIGroupListSectionHeaderFooterView createSectionFooter(CharSequence text) {
            return new QMUIGroupListSectionHeaderFooterView(mContext, text, true);
        }
    }

}
