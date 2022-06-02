package com.goldsikka.goldsikka.Fragments.JewelleryInventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.goldsikka.goldsikka.Activitys.OnBoardScreen;
import com.goldsikka.goldsikka.Adapter.BannersAdapter;
import com.goldsikka.goldsikka.Adapter.PageviewAdapter;
import com.goldsikka.goldsikka.Fragments.JewelleryInventory.Adapters.AllCatAdapter;
import com.goldsikka.goldsikka.Fragments.JewelleryInventory.Adapters.AllProductsAdapter;
import com.goldsikka.goldsikka.Models.BannersModel;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.Utils.shared_preference;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.interfaces.OnItemClickListener;
import com.goldsikka.goldsikka.model.Listmodel;
import com.goldsikka.goldsikka.netwokconnection.ApiClient;
import com.viewpagerindicator.CirclePageIndicator;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator3;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JewelleryHome extends AppCompatActivity implements OnItemClickListener, View.OnClickListener {

    TextView uidTv, unameTv, titleTv;
    RelativeLayout backbtn;
    String pid;

    List<Listmodel> flist;
    List<Listmodel> flistshuffle;

    RecyclerView productsrv;

    AllProductsAdapter adapter;
    ArrayList<Listmodel> productsList;

    RecyclerView catrv;

    AllCatAdapter catadapter;
    ArrayList<Listmodel> catlist;

    ApiDao apiDao, apiDao1;
    private Activity activity;

   ViewPager viewPager;
    private Handler sliderHandler = new Handler();
    List<BannersModel> sliderItemList;
    private ArrayList<String> urls = new ArrayList<>();

    String get_banners;
    String cid;

    List<Listmodel> flistwishhome = new ArrayList<>();

    SwipeRefreshLayout swipe_layout;



    TextView juname, jwish, wihslistcountTv, viewallproducts;

    shared_preference sharedPreference;

    ImageView wishlistbtn;

    int wcount = 0;

    int scrollCount = 0;

    private int currentPage = 0;
    private int NUM_PAGES;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jewellery_home);

        Objects.requireNonNull(getSupportActionBar()).hide();

        ButterKnife.bind(this);
        sharedPreference = new shared_preference(this);
        uidTv = findViewById(R.id.uid);
        unameTv = findViewById(R.id.uname);
        titleTv = findViewById(R.id.title);
        wihslistcountTv = findViewById(R.id.wihslistcountTv);

        uidTv.setText(AccountUtils.getCustomerID(this));
        unameTv.setText(AccountUtils.getName(this));
        titleTv.setText("Jewellery");

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        jwish = findViewById(R.id.jwish);
//        juname = findViewById(R.id.juname);
        wishlistbtn = findViewById(R.id.wishlistbtn);
        viewallproducts = findViewById(R.id.viewallproducts);

        wishlistbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JewelleryHome.this, Wishlist_Activity.class));
            }
        });

        viewallproducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JewelleryHome.this, SubCatagory_Activity.class);
                intent.putExtra("from", "viewallproducts");
                startActivity(intent);
            }
        });

//        juname.setText(AccountUtils.getName(this));

        String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
        if (Integer.parseInt(currentTime) < 12)
            jwish.setText("Good Morning,");
        else if (Integer.parseInt(currentTime) >= 12 && Integer.parseInt(currentTime) < 16)
            jwish.setText("Good Afternoon,");
        else if (Integer.parseInt(currentTime) >= 16 && Integer.parseInt(currentTime) <= 24)
            jwish.setText("Good Evening,");

        getWishlistCount();
        intilizerecyclerview();
        getAllCats();
        getAllProducts();
        getBannersData();
        onrefresh();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getWishlistCount();
        intilizerecyclerview();
