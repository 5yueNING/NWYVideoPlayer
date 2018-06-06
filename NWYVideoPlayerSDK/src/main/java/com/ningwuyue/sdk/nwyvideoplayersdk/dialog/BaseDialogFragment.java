package com.ningwuyue.sdk.nwyvideoplayersdk.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ningwuyue.sdk.nwyvideoplayersdk.R;


/**
 * Created by ZHT on 2017/4/18.
 * DialogFragment基类
 * 用于显示背景透明的DialogFragment
 */
public abstract class BaseDialogFragment extends DialogFragment {
    protected View mDialogFragmentView;
    protected Context mContext;
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        Window window = dialog == null ? null : dialog.getWindow();
        if (null != dialog && null != window) {
            window.setLayout(-1, -2);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.nwyvideo_dialog);  //具有阴影效果
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE); //去除标题栏
        if (mDialogFragmentView == null) {
            mDialogFragmentView = inflater.inflate(getLayoutId(), null, false);
        }
        ViewGroup parent = (ViewGroup) mDialogFragmentView.getParent();
        if (parent != null) {
            parent.removeAllViews();
        }

        /*// 设置宽度为屏宽、居中。
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;*/
        return mDialogFragmentView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        afterCreate(savedInstanceState);
    }

    /*@Override
    public int show(FragmentTransaction transaction, String tag) {
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        transaction.add(this, tag).addToBackStack(null);

        return transaction.commitAllowingStateLoss();
        //return super.show(transaction, tag);
    }*/

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignore) {
        }
    }

    @Override
    public void dismiss() {
        if (getActivity() != null && !getActivity().isFinishing()) {
            //在dismiss的时候同理。
            try {
                super.dismissAllowingStateLoss();
            } catch (Exception e) {//在activity销毁的时候，可能出现异常
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            super.onDismiss(dialog);
        } catch (Exception e) {//在activity销毁的时候，可能出现异常
            e.printStackTrace();
        }

    }

    protected abstract int getLayoutId();

    protected abstract void afterCreate(Bundle savedInstanceState);


}
