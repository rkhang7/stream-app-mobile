package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.iuh.stream.R;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.response.FileResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
            if (checkIsExistFile(fileResponse.getFilename())) {
                holder.openFileTv.setVisibility(View.VISIBLE);
                holder.downloadFileBtn.setVisibility(View.GONE);
            } else {
                holder.downloadFileBtn.setVisibility(View.VISIBLE);
                holder.openFileTv.setVisibility(View.GONE);
            }
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
        private TextView fileNameTv, fileSizeTv, openFileTv;
        private ImageButton downloadFileBtn;
        private LinearProgressIndicator linearProgressIndicator;
        public FilesViewHolder(@NonNull View itemView) {
            super(itemView);
            fileTypeIv = itemView.findViewById(R.id.type_file_iv);
            fileNameTv = itemView.findViewById(R.id.file_name_tv);
            fileSizeTv = itemView.findViewById(R.id.size_file_tv);
            downloadFileBtn = itemView.findViewById(R.id.download_btn);
            openFileTv = itemView.findViewById(R.id.open_view);
            linearProgressIndicator = itemView.findViewById(R.id.linear_pb);


            openFileTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileResponse fileResponse = fileResponseList.get(getAdapterPosition());
                    openFile(fileResponse.getFilename());
                }
            });


            downloadFileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileResponse fileResponse = fileResponseList.get(getAdapterPosition());
                    downloadFile(linearProgressIndicator, openFileTv, downloadFileBtn,fileResponse.getFilename());
                }
            });

        }
    }

    private boolean checkIsExistFile(String fileName) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fileName);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }

    private void openFile(String fileName) {
        String[] split = fileName.split("\\.");
        String type = split[1].toUpperCase(Locale.ROOT);
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fileName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (type) {
            case "JSON":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/json");
                break;
            case "DOC":
            case "DOCX":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                break;
            case "XLS":
            case "XLSX":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                break;
            case "PPT":
            case "PPTX":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/vnd.openxmlformats-officedocument.presentationml.presentation");
                break;
            case "TXT":
                intent.setDataAndType(Uri.parse(file.getPath()), "text/plain");
                break;
            case "PDF":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/pdf");
                break;
            case "APK":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/vnd.android.package-archive");
                break;
            case "CSV":
                intent.setDataAndType(Uri.parse(file.getPath()), "text/csv");
                break;
            case "HTML":
                intent.setDataAndType(Uri.parse(file.getPath()), "text/html");
                break;
            case "CSS":
                intent.setDataAndType(Uri.parse(file.getPath()), "text/css");
                break;
            case "ZIP":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/zip");
                break;
            case "RAR":
                intent.setDataAndType(Uri.parse(file.getPath()), "application/vnd.rar");
                break;
            case "MP3":
                intent.setDataAndType(Uri.parse(file.getPath()), "audio/mpeg");
                break;
            case "MP4":
            case "MOV":
            case "MKV":
                intent.setDataAndType(Uri.parse(file.getPath()), "video/mp4");
                break;
            default:
                intent.setDataAndType(Uri.parse(file.getPath()), "text/plain");
                break;

        }
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(mContext, "Không thể mở file", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadFile(LinearProgressIndicator linearProgressIndicator, TextView openFileTv, ImageButton downloadFileBtn,String fileName) {
        linearProgressIndicator.setVisibility(View.VISIBLE);
        RetrofitService.getInstance.downloadFile(fileName, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            downloadFile(linearProgressIndicator, openFileTv, downloadFileBtn, fileName);
                        } else {
                            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                            File file = new File(path, fileName);

                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(file);
                                IOUtils.write(response.body().bytes(), fileOutputStream);
                                CustomAlert.showToast((Activity) mContext, CustomAlert.INFO, "Đã tải xong");
                                linearProgressIndicator.setVisibility(View.GONE);
                                downloadFileBtn.setVisibility(View.GONE);
                                openFileTv.setVisibility(View.VISIBLE);

                            } catch (Exception e) {
                                CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, mContext.getString(R.string.error_notification));
                            }
                        }

                    }


                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

}
