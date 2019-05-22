package com.example.note.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.note.R;
import com.example.note.model.Note;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private List<Note> noteList;
    private Context context;
    private OnNoteAdapter onNoteAdapter;

    public NoteAdapter(List<Note> noteList, Context context, OnNoteAdapter onNoteAdapter) {
        this.noteList = noteList;
        this.context = context;
        this.onNoteAdapter = onNoteAdapter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.rlContainer.setBackgroundColor(note.getColor());
        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        holder.tvDate.setText(note.getDateCreate());
        if (!note.isAlarm())
            holder.imvAlarm.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return noteList != null ? noteList.size() : 0;
    }

    public void removeItem(int pos) {
        noteList.remove(noteList.get(pos));
        notifyItemRemoved(pos);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_content)
        TextView tvContent;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.imv_alarm)
        ImageView imvAlarm;
        @BindView(R.id.container)
        CardView rlContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onNoteAdapter != null) {
                        onNoteAdapter.onClickNote(getAdapterPosition());
                    }
                }
            });
        }
    }

    public List<Note> getNoteList() {
        return noteList;
    }

    public interface OnNoteAdapter {
        void onClickNote(int pos);
    }
}
