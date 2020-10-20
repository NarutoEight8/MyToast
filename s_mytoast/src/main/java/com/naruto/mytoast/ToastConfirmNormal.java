package com.naruto.mytoast;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;


/**
 * 确认框,可以非主线程
 */
//new ToastConfirmNormal(GlobalContext.getCurrentActivity(), null,true)
//        .withTitle("重要提醒")
//        .withInfo("无网络信号时手机无法控制车辆,请务必携带遥控器用车!")
//        .withButton("知道了", "下次不再提醒")
//        .withClick(new ToastConfirmNormal.OnButtonClickListener() {
public class ToastConfirmNormal extends RelativeLayout {
    private static boolean isShowing = false;
    private TextView txt_title, txt_info, btn_cancel,btn_center,btn_right, btn_confirm;
    private RelativeLayout lin_views;
    private LinearLayout lin_shows;

    private OnButtonClickListener listener;
    private OnButtonClickCenterListener listenerCenter;
    private OnButtonClickRightListener listenerRight;
    private static WeakReference<Activity> sCurrentActivityWeakRef;//activity的根View
//    private Activity useActivity;//activity的根View
    private ViewGroup decorViewGroup;//activity的根View

    public static ToastConfirmNormal ToastConfirmNormalThis;//外部用来判断是否弹出了窗
    // ===================================================
    public ToastConfirmNormal(Activity contextActivity, AttributeSet attrs) {
        super(contextActivity, attrs);
        if (contextActivity == null) return;
        if (sCurrentActivityWeakRef != null) sCurrentActivityWeakRef.clear();
        sCurrentActivityWeakRef = new WeakReference<Activity>(contextActivity);
//        this.useActivity = contextActivity;
        LayoutInflater.from(contextActivity).inflate(R.layout.toast_confirm_layout, this, true);
        lin_shows = (LinearLayout) findViewById(R.id.lin_shows);
        lin_views = (RelativeLayout) findViewById(R.id.lin_views);
        txt_title = (TextView) findViewById(R.id.txt_title);
        txt_info = (TextView) findViewById(R.id.txt_text);
        btn_cancel = (TextView) findViewById(R.id.btn_cancel);
        btn_center = findViewById(R.id.btn_center);
        btn_right = findViewById(R.id.btn_right);
        btn_confirm = (TextView) findViewById(R.id.btn_confirm);
        initViews();
        initEvent();
    }
    @Override
    protected void onAttachedToWindow() {
        isShowing = true;
        super.onAttachedToWindow();
    }
    @Override
    protected void onDetachedFromWindow() {
        isShowing = false;
        super.onDetachedFromWindow();
    }
    private void initViews() {
        lin_shows.setVisibility(INVISIBLE);
        txt_title.setVisibility(GONE);
        txt_info.setVisibility(GONE);
    }
    private long preConfirmTime = 0;
    private void initEvent() {
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
                if (listener != null) listener.onClickConfirm(false);
            }
        });
        btn_center.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
                if (listenerCenter != null) listenerCenter.onClickCenter();
            }
        });
        btn_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                exit();
                if (listenerRight != null) listenerRight.onClickRight();
            }
        });
        btn_confirm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if(now - preConfirmTime<3000L)return;
                preConfirmTime = now;
                exit();
                TimerTask task = new TimerTask() {
                    public void run() {
                        sCurrentActivityWeakRef.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (listener != null) listener.onClickConfirm(true);
                            }
                        });
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1000L);
            }
        });
        lin_shows.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //不要点了消失了
            }
        });
    }
    private void exit() {
        hideSystemKeyBoard();
        popView(true, new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                if (decorViewGroup != null) {
                    decorViewGroup.removeView(ToastConfirmNormal.this);
                    decorViewGroup = null;
                    ToastConfirmNormalThis = null;
                    Log.e("TOAST", "decorViewGroup removed ConfirmView");
                } else {
                    Log.e("TOAST", "decorViewGroup null");
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
    private void hideSystemKeyBoard(){
        if(lin_views.getChildCount() == 0)return;
        InputMethodManager imm = (InputMethodManager) lin_views.getChildAt(0).getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        boolean isOpen = imm.isActive();
        if (isOpen) {
            imm.hideSoftInputFromWindow(lin_views.getChildAt(0).getWindowToken(), 0);
        }
    }
    // ===================================================
    public enum TypeStyle{
        gredientGray,
        gredientSliver
    }
    public ToastConfirmNormal withStyle(TypeStyle type) {
        if(type == TypeStyle.gredientGray){
            lin_shows.setBackgroundResource(R.drawable.gredient_gray_round);
        }else if(type == TypeStyle.gredientSliver){
            lin_shows.setBackgroundResource(R.drawable.gredient_sliver_round);
        }
        return this;
    }
    public ToastConfirmNormal withTitle(CharSequence title) {
        if (title != null && title.length() > 0) {
            txt_title.setVisibility(VISIBLE);
            txt_title.setText(title);
        }
        return this;
    }
    public ToastConfirmNormal withInfo(CharSequence info) {
        if (info != null && info.length() > 0) {
            txt_info.setVisibility(VISIBLE);
            txt_info.setText(info);
        }
        return this;
    }
    public ToastConfirmNormal withAddView(ViewGroup view) {
        if (view != null) {
            lin_views.setVisibility(VISIBLE);
            lin_views.addView(view);
        }
        return this;
    }
    public ToastConfirmNormal withAddView(View view) {
        if (view != null) {
            lin_views.setVisibility(VISIBLE);
            lin_views.addView(view);
        }
        return this;
    }
    /**
     * 有一个为空，或空字符，就只显示一个按扭
     */
    public ToastConfirmNormal withButton(String cancle, String confirm) {
        if (TextUtils.isEmpty(cancle))btn_cancel.setVisibility(View.GONE);
        else btn_cancel.setText(cancle);
        if (TextUtils.isEmpty(confirm)) btn_confirm.setVisibility(View.GONE);
        else btn_confirm.setText(confirm);
        return this;
    }
    public ToastConfirmNormal withClick(OnButtonClickListener listener) {
        this.listener = listener;
        return this;
    }
    public ToastConfirmNormal withClickCenter(String centerName, OnButtonClickCenterListener listener) {
        this.listenerCenter = listener;
        btn_center.setVisibility(VISIBLE);
        btn_center.setText(centerName);
        return this;
    }
    public ToastConfirmNormal withClickRight(String rightName, OnButtonClickRightListener listener) {
        this.listenerRight = listener;
        btn_right.setVisibility(VISIBLE);
        btn_right.setText(rightName);
        return this;
    }
    private long preTime = 0;
    public void show() {
        if(isShowing)return;
        long now = System.currentTimeMillis();
        if (now - preTime < 2000) return;
        preTime = now;
        sCurrentActivityWeakRef.get().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (decorViewGroup == null) {
                    Log.e("TOASTNormal", "show");
                    View decorView = sCurrentActivityWeakRef.get().getWindow().getDecorView();
                    if (decorView == null) return;
                    decorViewGroup = (ViewGroup) decorView.findViewById(android.R.id.content);
                    decorViewGroup.addView(ToastConfirmNormal.this);
                    ToastConfirmNormalThis = ToastConfirmNormal.this;

                    lin_shows.setVisibility(VISIBLE);
//                popView(false,null);
                }else{
                    Log.e("TOASTNormal", "unshow");
                }
            }
        });
    }
    // ===================================================
    public void popView(final boolean isHide,final Animation.AnimationListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                TranslateAnimation animation ;
                //相对自身位移百分比 1f==100%
                if(isHide)animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
                else animation= new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
                animation.setInterpolator(new LinearInterpolator());//OvershootInterpolator
                animation.setFillAfter(true);
                animation.setDuration(10);
                animation.setAnimationListener(listener);
                if(lin_shows.getVisibility() == INVISIBLE)lin_shows.setVisibility(VISIBLE);
                lin_shows.startAnimation(animation);
            }
        },100L);
    }
    // ===================================================
    public interface OnButtonClickListener {
        void onClickConfirm(boolean isClickConfirm);
    }
    public interface OnButtonClickCenterListener {
        void onClickCenter();
    }
    public interface OnButtonClickRightListener {
        void onClickRight();
    }

}