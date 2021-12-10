package com.iuh.stream.fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iuh.stream.R;
import com.iuh.stream.adapter.ImagesAdapter;
import com.iuh.stream.api.RetrofitService;
import com.iuh.stream.datalocal.DataLocalManager;
import com.iuh.stream.models.response.ImageContentResponse;
import com.iuh.stream.models.response.ImageUrlResponse;
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
 * Use the {@link ImagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImagesFragment extends Fragment {

    private View view;
    private List<ImageContentResponse> imageUrlList;
    private ImagesAdapter imagesAdapter;
    private RecyclerView recyclerView;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";


    // TODO: Rename and change types of parameters
    private String mParam1;


    public ImagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ImagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ImagesFragment newInstance(String param1) {
        ImagesFragment fragment = new ImagesFragment();
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
        view =  inflater.inflate(R.layout.fragment_images, container, false);
        addControls();
        return view;
    }

    private void addControls() {
        imageUrlList = new ArrayList<>();
        imagesAdapter = new ImagesAdapter(getContext());
        imagesAdapter.setData(imageUrlList);
        recyclerView = view.findViewById(R.id.images_rcv);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        recyclerView.setAdapter(imagesAdapter);

        loadImages(mParam1);

        SocketClient.getInstance().on(MyConstant.RENDER_IMAGE_RESPONSE, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                ((Activity)getContext()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loadImages(mParam1);
                    }
                });
            }
        });
    }

    private void loadImages(String chatId) {
        RetrofitService.getInstance.getAllImagesChat(chatId, DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN))
                .enqueue(new Callback<List<ImageContentResponse>>() {
                    @Override
                    public void onResponse(Call<List<ImageContentResponse>> call, Response<List<ImageContentResponse>> response) {
                        if(response.code() == 403){
                            Util.refreshToken(DataLocalManager.getStringValue(MyConstant.ACCESS_TOKEN));
                            loadImages(chatId);
                        }
                        else if(response.code() == 200){
                            imageUrlList = response.body();
                            imagesAdapter.setData(imageUrlList);
                            recyclerView.setAdapter(imagesAdapter);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<ImageContentResponse>> call, Throwable t) {

                    }
                });
    }
}