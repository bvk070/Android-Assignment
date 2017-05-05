package com.buyhatketest.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.buyhatketest.CoupensListService;
import com.buyhatketest.FlyBitch;
import com.buyhatketest.R;
import com.buyhatketest.model.Coupen;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class CoupensAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Coupen> coupens;
    private static LayoutInflater inflater = null;

    public CoupensAdapter(Context ctx, ArrayList<Coupen> coupens) {
        this.mContext = ctx;
        this.coupens = coupens;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return coupens.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.coupen_row, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Coupen coupen = coupens.get(position);

        holder.tvCoupen.setText("" + coupen.getCoupen_code());
        holder.tvDiscount.setText("");
        holder.tvMax.setVisibility(View.GONE);

        if (coupen.isMax()) {
            holder.tvMax.setVisibility(View.VISIBLE);
        }

        Log.v("coupen discount", coupen.getCoupen_code() + ">>" + coupen.getCoupen_discount());

        if (coupen.getCoupen_discount().equals("-1")) {
            holder.tvDiscount.setText("Not Applicable !!");
        } else if (coupen.getCoupen_discount().equals("0")) {
            holder.tvDiscount.setText("");
        } else {
            holder.tvDiscount.setText("Discount: " + coupen.getCoupen_discount());
        }

        holder.btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String coupenCode = coupens.get(position).getCoupen_code();
                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(coupenCode, coupenCode);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mContext, "copied coupen code: " + coupenCode, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(mContext, FlyBitch.class);
                i.putExtra("coupen_code", coupens.get(position).getCoupen_code());
                mContext.startService(i);

                Intent intent = new Intent(mContext, CoupensListService.class);
                mContext.stopService(intent);

            }
        });

        return view;
    }

    public class ViewHolder {

        @InjectView(R.id.tvCoupen)
        TextView tvCoupen;
        @InjectView(R.id.tvDiscount)
        TextView tvDiscount;
        @InjectView(R.id.tvMax)
        TextView tvMax;
        @InjectView(R.id.btnApply)
        Button btnApply;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


}
