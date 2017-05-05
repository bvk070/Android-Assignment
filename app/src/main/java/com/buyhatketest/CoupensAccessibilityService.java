package com.buyhatketest;


import android.accessibilityservice.AccessibilityService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.buyhatketest.util.Util;

import java.util.ArrayList;

public class CoupensAccessibilityService extends AccessibilityService {

    static final String TAG = "CoupensService";

    private AccessibilityNodeInfo btnCoupenCodeApply, etEnterCoupenCode;

    /**
     * Callback for {@link AccessibilityEvent}s.
     *
     * @param event The new event. This event is owned by the caller and cannot be used after
     *              this method returns. Services wishing to use the event after this method returns should
     *              make a copy.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.v(TAG, String.format(
                "onAccessibilityEvent: [type] %s [class] %s [package] %s [time] %s [text] %s",
                getEventType(event), event.getClassName(), event.getPackageName(),
                event.getEventTime(), getEventText(event)));

        String activityName = getActivityName(event);
        Log.v("Activity Name:", ">>" + activityName);

        boolean isDefaultEvent = (getEventType(event).equals("default"));
        boolean isText = ((getEventText(event).contains("Apply Voucher")) || ((getEventText(event).contains("Voucher Applied"))));
        boolean isCheckoutActivity = (getActivityName(event).contains("CartWishListActivity"));

        if (getEventText(event).toLowerCase().contains("Voucher not applied".toLowerCase()) && getActivityName(event).toLowerCase().contains("android.app.Dialog".toLowerCase())) {
            Util.setDiscount(CoupensAccessibilityService.this, "-1");
        } else if (getEventText(event).toLowerCase().contains("Voucher Applied".toLowerCase())) {
            String currentText = getEventText(event);
            String[] array = currentText.split("Coupon Money- ");
            if (array.length > 1) {
                String[] discountArray = array[1].split("Total");
                Util.setDiscount(CoupensAccessibilityService.this, discountArray[0]);
            }
        }

        if (!isDefaultEvent && isText) {
            getAllNodes("");
        } else {
            if (!isDefaultEvent) {
                Intent intent = new Intent(this, FlyBitch.class);
                intent.putExtra("etEnterCoupenCode", etEnterCoupenCode);
                intent.putExtra("btnCoupenCodeApply", btnCoupenCodeApply);
                stopService(new Intent(this, FlyBitch.class));
            }
        }


    }

    /**
     * Callback for interrupting the accessibility feedback.
     */
    @Override
    public void onInterrupt() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle extras = intent.getExtras();
        if (extras != null) {
            String coupenCode = extras.getString("coupen_code");
            if (coupenCode == null) coupenCode = "";
            getAllNodes(coupenCode);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, FlyBitch.class);
            intent.putExtra("etEnterCoupenCode", etEnterCoupenCode);
            intent.putExtra("btnCoupenCodeApply", btnCoupenCodeApply);
            stopService(new Intent(this, FlyBitch.class));
            return true;
        }

        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Intent intent = new Intent(this, FlyBitch.class);
            intent.putExtra("etEnterCoupenCode", etEnterCoupenCode);
            intent.putExtra("btnCoupenCodeApply", btnCoupenCodeApply);
            stopService(new Intent(this, FlyBitch.class));
            return true;
        }

        return onKeyDown(keyCode, event);
    }

    private String getEventType(AccessibilityEvent event) {
        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                return "TYPE_NOTIFICATION_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                return "TYPE_VIEW_FOCUSED";
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                return "TYPE_VIEW_LONG_CLICKED";
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                return "TYPE_VIEW_SELECTED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                return "TYPE_WINDOW_STATE_CHANGED";
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                return "TYPE_VIEW_TEXT_CHANGED";
        }
        return "default";
    }

    private String getEventText(AccessibilityEvent event) {
        StringBuilder sb = new StringBuilder();
        for (CharSequence s : event.getText()) {
            sb.append(s);
        }
        return sb.toString();
    }

    private ActivityInfo tryGetActivity(ComponentName componentName) {
        try {
            return getPackageManager().getActivityInfo(componentName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private String getActivityName(AccessibilityEvent event) {
        if (event.getPackageName() != null && event.getClassName() != null) {
            ComponentName componentName = new ComponentName(
                    event.getPackageName().toString(),
                    event.getClassName().toString()
            );

            ActivityInfo activityInfo = tryGetActivity(componentName);
            boolean isActivity = activityInfo != null;
            if (isActivity) {
                return componentName.flattenToShortString();
            }
            return componentName.flattenToShortString();
        }
        return "";
    }

    private void getAllNodes(String coupenCode) {

        Log.v("coupenCode s", ">>" + coupenCode);

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        ArrayList<AccessibilityNodeInfo> textViewNodes = new ArrayList<AccessibilityNodeInfo>();
        ArrayList<AccessibilityNodeInfo> editTextNodes = new ArrayList<AccessibilityNodeInfo>();
        ArrayList<AccessibilityNodeInfo> buttonNodes = new ArrayList<AccessibilityNodeInfo>();

        findChildTextViews(rootNode, textViewNodes);
        findChildEditTexts(rootNode, editTextNodes);
        findChildButtons(rootNode, buttonNodes);

        for (AccessibilityNodeInfo mNode : textViewNodes) {
            if (mNode.getText() == null) {
                return;
            }
            String tv1Text = mNode.getText().toString();
            Log.v("textview", ">>" + tv1Text);
            //do whatever you want with the text content...

        }

        for (AccessibilityNodeInfo mNode : editTextNodes) {
            if (mNode.getText() == null) {
                return;
            }
            String tv1Text = mNode.getText().toString();
            int id = mNode.getWindowId();
            Log.v("edittext", ">>" + tv1Text + "  :id" + id);
            //do whatever you want with the text content...

            if (coupenCode.length() > 0) {
                Bundle arguments = new Bundle();
                arguments.putCharSequence(AccessibilityNodeInfo
                        .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, coupenCode);
                mNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }

            etEnterCoupenCode = mNode;
        }

        for (AccessibilityNodeInfo mNode : buttonNodes) {
            if (mNode.getText() == null) {
                return;
            }
            String tv1Text = mNode.getText().toString();
            int id = mNode.getWindowId();
            Log.v("button", ">>" + tv1Text + "  :id" + id);
            //do whatever you want with the text content...

            if (coupenCode.length() > 0)
                mNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);

            btnCoupenCodeApply = mNode;
        }

        if (coupenCode.length() > 0) {
            Log.v("btnCoupenCodeApply s", ">>" + btnCoupenCodeApply);
            Log.v("etEnterCoupenCode s", ">>" + etEnterCoupenCode);

//            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText(coupenCode, coupenCode);
//            clipboard.setPrimaryClip(clip);
//            Bundle arguments = new Bundle();
//            arguments.putCharSequence(AccessibilityNodeInfo
//                    .ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, coupenCode);
//            etEnterCoupenCode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
//            btnCoupenCodeApply.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            Intent intent = new Intent(this, FlyBitch.class);
            intent.putExtra("etEnterCoupenCode", etEnterCoupenCode);
            intent.putExtra("btnCoupenCodeApply", btnCoupenCodeApply);
            startService(intent);
        }

    }

    private void findChildTextViews(AccessibilityNodeInfo parentView, ArrayList<AccessibilityNodeInfo> textViewNodes) {
        if (parentView == null || parentView.getClassName() == null) {
            return;
        }

        int childCount = parentView.getChildCount();

        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.TextView"))) {
            textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildTextViews(parentView.getChild(i), textViewNodes);
            }
        }
    }

    private void findChildEditTexts(AccessibilityNodeInfo parentView, ArrayList<AccessibilityNodeInfo> editTextNodes) {
        if (parentView == null || parentView.getClassName() == null) {
            return;
        }

        int childCount = parentView.getChildCount();

        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.EditText"))) {
            editTextNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildEditTexts(parentView.getChild(i), editTextNodes);
            }
        }
    }

    private void findChildButtons(AccessibilityNodeInfo parentView, ArrayList<AccessibilityNodeInfo> buttonNodes) {
        if (parentView == null || parentView.getClassName() == null) {
            return;
        }

        int childCount = parentView.getChildCount();

        if (childCount == 0 && (parentView.getClassName().toString().contentEquals("android.widget.Button"))) {
            buttonNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildButtons(parentView.getChild(i), buttonNodes);
            }
        }
    }


}
