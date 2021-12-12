package com.iuh.stream.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.iuh.stream.R;
import com.iuh.stream.activity.ViewImageMessageActivity;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.chat.Line;
import com.iuh.stream.models.response.FileSizeResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.Util;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalLeftLineAdapter extends RecyclerView.Adapter<PersonalLeftLineAdapter.PersonLeftLineViewHolder> {
    private List<Line> lineList;
    private String hisImageUrl;
    private Context mContext;

    public PersonalLeftLineAdapter(Context context, String hisImageUrl) {
        this.mContext = context;
        this.hisImageUrl = hisImageUrl;
    }

    public void setData(List<Line> lineList) {
        this.lineList = lineList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonLeftLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.line_item_left, parent, false);
        return new PersonLeftLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PersonLeftLineViewHolder holder, int position) {
        Line line = lineList.get(position);

        if (line != null) {
            // text type
            if (line.getType().equals(MyConstant.TEXT_TYPE)) {
                holder.textLayout.setVisibility(View.VISIBLE);
                holder.imageLayout.setVisibility(View.GONE);
                holder.fileLayout.setVisibility(View.GONE);
                holder.textContentTv.setText(line.getContent());
                if (position == 0) {
                    Picasso.get().load(hisImageUrl).into(holder.avatarIv);
                    holder.avatarIv.setVisibility(View.VISIBLE);
                    holder.textLastTimeTv.setVisibility(View.VISIBLE);
                }
                if (position == lineList.size() - 1 && position != 0) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                    holder.textLastTimeTv.setVisibility(View.VISIBLE);
                    holder.textLastTimeTv.setText(Util.getTime(line.getCreatedAt()));
                }
                if (position != 0) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                }
                if (position != lineList.size() - 1) {
                    holder.textLastTimeTv.setVisibility(View.GONE);
                }
            }
            // image type
            else if (line.getType().equals(MyConstant.IMAGE_TYPE)) {
                holder.textLayout.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.VISIBLE);
                holder.fileLayout.setVisibility(View.GONE);
                Picasso.get().load(line.getContent()).into(holder.imageContentIv);
                if (position == 0) {
                    Picasso.get().load(hisImageUrl).into(holder.avatarIv);
                    holder.avatarIv.setVisibility(View.VISIBLE);
                    holder.imageLastTimeTv.setVisibility(View.VISIBLE);
                }
                if (position == lineList.size() - 1 && position != 0) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                    holder.imageLastTimeTv.setVisibility(View.VISIBLE);
                    holder.imageLastTimeTv.setText(Util.getTime(line.getCreatedAt()));
                }
                if (position != 0) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                }
                if (position != lineList.size() - 1) {
                    holder.imageLastTimeTv.setVisibility(View.GONE);
                }
            }
            // file
            if (line.getType().equals(MyConstant.FILE_TYPE)) {
                holder.textLayout.setVisibility(View.GONE);
                holder.imageLayout.setVisibility(View.GONE);
                holder.fileLayout.setVisibility(View.VISIBLE);
                // abc.docx
                String content = line.getContent();
                String[] split = content.split("\\.");
                String name = split[0];
                String type = split[1].toUpperCase(Locale.ROOT);
                holder.nameFileTv.setText(name);
                holder.typeFileTv.setText(type + " * ");
                setSize(holder.sizeFileTv, content);
                if (checkIsExistFile(content)) {
                    holder.openFileTv.setVisibility(View.VISIBLE);
                    holder.downloadFileBtn.setVisibility(View.GONE);
                } else {
                    holder.downloadFileBtn.setVisibility(View.VISIBLE);
                    holder.openFileTv.setVisibility(View.GONE);
                }

                switch (type) {
                    case "JSON":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_json_48);
                        break;
                    case "DOC":
                    case "DOCX":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_docx_48);
                        break;
                    case "XLS":
                    case "XLSX":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_xls_48);
                        break;
                    case "PPT":
                    case "PPTX":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_pptx_48);
                        break;
                    case "TXT":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_txt_48);
                        break;
                    case "PDF":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_pdf_48);
                        break;
                    case "APK":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_apk_48);
                        break;
                    case "CSV":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_csv_48);
                        break;
                    case "HTML":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_html_48);
                        break;
                    case "CSS":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_css_48);
                        break;
                    case "ZIP":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_zip_48);
                        break;
                    case "RAR":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_rar_48);
                        break;
                    case "MP3":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_mp3_48);
                        break;
                    case "MP4":
                    case "MOV":
                    case "MKV":
                        holder.typeFileIv.setImageResource(R.drawable.icons8_video_48);
                        break;
                    default:
                        holder.typeFileIv.setImageResource(R.drawable.icons8_file_48);
                        break;

                }

                if (position == 0) {
                    Picasso.get().load(hisImageUrl).into(holder.avatarIv);
                    holder.avatarIv.setVisibility(View.VISIBLE);
                    holder.fileLastTimeTv.setVisibility(View.VISIBLE);
                }
                if (position == lineList.size() - 1 && position != 0) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                    holder.fileLastTimeTv.setVisibility(View.VISIBLE);
                    holder.fileLastTimeTv.setText(Util.getTime(line.getCreatedAt()));
                }
                if (position != 0) {
                    holder.avatarIv.setVisibility(View.INVISIBLE);
                }
                if (position != lineList.size() - 1) {
                    holder.fileLastTimeTv.setVisibility(View.GONE);
                }
            }

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


    private void setSize(TextView sizeFileTv, String name) {
        RetrofitService.getInstance.getFileSizeByName(name, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<FileSizeResponse>() {
                    @Override
                    public void onResponse(Call<FileSizeResponse> call, Response<FileSizeResponse> response) {
                        if (response.code() == 403) {
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            setSize(sizeFileTv, name);
                        } else if (response.code() == 200) {
                            FileSizeResponse fileSizeResponse = response.body();
                            int size = fileSizeResponse.getSize();
                            String sizeConverted = Formatter.formatFileSize(mContext, size);
                            sizeFileTv.setText(sizeConverted);
                        } else {
                            CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, mContext.getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(Call<FileSizeResponse> call, Throwable t) {
                        CustomAlert.showToast((Activity) mContext, CustomAlert.WARNING, t.getMessage());
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (lineList != null) {
            return lineList.size();
        }
        return 0;
    }

    public class PersonLeftLineViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView avatarIv;
        private TextView textLastTimeTv, imageLastTimeTv, textContentTv, nameFileTv, typeFileTv, fileLastTimeTv, sizeFileTv, openFileTv;
        private LinearLayout textLayout, imageLayout;
        private ImageView imageContentIv, typeFileIv;
        private RelativeLayout fileLayout;
        private ImageButton downloadFileBtn;
        private LinearProgressIndicator linearProgressIndicator;

        public PersonLeftLineViewHolder(@NonNull View itemView) {
            super(itemView);

            avatarIv = itemView.findViewById(R.id.chat_image);

            // text
            textContentTv = itemView.findViewById(R.id.chat_text_content);
            textLastTimeTv = itemView.findViewById(R.id.text_last_time_line_tv);
            textLayout = itemView.findViewById(R.id.text_layout);
            //image
            imageLayout = itemView.findViewById(R.id.image_layout);
            imageContentIv = itemView.findViewById(R.id.chat_image_content);
            imageLastTimeTv = itemView.findViewById(R.id.image_last_time_line_tv);
            //file
            fileLayout = itemView.findViewById(R.id.file_layout);
            typeFileIv = itemView.findViewById(R.id.type_file_iv);
            nameFileTv = itemView.findViewById(R.id.name_file_tv);
            typeFileTv = itemView.findViewById(R.id.type_file_tv);
            fileLastTimeTv = itemView.findViewById(R.id.file_last_time_line_tv);
            sizeFileTv = itemView.findViewById(R.id.size_file_tv);
            openFileTv = itemView.findViewById(R.id.open_view);
            downloadFileBtn = itemView.findViewById(R.id.download_view);
            linearProgressIndicator = itemView.findViewById(R.id.linear_pb);


            // view image
            imageContentIv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Line line = lineList.get(getAdapterPosition());
                    Intent intent = new Intent(mContext, ViewImageMessageActivity.class);
                    intent.putExtra(MyConstant.CONTENT_KEY, line.getContent());
                    mContext.startActivity(intent);
                }
            });

            openFileTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Line line = lineList.get(getAdapterPosition());
                    openFile(line.getContent());
                }
            });

            downloadFileBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Line line = lineList.get(getAdapterPosition());
                    downloadFile(linearProgressIndicator, openFileTv, downloadFileBtn, line.getContent());
                }
            });
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
        }catch (Exception e){
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
