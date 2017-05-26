package com.yunzhanghu.redpacketui.ui.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.yunzhanghu.redpacketsdk.constant.RPConstant;
import com.yunzhanghu.redpacketui.R;

import java.lang.reflect.Field;


/**
 * Created by max on 16/1/22
 */
public class PayTipsDialogFragment extends DialogFragment implements View.OnClickListener {

    private final static String ARGS_PAY_STATUS = "pay_status";

    private final static String ARGS_MESSAGE = "message";

    private Context mContext;

    private String mMessage;

    private String mPayStatus = "-1";

    private OnDialogConfirmClickCallback mCallback;

    private ConstraintSet mConstraintSet;

    private ConstraintLayout mConstraintLayout;

    public static PayTipsDialogFragment newInstance(String status, String message) {
        PayTipsDialogFragment payTipsDialogFragment = new PayTipsDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARGS_MESSAGE, message);
        bundle.putString(ARGS_PAY_STATUS, status);
        payTipsDialogFragment.setArguments(bundle);
        return payTipsDialogFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //去掉Dialog的Title
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPayStatus = getArguments().getString(ARGS_PAY_STATUS);
            mMessage = getArguments().getString(ARGS_MESSAGE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        keepFontSize();//保持字体不变
        return inflater.inflate(R.layout.rp_pay_tips_dialog_dev, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        TextView buttonOk = (TextView) view.findViewById(R.id.btn_ok);
        buttonOk.setOnClickListener(this);
        TextView buttonCancel = (TextView) view.findViewById(R.id.btn_cancel);
        buttonCancel.setOnClickListener(this);
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
        TextView tvMessage = (TextView) view.findViewById(R.id.tv_msg);
        String titleText = mContext.getString(R.string.hint_title);
        mConstraintLayout = (ConstraintLayout) view.findViewById(R.id.constraint_main);
        mConstraintSet = new ConstraintSet();
        mConstraintSet.clone(mContext, R.layout.rp_pay_tips_dialog_dev);
        switch (mPayStatus) {
            case RPConstant.CLIENT_CODE_OTHER_ERROR:
                setConstraintAttr();
                buttonOk.setText(R.string.btn_know);
                break;
            case RPConstant.CLIENT_CODE_ALI_NO_AUTHORIZED:
                titleText = mContext.getString(R.string.str_authorized_bind_ali_title);
                buttonCancel.setText(R.string.btn_cancel);
                buttonOk.setText(R.string.str_authorized);
                break;
            case RPConstant.CLIENT_CODE_CHECK_ALI_ORDER_ERROR:
                titleText = mContext.getString(R.string.str_heck_ali_order_error_title);
                setConstraintAttr();
                buttonOk.setText(R.string.btn_know);
                break;
            case RPConstant.CLIENT_CODE_ALI_PAY_CANCEL:
                titleText = mContext.getString(R.string.str_ali_cancel_pay_title);
                buttonOk.setText(R.string.btn_know);
                setConstraintAttr();
                break;
            case RPConstant.CLIENT_CODE_ALI_PAY_FAIL:
                titleText = mContext.getString(R.string.str_ali_pay_fail_title);
                buttonOk.setText(R.string.btn_know);
                setConstraintAttr();
                break;
            case RPConstant.CLIENT_CODE_ALI_AUTH_SUCCESS:
                mConstraintSet.setVisibility(R.id.tv_title,ConstraintSet.GONE);
                buttonOk.setText(R.string.btn_know);
                setConstraintAttr();
                break;
            case RPConstant.CLIENT_CODE_AD_SHARE_SUCCESS:
                mConstraintSet.setVisibility(R.id.tv_title,ConstraintSet.GONE);
                buttonOk.setText(R.string.ad_share);
                break;
            case RPConstant.CLIENT_CODE_UNBIND_ALI_ACCOUNT:
                titleText = getString(R.string.tip_title_unbind_ali);
                buttonOk.setText(getString(R.string.btn_unbind));
                buttonOk.setTextColor(ContextCompat.getColor(mContext, R.color.rp_top_red_color));
                buttonCancel.setText(R.string.btn_cancel);
                break;
            default:
                buttonOk.setText(R.string.btn_know);
                setConstraintAttr();
                break;
        }
        tvTitle.setText(titleText);
        tvMessage.setText(mMessage);
    }

    private void setConstraintAttr() {
        mConstraintSet.setVisibility(R.id.btn_cancel, ConstraintSet.GONE);
        mConstraintSet.setVisibility(R.id.dialog_hint_divider, ConstraintSet.GONE);
        mConstraintSet.connect(R.id.btn_ok, ConstraintSet.LEFT, R.id.constraint_main, ConstraintSet.LEFT, 0);
        mConstraintSet.applyTo(mConstraintLayout);
    }

    @Override
    public void onClick(View v) {
        this.dismissAllowingStateLoss();
        if (v.getId() == R.id.btn_ok && mCallback != null) {
            switch (mPayStatus) {
                case RPConstant.CLIENT_CODE_ALI_NO_AUTHORIZED:
                    mCallback.onConfirmClick();
                    break;
                case RPConstant.CLIENT_CODE_AD_SHARE_SUCCESS:
                    mCallback.onConfirmClick();
                    break;
                case RPConstant.CLIENT_CODE_UNBIND_ALI_ACCOUNT:
                    mCallback.onConfirmClick();
                    break;
            }
        }
    }

    private void keepFontSize() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // for bug ---> java.lang.IllegalStateException: Activity has been destroyed
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setCallback(OnDialogConfirmClickCallback callback) {
        mCallback = callback;
    }

    public interface OnDialogConfirmClickCallback {
        void onConfirmClick();
    }


}
