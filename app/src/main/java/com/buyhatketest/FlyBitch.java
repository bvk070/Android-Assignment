package com.buyhatketest;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.ImageView;

public class FlyBitch extends Service {

    private WindowManager windowManager;
    private ImageView chatHead;

    private AccessibilityNodeInfo btnCoupenCodeApply, etEnterCoupenCode;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (btnCoupenCodeApply == null) {
                btnCoupenCodeApply = (AccessibilityNodeInfo) extras.get("btnCoupenCodeApply");
                etEnterCoupenCode = (AccessibilityNodeInfo) extras.get("etEnterCoupenCode");
            } else {
                String coupenCode = extras.getString("coupen_code");
                Log.v("coupenCode service", ">>" + coupenCode);

                Intent i = new Intent(this, CoupensAccessibilityService.class);
                i.putExtra("coupen_code", coupenCode);
                startService(i);

            }
        }

        Log.v("btnCoupenCodeApply", ">>" + btnCoupenCodeApply);
        Log.v("etEnterCoupenCode", ">>" + etEnterCoupenCode);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        chatHead = new ImageView(this);

        chatHead.setImageResource(R.mipmap.ic_launcher);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(chatHead, params);

        try {

            chatHead.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(FlyBitch.this, CoupensListService.class);
                    startService(intent);

                }
            });

            chatHead.setOnTouchListener(new View.OnTouchListener() {
                private WindowManager.LayoutParams paramsF = params;
                private int initialX;
                private int initialY;
                private float initialTouchX;
                private float initialTouchY;

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            // Get current time in nano seconds.

                            initialX = paramsF.x;
                            initialY = paramsF.y;
                            initialTouchX = event.getRawX();
                            initialTouchY = event.getRawY();
                            break;
                        case MotionEvent.ACTION_UP:
                            break;
                        case MotionEvent.ACTION_MOVE:
                            paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                            paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                            windowManager.updateViewLayout(chatHead, paramsF);
                            break;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatHead != null) windowManager.removeView(chatHead);
    }

}
