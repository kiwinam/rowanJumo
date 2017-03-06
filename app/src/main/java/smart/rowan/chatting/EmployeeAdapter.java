package smart.rowan.chatting;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Vector;

import smart.rowan.R;

public class EmployeeAdapter extends BaseAdapter {
    private Vector<User> user;
    private LayoutInflater layoutInflater;
    private SharedPreferences myData;

    public EmployeeAdapter(Context context, Vector<User> user, LayoutInflater layoutInflater) {
        this.user = user;
        this.layoutInflater = layoutInflater;
        myData = context.getSharedPreferences("SharedData", Context.MODE_PRIVATE);
    }

    private class ViewHolder {
        private TextView textView;
        private TextView msgCountTextView;
    }

    @Override
    public int getCount() {
        return user.size();
    }

    @Override
    public Object getItem(int i) {
        return user.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.employee_row, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.employeeId);
            viewHolder.msgCountTextView = (TextView) convertView.findViewById(R.id.msgCountTextView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String subject = user.get(position).getName();
        viewHolder.textView.setText(subject);
        int count = user.get(position).getCount();
        if (count == 0) {
            viewHolder.msgCountTextView.setText("");
            viewHolder.msgCountTextView.setBackgroundResource(0);
        } else {
            viewHolder.msgCountTextView.setText(String.valueOf(count));
            viewHolder.msgCountTextView.setBackgroundResource(R.drawable.chat_count);
        }
        return convertView;
    }
}