package smart.rowan.chatting;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import smart.rowan.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    public TextView messageTextView;
    public TextView messengerTextView;
    public TextView messengerTimeView;
    public LinearLayout messageLayout;
    public LinearLayout messageViewLayout;
    public CircleImageView messengerImageView;
    public LinearLayout messageTextLayout;

    public ViewHolder(View v) {
        super(v);
        messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
        messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        messengerTimeView = (TextView) itemView.findViewById(R.id.messengerTimeView);
        messageLayout = (LinearLayout) itemView.findViewById(R.id.messageLayout);
        messengerImageView = (CircleImageView) itemView.findViewById(R.id.messengerImageView);
        messageViewLayout = (LinearLayout) itemView.findViewById(R.id.messageViewLayout);
        messageTextLayout = (LinearLayout) itemView.findViewById(R.id.messageTextLayout);
    }
}