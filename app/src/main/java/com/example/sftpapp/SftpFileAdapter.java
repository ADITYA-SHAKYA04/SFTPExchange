package com.example.sftpapp;

import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sftpapp.R;
import com.jcraft.jsch.ChannelSftp;
import java.util.List;
import java.util.HashSet;

public class SftpFileAdapter extends RecyclerView.Adapter<SftpFileAdapter.FileViewHolder> {
    private List<ChannelSftp.LsEntry> files;
    private OnFileClickListener clickListener;
    private OnFileLongClickListener longClickListener;
    private HashSet<ChannelSftp.LsEntry> selectedFiles = new HashSet<>();


    public interface OnFileLongClickListener {
        void onFileLongClick(ChannelSftp.LsEntry entry);
    }

    public interface OnFileClickListener {
        void onFileClick(ChannelSftp.LsEntry entry);
    }

    public SftpFileAdapter(List<ChannelSftp.LsEntry> files, OnFileClickListener clickListener, OnFileLongClickListener longClickListener) {
        this.files = files;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    public void setFiles(List<ChannelSftp.LsEntry> files) {
        this.files = files;
        notifyDataSetChanged();
    }

    public void toggleFileSelection(ChannelSftp.LsEntry entry) {
        if (selectedFiles.contains(entry)) {
            selectedFiles.remove(entry);
        } else {
            selectedFiles.add(entry);
        }
        notifyDataSetChanged();
    }

    public List<ChannelSftp.LsEntry> getSelectedFiles() {
        return new java.util.ArrayList<>(selectedFiles);
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file_card, parent, false);
        return new FileViewHolder(view);
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {
        android.widget.ImageView fileIcon;
        TextView fileName;
        TextView fileSize;
        FileViewHolder(View itemView) {
            super(itemView);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        ChannelSftp.LsEntry entry = files.get(position);
        String name = entry.getFilename();
        long size = entry.getAttrs().getSize();
        boolean isDir = entry.getAttrs().isDir();

        holder.fileName.setText(name);
        holder.fileName.setTypeface(null, isDir ? Typeface.BOLD : Typeface.NORMAL);
        holder.fileSize.setText(isDir ? "Folder" : formatSize(size));
        holder.fileIcon.setImageResource(isDir ? android.R.drawable.ic_menu_agenda : android.R.drawable.ic_menu_save);
        holder.fileIcon.setColorFilter(holder.fileIcon.getContext().getResources().getColor(isDir ? R.color.rose : R.color.neonGreen, null));

        if (selectedFiles.contains(entry)) {
            holder.itemView.setBackgroundColor(0xFF1976D2); // Highlight with a blue color
            holder.fileName.setTextColor(0xFFFFFFFF); // White text
            holder.fileSize.setTextColor(0xFFFFFFFF);
        } else {
            holder.itemView.setBackgroundColor(0x00000000); // Transparent
            // Get theme-aware text colors
            int textColorPrimary = resolveAttrColor(holder.fileName.getContext(), android.R.attr.textColorPrimary);
            int textColorSecondary = resolveAttrColor(holder.fileSize.getContext(), android.R.attr.textColorSecondary);
            holder.fileName.setTextColor(textColorPrimary);
            holder.fileSize.setTextColor(textColorSecondary);
        }

        holder.itemView.setOnClickListener(v -> clickListener.onFileClick(entry));
        holder.itemView.setOnLongClickListener(v -> {
            longClickListener.onFileLongClick(entry);
            return true;
        });
    }

    private String formatSize(long size) {
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    @Override
    public int getItemCount() {
        return files != null ? files.size() : 0;
    }

    // Helper to resolve theme attribute color
    private int resolveAttrColor(android.content.Context context, int attr) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(attr, typedValue, true);
        if (typedValue.resourceId != 0) {
            return context.getResources().getColor(typedValue.resourceId, context.getTheme());
        } else {
            return typedValue.data;
        }
    }
}
