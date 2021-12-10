package com.iuh.stream.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iuh.stream.R;
import com.iuh.stream.adapter.FilesAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.dialog.CustomAlert;
import com.iuh.stream.models.response.FileResponse;
import com.iuh.stream.utils.MyConstant;
import com.iuh.stream.utils.SocketClient;
import com.iuh.stream.utils.Util;

import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FilesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FilesFragment extends Fragment {
    private View view;
    private List<FileResponse> fileResponseList;
    private FilesAdapter filesAdapter;
    private RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    public FilesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FilesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FilesFragment newInstance(String param1) {
        FilesFragment fragment = new FilesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_files, container, false);
        addControls();
        return view;
    }

    private void addControls() {
        fileResponseList = new ArrayList<>();
        filesAdapter = new FilesAdapter(getContext());
        filesAdapter.setData(fileResponseList);
        recyclerView = view.findViewById(R.id.files_rcv);
        recyclerView.setAdapter(filesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFiles(mParam1);
        SocketClient.getInstance().on(MyConstant.RENDER_FILE_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadFiles(mParam1);
                    }
                });
            }
        });
    }

    private void loadFiles(String chatId) {
        RetrofitService.getInstance.getAllFilesChat(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<List<FileResponse>>() {
                    @Override
                    public void onResponse(Call<List<FileResponse>> call, Response<List<FileResponse>> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.REFRESH_TOKEN));
                            loadFiles(chatId);
                        }
                        else if(response.code() == 200){
                            fileResponseList = response.body();
                            filesAdapter.setData(fileResponseList);
                            recyclerView.setAdapter(filesAdapter);
                        }
                        else{
                            CustomAlert.showToast((Activity) getContext(), CustomAlert.WARNING, getString(R.string.error_notification));
                        }
                    }

                    @Override
                    public void onFailure(Call<List<FileResponse>> call, Throwable t) {
                        CustomAlert.showToast((Activity) getContext(), CustomAlert.WARNING, t.getMessage());
                    }
                });
    }
}