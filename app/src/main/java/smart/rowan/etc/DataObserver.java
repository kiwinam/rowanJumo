package smart.rowan.etc;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import smart.rowan.chatting.ChatData;
import smart.rowan.chatting.ViewHolder;

public class DataObserver extends RecyclerView.AdapterDataObserver {
    private FirebaseRecyclerAdapter<ChatData, ViewHolder> firebaseRecyclerAdapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView recyclerView;
    public DataObserver(FirebaseRecyclerAdapter<ChatData, ViewHolder> firebaseRecyclerAdapter,
                        LinearLayoutManager linearLayoutManager, RecyclerView recyclerView) {
        this.firebaseRecyclerAdapter = firebaseRecyclerAdapter;
        this.linearLayoutManager = linearLayoutManager;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        super.onItemRangeInserted(positionStart, itemCount);
        int friendlyMessageCount = firebaseRecyclerAdapter.getItemCount();
        int lastVisiblePosition =
                linearLayoutManager.findLastCompletelyVisibleItemPosition();
        if (lastVisiblePosition == -1 ||
                (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition == (positionStart - 1))) {
            recyclerView.scrollToPosition(positionStart);
        }
    }
}
