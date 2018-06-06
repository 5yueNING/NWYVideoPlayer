package com.ningwuyue.sdk.nwyvideoplayersdk.dialog;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.AnthologyEntity;
import com.ningwuyue.sdk.nwyvideoplayersdk.model.event.EventObject;
import com.ningwuyue.sdk.nwyvideoplayersdk.util.common.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by ${武跃} on 2017/7/3.
 * <p>
 * {一句话描述功能:----vip播放平台 退出弹框}
 */
public class AnthologyDialog extends BaseDialogFragment {
    public static final String BUNDLE_KEY_LIST = "bundle_key_list";

    private List<AnthologyEntity> mItemList = null;
    private int mCurrPos = 0;
    private int mSize;
    private boolean isOrder = false;
    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerView;
    private TextView mTv_current_anthology;


    @Override
    protected int getLayoutId() {
        return R.layout.nwyvideo_dialog_anthology;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        setCancelable(true);
        configView();
        mItemList = getArguments().getParcelableArrayList(BUNDLE_KEY_LIST);
        mSize = (mItemList == null ? 0 : mItemList.size());
        if (mItemList != null && !mItemList.isEmpty()) {
            if (mSize > 1) {
                try {
                    AnthologyEntity item1 = mItemList.get(0);
                    AnthologyEntity item2 = mItemList.get(1);
                    if (item1 != null && item2 != null) {
                        String number1 = item1.number;
                        String number2 = item2.number;
                        if (!TextUtils.isEmpty(number1) && !TextUtils.isEmpty(number2)) {
                            int num1 = Integer.valueOf(number1);
                            int num2 = Integer.valueOf(number2);
                            if (num1 >= num2) {
                                isOrder = false;
                            } else {
                                isOrder = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.getMessage();
                }
            }

            for (int i = 0; i < mSize; i++) {
                final AnthologyEntity item = mItemList.get(i);
                if (item != null) {
                    boolean isSelected = item.isSelected;
                    if (isSelected) {
                        mCurrPos = i;
                        if (mTv_current_anthology != null) {
                            String title = item.title;
                            if (!TextUtils.isEmpty(title)) {
                                mTv_current_anthology.setText(title);
                            }
                        }
                        break;
                    }
                }
            }
        }
        mRecyclerView.setHasFixedSize(true);
        mGridLayoutManager = new GridLayoutManager(mContext, 5);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        MyAdapter myAdapter = new MyAdapter();
        mRecyclerView.setAdapter(myAdapter);
    }

    private void configView() {
        RelativeLayout rl_root = mDialogFragmentView.findViewById(R.id.rl_root);
        ImageButton ibtnClose = mDialogFragmentView.findViewById(R.id.ibtn_close);
        mRecyclerView = mDialogFragmentView.findViewById(R.id.recyclerview);
        LinearLayout mLlBanner = mDialogFragmentView.findViewById(R.id.ll_banner);
        TextView tv_up_anthology = mDialogFragmentView.findViewById(R.id.tv_up_anthology);
        mTv_current_anthology = mDialogFragmentView.findViewById(R.id.tv_current_anthology);
        TextView tv_next_anthology = mDialogFragmentView.findViewById(R.id.tv_next_anthology);

        tv_up_anthology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_up_anthology(view);
            }
        });
        tv_next_anthology.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv_next_anthology(view);
            }
        });

        rl_root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
    }

    private void tv_up_anthology(View view) {
        if (mSize > 0) {
            if (isOrder) {//顺序
                if (mCurrPos == 0) {//也就是第一集
                    ToastUtils.showShort("没有上一集了");
                } else {
                    if (--mCurrPos >= 0) {
                        for (int i = 0; i < mSize; i++) {
                            mItemList.get(i).isSelected = false;
                        }
                        AnthologyEntity entity = mItemList.get(mCurrPos);
                        entity.isSelected = true;
                        dismissAllowingStateLoss();
                        EventBus.getDefault().post(new EventObject(EventObject.TAG_ANTHOLOGYDIALOG, mCurrPos));
                    }
                }
            } else {//逆序
                if (mCurrPos >= mSize - 1) {//也就是第一集
                    ToastUtils.showShort("没有上一集了");
                } else {
                    if (++mCurrPos <= mSize - 1) {
                        for (int i = 0; i < mSize; i++) {
                            mItemList.get(i).isSelected = false;
                        }
                        AnthologyEntity entity = mItemList.get(mCurrPos);
                        entity.isSelected = true;
                        dismissAllowingStateLoss();
                        EventBus.getDefault().post(new EventObject(EventObject.TAG_ANTHOLOGYDIALOG, mCurrPos));
                    }
                }
            }
        }
    }

    private void tv_next_anthology(View view) {
        if (mSize > 0) {
            if (isOrder) {//顺序
                if (mCurrPos == mSize - 1) {//也就是第一集
                    ToastUtils.showShort("没有下一集了");
                } else {
                    if (++mCurrPos <= mSize - 1) {
                        for (int i = 0; i < mSize; i++) {
                            mItemList.get(i).isSelected = false;
                        }
                        AnthologyEntity entity = mItemList.get(mCurrPos);
                        entity.isSelected = true;
                        dismissAllowingStateLoss();
                        EventBus.getDefault().post(new EventObject(EventObject.TAG_ANTHOLOGYDIALOG, mCurrPos));
                    }
                }
            } else {//逆序
                if (mCurrPos == 0) {//也就是第一集
                    ToastUtils.showShort("没有下一集了");
                } else {
                    if (--mCurrPos >= 0) {
                        for (int i = 0; i < mSize; i++) {
                            mItemList.get(i).isSelected = false;
                        }
                        AnthologyEntity entity = mItemList.get(mCurrPos);
                        entity.isSelected = true;
                        dismissAllowingStateLoss();
                        EventBus.getDefault().post(new EventObject(EventObject.TAG_ANTHOLOGYDIALOG, mCurrPos));
                    }
                }
            }
        }
    }

    class MyAdapter extends RecyclerView.Adapter {
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.nwyvideo_recyitem_anthology, parent, false);
            MyViewHolder viewHolder = new MyViewHolder(inflate);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (mItemList != null) {
                final AnthologyEntity item = mItemList.get(position);
                if (item != null) {
                    boolean isSelected = item.isSelected;
                    final String number = item.number;
                    MyViewHolder viewholder = (MyViewHolder) holder;
                    final TextView tvBtn = viewholder.mTvBtn;
                    tvBtn.setText(number);
                    tvBtn.setSelected(isSelected);
                    if (isSelected) {
                        mCurrPos = position;
                        if (mGridLayoutManager != null) {
                            mGridLayoutManager.scrollToPosition(position);
                        }
                        if (mTv_current_anthology != null) {
                            String title = item.title;
                            if (!TextUtils.isEmpty(title)) {
                                mTv_current_anthology.setText(title);
                            }
                        }
                    }
                    tvBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            for (int i = 0; i < mItemList.size(); i++) {
                                mItemList.get(i).isSelected = false;
                            }
                            item.isSelected = true;
                            if (mGridLayoutManager != null) {
                                mGridLayoutManager.scrollToPosition(position);
                            }

                            tvBtn.setSelected(true);
                            notifyDataSetChanged();
                            dismissAllowingStateLoss();
                            EventBus.getDefault().post(new EventObject(EventObject.TAG_ANTHOLOGYDIALOG,position));
                        }
                    });
                }
            }
        }

        @Override
        public int getItemCount() {
            return mItemList == null ? 0 : mItemList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView mTvBtn;

            public MyViewHolder(View itemView) {
                super(itemView);
                mTvBtn = itemView.findViewById(R.id.tv_anthology_btn);
            }
        }
    }
}
