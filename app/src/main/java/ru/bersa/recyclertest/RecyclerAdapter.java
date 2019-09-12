package ru.bersa.recyclertest;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.gson.Gson;
import java.util.ArrayList;

import static androidx.recyclerview.widget.RecyclerView.*;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.LocalViewHolder> implements Filterable {
    private ArrayList<ImgContainer> items = new ArrayList<>();
    private ArrayList<ImgContainer> itemsNoflt = new ArrayList<>();
    private  JSONArray photoArray;
    LayoutInflater layoutInflater;
    OnLoadMoreListener onLoadMoreListener;
    private int lastVisibleItem, totalItemCount;
    int visibleThreshold = 5;
    Boolean isLoading = false;
    private OnItemClickListener mListenet;
    private RecyclerView mContext;


    public RecyclerAdapter(Context context, RecyclerView recyclerView){
        this.layoutInflater = LayoutInflater.from(context);
        mContext = (RecyclerView) recyclerView;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = recyclerView.getLayoutManager().getItemCount();
                lastVisibleItem = recyclerView.getLayoutManager().getChildCount();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (onLoadMoreListener != null) {
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String myString = charSequence.toString();
                if(myString.isEmpty()){
                    items=itemsNoflt;
                }else{
                    ArrayList<ImgContainer> fltResult= new ArrayList<>();
                    for (ImgContainer elem : itemsNoflt){
                        if(elem.getTitle().toLowerCase().contains(myString)){
                            fltResult.add(elem);
                        }
                    }
                    items= fltResult;


                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = items;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                items = (ArrayList<ImgContainer>) filterResults.values;
                notifyDataSetChanged();

            }
        };
    }


    public interface OnItemClickListener{
        void onItemClick(int pos);
    }

    public void  setOnItemClickListener(OnItemClickListener listener){
        mListenet = listener;
    }


    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener){
        this.onLoadMoreListener = mOnLoadMoreListener;
    }




    class LocalViewHolder extends ViewHolder {
        TextView mTextView;
        ImageView mImageView;
        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.item_text);
            mImageView= itemView.findViewById(R.id.image_view);
            mImageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListenet!=null){
                        int pos = getAdapterPosition();
                        if (pos!= NO_POSITION){
                            mListenet.onItemClick(pos);
                        }

                    }
                }
            });

        }
    }


    @NonNull
    @Override
    public LocalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.item_view, parent,false);
        return new LocalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapter.LocalViewHolder holder, int position) {
        if(mContext.getLayoutManager() instanceof GridLayoutManager){
            holder.mTextView.setVisibility(View.INVISIBLE);
        } else {
            holder.mTextView.setVisibility(View.VISIBLE);
            holder.mTextView.setText(getItem(position).getTitle());
        }
        holder.mImageView.setImageBitmap(GalleryReceiver.getBmp(getItem(position).getPreview()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public ImgContainer getItem(int id){
        return items.get(id);
    }


    public boolean fillContent() {

        String stringGalery = GalleryReceiver.GetGalery("66911286-72157691209937521");
        if (stringGalery==null){
            return false;
        }

        try {
            JSONObject mainJSON = new JSONObject(stringGalery);
            JSONObject photosJSON = mainJSON.getJSONObject("photos");
            photoArray = photosJSON.getJSONArray("photo");
            for(int i=0; i<visibleThreshold; i++){
                JSONObject photoJSON = photoArray.getJSONObject(i);
                Gson gson = new Gson();
                ImgContainer imgContainer = gson.fromJson(photoJSON.toString(), ImgContainer.class);
                items.add(imgContainer);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        itemsNoflt=items;
        return true;
    }

    public boolean addPhotoToArray(int index){
        int currentSize = items.size();
        if (photoArray.length()<=currentSize)
            return true;

        try{
            JSONObject photoJSON = photoArray.getJSONObject(currentSize);
            Gson gson = new Gson();
            ImgContainer imgContainer = gson.fromJson(photoJSON.toString(), ImgContainer.class);
            items.add(imgContainer);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void setLoaded() {
        isLoading = false;
    }


}
