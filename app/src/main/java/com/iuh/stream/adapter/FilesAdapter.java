package com.iuh.stream.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iuh.stream.R;
import com.iuh.stream.models.response.FileResponse;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FilesViewHolder>{
    private Context mContext;
    private List<FileResponse> fileResponseList;

    public FilesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<FileResponse> fileResponseList){
        this.fileResponseList = fileResponseList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FilesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_item, parent, false);
        return new FilesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesViewHolder holder, int position) {
        FileResponse fileResponse = fileResponseList.get(position);
        if(fileResponse != null){
            String[] split = fileResponse.getFilename().split("\\.");
            String name = split[0];
            String type = split[1].toUpperCase(Locale.ROOT);
            holder.fileNameTv.setText(name);
            setSize(holder.fileSizeTv, fileResponse.getChunkSize());
            switch (type){
                case "JSON":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_json_48);
                    break;
                case "DOC":
                case "DOCX":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_docx_48);
                    break;
                case "XLS":
                case "XLSX":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_xls_48);
                    break;
                case "PPT":
                case "PPTX":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_pptx_48);
                    break;
                case "TXT":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_txt_48);
                    break;
                case "PDF":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_pdf_48);
                    break;
                case "APK":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_apk_48);
                    break;
                case "CSV":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_csv_48);
                    break;
                case "HTML":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_html_48);
                    break;
                case "CSS":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_css_48);
                    break;
                case "ZIP":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_zip_48);
                    break;
                case "RAR":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_rar_48);
                    break;
                case "MP3":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_mp3_48);
                    break;
                case "MP4":
                case "MOV":
                case "MKV":
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_video_48);
                    break;
                default:
                    holder.fileTypeIv.setImageResource(R.drawable.icons8_file_48);
                    break;

            }
        }
    }

    private void setSize(TextView fileSizeTv, int chunkSize) {
        String sizeConverted = Formatter.formatFileSize(mContext,chunkSize);
        fileSizeTv.setText(sizeConverted);
    }

    @Override
    public int getItemCount() {
        if(fileResponseList != null){
            return fileResponseList.size();
        }
        return 0;
    }

    public class FilesViewHolder extends RecyclerView.ViewHolder {
        private ImageView fileTypeIv;
        private TextView fileNameTv, fileSizeTv;
        private ImageButton downloadBtn;
        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            fileTypeIv = itemView.findViewById(R.id.type_file_iv);
            fileNameTv = itemView.findViewById(R.id.file_name_tv);
            fileSizeTv = itemView.findViewById(R.id.size_file_tv);
            downloadBtn = itemView.findViewById(R.id.download_btn);
        }
    }
}
