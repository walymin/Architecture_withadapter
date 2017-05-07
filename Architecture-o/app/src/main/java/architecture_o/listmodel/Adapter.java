package architecture_o.listmodel;

import android.support.v7.widget.RecyclerView;

public abstract class Adapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
        public Adapter() {
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }