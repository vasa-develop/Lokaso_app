package com.lokaso.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lokaso.R;
import com.lokaso.model.Interest;
import com.lokaso.model.Profile;
import com.lokaso.preference.MyPreferencesManager;
import com.lokaso.retrofit.RestClient;
import com.lokaso.retromodel.RetroResponse;
import com.lokaso.util.Constant;
import com.lokaso.util.MyFont1;
import com.lokaso.util.MyLog;
import com.lokaso.util.MyToast;
import com.lokaso.util.NetworkConnection;
import com.lokaso.util.Picaso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Lax on 05-Jan-17.
 */
public class FilterInterestListAdapter extends RecyclerView.Adapter<FilterInterestListAdapter.ViewHolder> {

    private String TAG = FilterInterestListAdapter.class.getSimpleName();
    private Context context;
    private List<Interest> list;

    private NetworkConnection networkConnection;

    public FilterInterestListAdapter(Context context, List<Interest> list) {
        this.context = context;
        this.list = list;
        networkConnection = new NetworkConnection(context);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void refresh(List<Interest> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_checkbox, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Interest interest = list.get(position);

        holder.checkBoxText.setText(interest.getName());
        holder.checkBox.setChecked(interest.isChecked());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.checkBox.setChecked(!holder.checkBox.isChecked());
                if (clickInterface != null) {
                    clickInterface.onItemClick(position, holder.checkBox.isChecked());
                }
            }
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        protected CheckBox checkBox;
        protected TextView checkBoxText;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
            checkBoxText = (TextView) itemView.findViewById(R.id.checkBoxText);

            MyFont1 myFont = new MyFont1(context);
            myFont.setAppFont((ViewGroup) itemView);
        }
    }

    private ClickInterface clickInterface;
    public interface ClickInterface {
        public void onItemClick(int position, boolean isChecked);
    }

    public void setOnClickListener(ClickInterface clickInterface) {
        this.clickInterface = clickInterface;
    }
}
