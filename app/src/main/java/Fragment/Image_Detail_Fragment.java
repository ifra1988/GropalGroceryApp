package Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import Adapter.ImagesListViewAdapter;
import Model.SliderItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import gropal.in.MainActivity;
import gropal.in.R;
import util.ItemDecorationColumns;
import util.RecyclerTouchListener;
import util.UtilityHelper;

public class Image_Detail_Fragment extends Fragment {
    private static String TAG = Image_Detail_Fragment.class.getSimpleName();
    private SliderItem product;
    private ImagesListViewAdapter adapter;
    private ArrayList<SliderItem> images_list ;

    @BindView(R.id.rv_images)
    RecyclerView rv_prod_images;

    @BindView(R.id.iv_selected)
    ImageView iv_selected;


    public Image_Detail_Fragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image_detail, container, false);
        ButterKnife.bind(this, view);

        images_list = getArguments().getParcelableArrayList("images");

        ((MainActivity) getActivity()).setToolbarTitle(images_list.get(0).getName());

        UtilityHelper.setServerImage(getContext(),images_list.get(0).getImageUrl(),iv_selected);
        setImagesList();
        adapter = new ImagesListViewAdapter(getContext(),images_list);
        rv_prod_images.setAdapter(adapter);

        return view;
    }


    private void setImagesList() {
        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false);
        ;
        rv_prod_images.setLayoutManager(horizontalLayout);
        rv_prod_images.setItemAnimator(new DefaultItemAnimator());
        // rv_similar_prods.setNestedScrollingEnabled(false);
        // rv_similar_prods.addItemDecoration(new Fragment.Home_fragment.SpacesItemDecoration(5));
       // adapter_product.notifyDataSetChanged();
        rv_prod_images.addItemDecoration(new ItemDecorationColumns(20, images_list.size()));
        rv_prod_images.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rv_prod_images, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SliderItem item = images_list.get(position);
                UtilityHelper.setServerImage(getContext(),item.getImageUrl(),iv_selected);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

}

