package com.buyhatketest;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;

import com.buyhatketest.adapter.CoupensAdapter;
import com.buyhatketest.model.Coupen;
import com.buyhatketest.rest.RestClient;
import com.buyhatketest.util.Constant;
import com.buyhatketest.util.Util;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CoupensListService extends Service {

    private WindowManager windowManager;
    ViewGroup mTopView;

    /**
     * Return the communication channel to the service.  May return null if
     * clients can not bind to the service.  The returned
     * {@link IBinder} is usually for a complex interface
     * that has been <a href="{@docRoot}guide/components/aidl.html">described using
     * aidl</a>.
     * <p>
     * <p><em>Note that unlike other application components, calls on to the
     * IBinder interface returned here may not happen on the main thread
     * of the process</em>.  More information about the main thread can be found in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html">Processes and
     * Threads</a>.</p>
     *
     * @param intent The Intent that was used to bind to this service,
     *               as given to {@link Context#bindService
     *               Context.bindService}.  Note that any extras that were included with
     *               the Intent at that point will <em>not</em> be seen here.
     * @return Return an IBinder through which clients can call on to the
     * service.
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Util.isOnline(getApplicationContext())) {
            getCoupens();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeViewFromWM();
    }

    private void addViewToWM(ArrayList<Coupen> coupens) {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT);

        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        LayoutInflater li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mTopView = (ViewGroup) li.inflate(R.layout.window_manager_layout, null);

        Log.v("coupens size", ">>" + coupens.size());
        ListView lvCoupens = (ListView) mTopView.findViewById(R.id.lvCoupens);
        lvCoupens.setAdapter(new CoupensAdapter(CoupensListService.this, coupens));
        windowManager.addView(mTopView, params);

    }

    private void removeViewFromWM() {
        if (mTopView != null) windowManager.removeView(mTopView);
    }


    public void getCoupens() {

        RestClient.getCommonService().getCoupens("1", new Callback<Response>() {
            @Override
            public void success(Response response1, Response response) {

                try {
                    Log.d("response", ">>" + Util.getString(response.getBody().in()));
                    String responseStr = Util.getString(response.getBody().in());
                    String[] coupenCodes = responseStr.split("~");
                    ArrayList<Coupen> coupens = new ArrayList<Coupen>();
                    String coupenCodesOld = Util.ReadSharePrefrence(CoupensListService.this, Constant.SHRED_PR.KEY_COUPENS);
                    Gson gson = new Gson();
                    ArrayList<Coupen> coupensOld = gson.fromJson(coupenCodesOld, new TypeToken<ArrayList<Coupen>>() {
                    }.getType());
                    if (coupensOld == null) coupensOld = new ArrayList<Coupen>();
                    float max = 0;
                    int currentMaxPos = -1;
                    for (int i = 0; i < coupenCodes.length; i++) {
                        Coupen coupen = new Coupen();
                        coupen.setMax(false);
                        coupen.setCoupen_code(coupenCodes[i]);
                        coupen.setCoupen_discount("0");
                        if (coupensOld.size() > i)
                            coupen.setCoupen_discount(coupensOld.get(i).getCoupen_discount());
                        coupens.add(coupen);

                        try {
                            if (coupen.getCoupen_discount().length() > 3) {
                                String discount = coupen.getCoupen_discount().substring(3).trim();
                                float intDiscount = Float.parseFloat(discount);
                                if (intDiscount > max) {
                                    max = intDiscount;
                                    currentMaxPos = i;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.v("max dis", ">>" + max);
                    }

                    if (currentMaxPos > -1) {
                        coupens.get(currentMaxPos).setMax(true);
                    }

                    Util.WriteSharePrefrence(CoupensListService.this, Constant.SHRED_PR.KEY_COUPENS, gson.toJson(coupens));
                    addViewToWM(coupens);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

}
