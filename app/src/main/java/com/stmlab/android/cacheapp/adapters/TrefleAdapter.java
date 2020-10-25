package com.stmlab.android.cacheapp.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stmlab.android.cacheapp.R;
import com.stmlab.android.cacheapp.models.TrefleModel;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

public class TrefleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Subject<ArrayList<TrefleModel>> mObservable = PublishSubject.create();
    ArrayList<TrefleModel> mTrefleList = new ArrayList<>();
    OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public Subject<ArrayList<TrefleModel>> getObservable() {
        return mObservable;
    }

    public void setupTrefleList(ArrayList<TrefleModel> data) {
        mTrefleList.clear();
        mTrefleList = data;
        notifyDataSetChanged();
    }
    public void addTrefleList(ArrayList<TrefleModel> data) {
        mObservable.onNext(data);
        int startPositionUpdate = mTrefleList.size();
        mTrefleList.addAll(data);
        notifyItemInserted(startPositionUpdate);
    }
    public void addTrefleList(ArrayList<TrefleModel> data, int DATA_CLEAR) {
        mObservable.onNext(data);
        if ( DATA_CLEAR == 1 ) {
            setupTrefleList(data);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.item, viewGroup, false);
        return new TrefleHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if ( viewHolder instanceof TrefleHolder ) {
            ((TrefleHolder) viewHolder).bind(mTrefleList.get(i));
        }
    }

    @Override
    public int getItemCount() {
        return mTrefleList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(TrefleModel model);
    }

    public class TrefleHolder extends RecyclerView.ViewHolder {
        TextView mTextViewName;

        public TrefleHolder(@NonNull View itemView) {
            super(itemView);
            mTextViewName = itemView.findViewById(R.id.textViewName);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if ( position != NO_POSITION ) {
//                    mOnItemClickListener.onItemClick(mTrefleList.get(position));
                }
            });
        }

        public void bind(TrefleModel trefle) {
            mTextViewName.setText(trefle.getCommonName());
        }
    }
}