//        getAllCats();
//        getAllProducts();
//        getbanners();
        onrefresh();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWishlistCount();
        intilizerecyclerview();
        getAllCats();
        getAllProducts();
        getbanners();
        onrefresh();
    }

    public void getWishlistCount() {

        apiDao = ApiClient.getClient(AccountUtils.getAccessToken(this)).create(ApiDao.class);
        Call<List<Listmodel>> getWishlist = apiDao.getWishlist("Bearer " + AccountUtils.getAccessToken(this));
        getWishlist.enqueue(new Callback<List<Listmodel>>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(@NotNull Call<List<Listmodel>> call, @NotNull Response<List<Listmodel>> response) {
                int statuscode = response.code();
                Log.e("statuscode", String.valueOf(statuscode));
//                assert response.body() != null;

                if (statuscode == 200 || statuscode == 201) {
                    List<Listmodel> flistwishhome = new ArrayList<>();

                    adapter.setFlistwish(flistwishhome);
                    adapter.setFlistwish(response.body());

                    if (!adapter.getFlistwish().isEmpty()) {
                        wcount = adapter.getFlistwish().size();
                        wihslistcountTv.setText(String.valueOf(wcount));
                    } else {
                        wcount = 0;
                        wihslistcountTv.setText(String.valueOf(wcount));
                    }
                } else if (statuscode == 422) {
                    Log.e("cv", String.valueOf(statuscode));
                    wihslistcountTv.setText(String.valueOf(wcount));
                } else {
                    Log.e("fgd", "sdfsd");
                    wihslistcountTv.setText(String.valueOf(wcount));
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Listmodel>> call, @NonNull Throwable t) {
                Log.e("ughb", String.valueOf(t));
                wihslistcountTv.setText(String.valueOf(wcount));

//                openpopupscreen("Successfully subscribed to Gold Plus Plan");
            }
        });


    }

    private void getAllProducts() {

        final int[] counts = {0};
        productsList.clear();
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();

        apiDao = ApiClient.getClient("").create(ApiDao.class);
        Call<List<Listmodel>> getproductslist = apiDao.getJewProducts();

        getproductslist.enqueue(new Callback<List<Listmodel>>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NotNull Call<List<Listmodel>> call, @NotNull Response<List<Listmodel>> response) {
                int statuscode = response.code();
                Log.e("statuscode", String.valueOf(statuscode));
                flist = response.body();
                dialog.dismiss();
                if (statuscode == 200 || statuscode == 201) {
                    Log.e("statuscode", String.valueOf(statuscode));
//                    assert listmodel != null;
                    if (flist != null) {
                        productsList.clear();
                        Collections.shuffle(flist);
                        for (Listmodel listmodel : flist) {
                            if (counts[0] == 20) {
                                return;
                            } else {
                                counts[0] += 1;
                                pid = listmodel.getId();
                                productsList.add(listmodel);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Log.e("catname", "No Products");
                    }
//                    openpopupscreen(listmodel.getDescription());
                } else if (statuscode == 422) {
                    dialog.dismiss();
                    Log.e("cv", String.valueOf(statuscode));
//                        ToastMessage.onToast(Elevenplus_Jewellery.this, String.valueOf(statuscode), ToastMessage.ERROR);
                } else {
                    dialog.dismiss();
//                    ToastMessage.onToast(Elevenplus_Jewellery.this, "Please try again", ToastMessage.ERROR);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Listmodel>> call, @NonNull Throwable t) {
                dialog.dismiss();
                Log.e("ughb", String.valueOf(t));
//                openpopupscreen("Successfully subscribed to Gold Plus Plan");
            }
        });
    }

    public void onrefresh() {
        Log.e("onrefresssss", "Enterrrrrrrc");
        swipe_layout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        swipe_layout.setColorSchemeColors(Color.RED, Color.YELLOW, Color.GREEN);
        swipe_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!NetworkUtils.isConnected(JewelleryHome.this)) {
                            ToastMessage.onToast(JewelleryHome.this, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
                            swipe_layout.setRefreshing(false);
                            return;
                        } else {
                            getWishlistCount();
                            intilizerecyclerview();
                            getAllCats();
                            getAllProducts();
                            getbanners();
                            onrefresh();
                        }
                        swipe_layout.setRefreshing(false);
                    }
                }, 500);
            }
        });
    }

    public void getbanners() {
      //  getBannersData();

    }

    private Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
        }
    };


    BannersAdapter bannersAdapter;

    private void setupSlider() {
      /*  sliderItemList.add(new BannersModel(get_banners));

        bannersAdapter = new BannersAdapter(this, sliderItemList, viewPager);
        viewPager.setAdapter(bannersAdapter);
        bannersAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());
        indicator.setViewPager(viewPager);*/
        String[] image = new String[]{get_banners};
        for (int i = 0; i < image.length; i++) {
            Log.e("bannerurl", String.valueOf(urls));
            urls.add(image[i]);
        }

        viewPager.setAdapter(new PageviewAdapter(JewelleryHome.this, urls));

        CirclePageIndicator indicator = findViewById(R.id.indicator);

        indicator.setViewPager(viewPager);

        final float density = getResources().getDisplayMetrics().density;

        //Set circle indicator radius
        indicator.setRadius(4 * density);
        NUM_PAGES = urls.size();
        Log.e("pages", String.valueOf(NUM_PAGES));

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                // yourMethod();
                // Auto start of viewpager
                Handler handler = new Handler();
                Runnable Update = new Runnable() {
                    public void run() {
                        if (currentPage == NUM_PAGES - 1) {
                            currentPage = NUM_PAGES;
                            Log.e("curr", String.valueOf(NUM_PAGES));
                        }
                        viewPager.setCurrentItem(currentPage++, true);


                    }
                };
                handler.post(Update);
                handler.postDelayed(Update, 2000);
                Timer swipeTimer = new Timer();
                swipeTimer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            handler.post(Update);

                                        }
                                    },
                        1500, NUM_PAGES * 2000);
            }
        }, NUM_PAGES * 2000);   //2 seconds
        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                   //currentPage = position;
            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
                //   currentPage = pos;
            }

            @Override
            public void onPageScrollStateChanged(int pos) {
                currentPage = pos;

            }
        });


    }

    public void getBannersData() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();
        sliderItemList.clear();
        apiDao1 = ApiClient.getClient(AccountUtils.getAccessToken(this)).create(ApiDao.class);
        Call<List<Listmodel>> getdetails = apiDao1.getBannerImages("Bearer " + AccountUtils.getAccessToken(this));
        getdetails.enqueue(new Callback<List<Listmodel>>() {
            @Override
            public void onResponse(Call<List<Listmodel>> call, Response<List<Listmodel>> response) {
                int statuscoe = response.code();
                Log.e("Status code", String.valueOf(statuscoe));
                if (statuscoe == HttpsURLConnection.HTTP_OK || statuscoe == HttpsURLConnection.HTTP_CREATED) {
                    dialog.dismiss();
                    List<Listmodel> list = response.body();
                    if (list != null) {
                        for (Listmodel listmodel : list) {
                            get_banners = listmodel.getBanner_uri();
                            Log.e("Banners", get_banners);
                            setupSlider();
                        }
                    } else {
                        Toast.makeText(activity, "No Images", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    dialog.dismiss();
                    Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Listmodel>> call, Throwable t) {
                dialog.dismiss();
                Toast.makeText(activity, "onFailure", Toast.LENGTH_SHORT).show();
            }
        });

    }


    public void getAllCats() {

        catlist.clear();

        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();

        apiDao = ApiClient.getClient("").create(ApiDao.class);
        Call<List<Listmodel>> getcatlist = apiDao.getJewCats();

        getcatlist.enqueue(new Callback<List<Listmodel>>() {
            @SuppressLint({"LongLogTag", "NotifyDataSetChanged"})
            @Override
            public void onResponse(@NotNull Call<List<Listmodel>> call, @NotNull Response<List<Listmodel>> response) {
                int statuscode = response.code();
                Log.e("statuscode", String.valueOf(statuscode));
                flist = response.body();
                dialog.dismiss();
                if (statuscode == 200 || statuscode == 201) {
                    Log.e("statuscode", String.valueOf(statuscode));
                    if (flist != null) {
                        for (Listmodel listmodel : flist) {
                            cid = listmodel.getId();
                            catlist.add(listmodel);
                            catadapter.notifyDataSetChanged();
                        }

                    } else {
                        Log.e("catname", "No cats");
                    }
                } else if (statuscode == 422) {
                    dialog.dismiss();
                    Log.e("cv", String.valueOf(statuscode));
                } else {
                    dialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Listmodel>> call, @NonNull Throwable t) {
                dialog.dismiss();
                Log.e("ughb", String.valueOf(t));
            }
        });

    }

    public void intilizerecyclerview() {
        swipe_layout = findViewById(R.id.swipe_layout);
        productsrv = findViewById(R.id.productsrv);
        productsrv.setHasFixedSize(true);

        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(activity, 3);
        productsrv.setLayoutManager(gridLayoutManager);
        productsList = new ArrayList<>();
        adapter = new AllProductsAdapter(this, productsList, this, "home");
        productsrv.setAdapter(adapter);
        viewPager = findViewById(R.id.banners);
        sliderItemList = new ArrayList<>();


        /////////////////////////Categories////////////////
        catrv = findViewById(R.id.catrv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(JewelleryHome.this) {

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(JewelleryHome.this) {
                    private static final float SPEED = 8000f;// Change this value (default=25f)

                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        catlist = new ArrayList<>();
        catadapter = new AllCatAdapter(this, catlist, this);
        autoScrollAnother();
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        catrv.setLayoutManager(layoutManager);
        catrv.setHasFixedSize(true);
        catrv.setItemViewCacheSize(1000);
        catrv.setDrawingCacheEnabled(true);
        catrv.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        catrv.setAdapter(catadapter);


        ////////////////////////////////////////////////////
    }

    public void autoScrollAnother() {
        scrollCount = 0;
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void run() {
                catrv.smoothScrollToPosition((scrollCount++));
                if (scrollCount == catadapter.getItemCount()) {
                    catlist.addAll(catlist);
                    catadapter.notifyDataSetChanged();
                }
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    @Override
    public void onClick(View v) {
        Log.e("dvds", "fdsc");
    }

    @Override
    public void onItemClick(View view, int position) {
        Listmodel listmodel = catlist.get(position);
//        ToastMessage.onToast(getContext(), String.valueOf(listmodel.getId()), ToastMessage.SUCCESS);
        if (view.getId() == R.id.cimg) {
            //  init_category(listmodel.getId());
            Intent intent = new Intent(this, SubCatagory_Activity.class);
            intent.putExtra("catid", listmodel.getId());
            intent.putExtra("catName", listmodel.getCatname());
            intent.putExtra("from", "cats");
            startActivity(intent);
        } else if (view.getId() == R.id.selectwish) {
            getWishlistCount();
        }
        Log.e("wishlist", String.valueOf(view.getId()));
        Log.e("wishlist", String.valueOf(R.id.selectwish));
    }
}