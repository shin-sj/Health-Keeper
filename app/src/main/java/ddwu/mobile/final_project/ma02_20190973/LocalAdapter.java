package ddwu.mobile.final_project.ma02_20190973;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LocalAdapter extends BaseAdapter {
    public static final String TAG = "LocalAdapter";

    private LayoutInflater inflater;
    private Context context;
    private int layout;
    private ArrayList<NaverLocalDto> list;

    public LocalAdapter(Context context, int resource, ArrayList<NaverLocalDto> list) {
        this.context = context;
        this.layout = resource;
        this.list = list;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public NaverLocalDto getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView with position : " + position);
        View view = convertView;
        ViewHolder viewHolder = null;

        if (view == null) {
            view = inflater.inflate(layout, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvTitle = view.findViewById(R.id.local_title);
            viewHolder.tvTelephone = view.findViewById(R.id.local_telephone);
            viewHolder.tvCategory = view.findViewById(R.id.local_category);
            viewHolder.tvRoadAddress = view.findViewById(R.id.local_roadAddress);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)view.getTag();
        }

        NaverLocalDto dto = list.get(position);

        viewHolder.tvTitle.setText(dto.getTitle());
        viewHolder.tvTelephone.setText(dto.getTelephone());
        viewHolder.tvCategory.setText(dto.getCategory());
        viewHolder.tvRoadAddress.setText(dto.getRoadAddress());

        return view;
    }


    public void setList(ArrayList<NaverLocalDto> list) {
        this.list = list;
    }


    //    ※ findViewById() 호출 감소를 위해 필수로 사용할 것
    static class ViewHolder {
        public TextView tvTitle = null;
        public TextView tvTelephone = null;
        public TextView tvCategory = null;
        public TextView tvRoadAddress = null;
    }
}
