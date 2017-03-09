package smart.rowan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by charlie on 2017. 3. 9..
 */

public class RealTimeAdapter extends RecyclerView.Adapter<RealTimeAdapter.ViewHolder>
{
    private Context context;
    private ArrayList<RealTimeItem> mItems;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;

    public RealTimeAdapter(ArrayList<RealTimeItem> items, Context mContext)
    {
        mItems = items;
        context = mContext;
    }

    // 필수로 Generate 되어야 하는 메소드 1 : 새로운 뷰 생성
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 새로운 뷰를 만든다
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    // 필수로 Generate 되어야 하는 메소드 2 : ListView의 getView 부분을 담당하는 메소드
    @Override
    public void onBindViewHolder(RealTimeAdapter.ViewHolder holder, int position) {

        holder.realTimeImage.setImageResource(mItems.get(position).getImage());
        holder.realTimeName.setText(mItems.get(position).getName());
        holder.realTimeStatus.setText(mItems.get(position).getStatus());

       // setAnimation(holder.realTimeImage, position);
    }

    // // 필수로 Generate 되어야 하는 메소드 3
    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {

        public ImageView realTimeImage;
        public TextView realTimeName;
        public TextView realTimeStatus;

        public ViewHolder(View view) {
            super(view);
            realTimeImage = (ImageView) view.findViewById(R.id.realTimeImage);
            realTimeName = (TextView) view.findViewById(R.id.realTimeName);
            realTimeStatus = (TextView) view.findViewById(R.id.realTimeStatus);
        }
    }
/*
    private void setAnimation(View viewToAnimate, int position)
    {
        // 새로 보여지는 뷰라면 애니메이션을 해줍니다
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }*/

}
