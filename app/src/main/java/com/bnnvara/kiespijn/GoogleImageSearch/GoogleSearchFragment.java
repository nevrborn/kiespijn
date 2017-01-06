package com.bnnvara.kiespijn.GoogleImageSearch;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.bnnvara.kiespijn.R;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GoogleSearchFragment extends Fragment {

    private static final String TAG = "PhotoGalleryFragment";
    private static final String GOOGLE_IMAGE_URL = "google_image_url";

    private RecyclerView mPhotoRecylerView;
    private static String mSearchString;
    private static List<GalleryItem> mGalleryItems;
    private String mChosenURL;
    private List<CheckBox> mRadioButtonList;
    private List<ImageView> mImageViewList;

    public static GoogleSearchFragment newInstance(String searchString) {
        mSearchString = searchString;
        return new GoogleSearchFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        mRadioButtonList = new ArrayList<>();
        mImageViewList = new ArrayList<>();
        getImages();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_google_search, menu);

        MenuItem useImage = menu.findItem(R.id.menu_google_search_use);

        useImage.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                Intent resultIntent = new Intent();
                resultIntent.putExtra(GOOGLE_IMAGE_URL, mChosenURL);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();

                return false;
            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_google_search, container, false);

        mPhotoRecylerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecylerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        return view;
    }

    private void getImages() {


        String key = "AIzaSyDNtEcEBu5G3341BkSjJqoOeUqID9MLNp4";
        String cx = "005303562240230618745:fehybwiv3j0";

        // Logging
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        Gson gson = new GsonBuilder().setLenient().create();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit restAdapter = new Retrofit.Builder()
                .baseUrl("https://www.googleapis.com")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        GoogleApiRestInterface apiResponse = restAdapter.create(GoogleApiRestInterface.class);

        Call<GoogleImageApiResponse> mGalleryResponse = apiResponse.customSearch(key, cx, mSearchString);

        mGalleryResponse.enqueue(new Callback<GoogleImageApiResponse>() {
            @Override
            public void onResponse(Call<GoogleImageApiResponse> call, Response<GoogleImageApiResponse> response) {
                GoogleImageApiResponse mGoogleImageApiResponse = response.body();

                if (response.body() == null) {
                    Log.e("Retrofit body null", String.valueOf(response.code()));
                }

                mGalleryItems = mGoogleImageApiResponse.getGalleryItems();
                Log.v("mGalleryItems", String.valueOf(response.body().getGalleryItems().size()));

                if (mPhotoRecylerView != null) {
                    mPhotoRecylerView.setAdapter(new PhotoAdapter(mGalleryItems));
                }
            }

            @Override
            public void onFailure(Call<GoogleImageApiResponse> call, Throwable t) {
                Log.e("Retrofit error", t.getMessage());
            }
        });

    }

    public class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView mItemImageView;
        private CheckBox mRadioButton;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
            mRadioButton = (CheckBox) itemView.findViewById(R.id.radioButton);
            mItemImageView = (ImageView) itemView.findViewById(R.id.iv_photo_gallery_fragment);
            itemView.setOnClickListener(this);
        }

        public void bindGalleryItem(GalleryItem item) {
            mGalleryItem = item;
            String url = mGalleryItem.getUrl();
            String substring = url.substring(url.lastIndexOf('/'), url.lastIndexOf('.'));
            String imageUrl = "http://www.gstatic.com/hostedimg/" + substring + "_large";
            mGalleryItem.setImageURL(imageUrl);
            mRadioButtonList.add(mRadioButton);
            mImageViewList.add(mItemImageView);
            Log.i(TAG, imageUrl);

            Glide.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mItemImageView);
        }

        @Override
        public void onClick(View view) {
            int i = 0;

            while (i < mRadioButtonList.size()) {
                mRadioButtonList.get(i).setChecked(false);
                mImageViewList.get(i).setAlpha(1f);
                i += 1;
            }

            mRadioButton.setChecked(true);
            mItemImageView.setAlpha(0.7f);
            mChosenURL = mGalleryItem.getImageURL();

        }

    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.google_search_gallery_item, parent, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem mGalleryItem = mGalleryItems.get(position);
            holder.bindGalleryItem(mGalleryItem);
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }

        public void setupAdapter() {
            if (isAdded()) {
                mPhotoRecylerView.setAdapter(new PhotoAdapter(mGalleryItems));
            }
        }
    }
}
