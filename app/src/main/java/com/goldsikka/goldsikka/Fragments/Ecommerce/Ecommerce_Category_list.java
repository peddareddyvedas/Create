package com.goldsikka.goldsikka.Fragments.Ecommerce;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.goldsikka.goldsikka.Activitys.Kyc_Details;
import com.goldsikka.goldsikka.Activitys.Nominee_Details;
import com.goldsikka.goldsikka.Activitys.Profile.CustomerAddressList;
import com.goldsikka.goldsikka.Adapter.Ecommerce.Ecommerce_Categorylist_Adapter;
import com.goldsikka.goldsikka.ComingSoon;
import com.goldsikka.goldsikka.Fragments.Customer_BankDetailslist;
import com.goldsikka.goldsikka.Fragments.Edit_coustomer_details;
import com.goldsikka.goldsikka.Fragments.Get_kyc_details_fragment;
import com.goldsikka.goldsikka.MainActivity;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.Utils.shared_preference;
import com.goldsikka.goldsikka.WelcomeActivity;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.interfaces.OnItemClickListener;
import com.goldsikka.goldsikka.model.Listmodel;
import com.goldsikka.goldsikka.model.data;
import com.goldsikka.goldsikka.netwokconnection.ApiClient;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Ecommerce_Category_list extends Fragment implements OnItemClickListener, View.OnClickListener {

    ApiDao apiDao;
    private Activity activity;
    RecyclerView rv_categorylist;

    Ecommerce_Categorylist_Adapter adapter;
    ArrayList<Listmodel> arrayList;
        ImageView iv_cart;
    String categoryid;
    TextView tv_item;
//    ImageView ivprofile;

    shared_preference sharedPreference;
    String stavathar,stprofileimg;

    TextView uidTv, unameTv;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ecommerce_cotegorylist,container,false);

        uidTv = view.findViewById(R.id.uid);
        unameTv = view.findViewById(R.id.uname);

        uidTv.setText(AccountUtils.getCustomerID(activity));
        unameTv.setText(AccountUtils.getName(activity));

//        Toolbar toolbar = view.findViewById(R.id.toolbar);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Ecommerce");
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                //getActivity().onBackPressed();
//                Intent intent = new Intent(activity, MainActivity.class);
//                startActivity(intent);
//            }
//        });
//        iv_cart = view.findViewById(R.id.iv_cart);
//        iv_cart.setOnClickListener(this);
//        tv_item = view.findViewById(R.id.tv_itemcount);
//        String noofproducts = AccountUtils.getProductQuantity(activity);
//        if (noofproducts.equals("0")){
//            tv_item.setVisibility(View.GONE);
//        }else {
//            tv_item.setText(noofproducts);
//        }


        intilizerecyclerview(view);
        CustomerDetails();
//        if (AccountUtils.getAvathar(activity) != null){
//            Glide.with(activity)
//                    .load(AccountUtils.getProfileImg(activity))
//                    .into(ivprofile);
//
////            Picasso.with(activity).load(AccountUtils.getProfileImg(activity)).into(ivprofile);
//        }else {
//            ivprofile.setImageResource(R.drawable.profile);
//        }
//        ivprofile.setOnClickListener(this);

        getdata(view);
        return view;
    }

    public void CustomerDetails(){


        if (!NetworkUtils.isConnected(activity)){
            ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
        }else {
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(activity)).create(ApiDao.class);
            Call<Listmodel> getprofile = apiDao.getprofile_details("Bearer " + AccountUtils.getAccessToken(activity));
            getprofile.enqueue(new Callback<Listmodel>() {

                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {

                    int stauscode = response.code();
                    if (stauscode == HttpsURLConnection.HTTP_CREATED || stauscode == HttpsURLConnection.HTTP_OK) {
                        List<Listmodel> list = Collections.singletonList(response.body());
                        if (list != null) {
                            for (Listmodel listmodel : list) {

                                AccountUtils.setIsPin(activity,listmodel.getIsgspin());

                                stavathar = listmodel.getAvatar();
                                AccountUtils.setAvathar(activity,stavathar);
                                if (stavathar != null){
                                    stprofileimg = listmodel.getAvatarImageLink();
                                    AccountUtils.setProfileImg(activity,stprofileimg);
//                                    Glide.with(activity)
//                                            .load(listmodel.getAvatarImageLink())
//                                            .into(ivprofile);
                                    //Picasso.with(activity).load(listmodel.getAvatarImageLink()).into(ivprofileimg);
                                }else {
                                    AccountUtils.setAvathar(activity,null);

//                                    ivprofile.setImageResource(R.drawable.profile);
                                }




                            }
                        }

                    }
                    else if (stauscode == HttpsURLConnection.HTTP_UNAUTHORIZED){
                        sharedPreference.WriteLoginStatus(false);
                        Intent intent = new Intent(activity, WelcomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        try {
                            assert response.errorBody() != null;
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            String st = jObjError.getString("message");
                            JSONObject er = jObjError.getJSONObject("errors");
                            Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                    ToastMessage.onToast(activity, "We Have Some Issues", ToastMessage.ERROR);
                    Log.e("Error", t.toString());
                }

            });

        }

    }


    public void getdata(View view){

        arrayList.clear();

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();

        apiDao = ApiClient.getClient("").create(ApiDao.class);
        Call<data> get_cotegorylist = apiDao.get_cotegorylist();
        get_cotegorylist.enqueue(new Callback<data>() {
            @Override
            public void onResponse(Call<data> call, Response<data> response) {
                int statuscode =  response.code();

                Log.e("styatus code",String.valueOf(statuscode));
                List<Listmodel> listmodel = response.body().getResult();
                if (statuscode== HttpsURLConnection.HTTP_OK){
                    dialog.dismiss();
                    for (Listmodel listmodel1 :listmodel){
                    categoryid = listmodel1.getId();
                    arrayList.add(listmodel1);
                    adapter.notifyDataSetChanged();
                    }

                }else {
                        dialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<data> call, Throwable t) {
                dialog.dismiss();
                ToastMessage.onToast(activity,"we have some issues",ToastMessage.ERROR);

            }
        });

    }

    public void intilizerecyclerview(View view){
//        ivprofile = view.findViewById(R.id.ivprofileimg);
        rv_categorylist = view.findViewById(R.id.rv_categorylist);

        rv_categorylist.setHasFixedSize(true);
        rv_categorylist.setLayoutManager(new GridLayoutManager(activity,2));
//        rv_categorylist.setItemAnimator(new DefaultItemAnimator());
//
//        RecyclerView.ItemDecoration decoration = new DividerItemDecoration(activity,LinearLayoutManager.VERTICAL);
//        rv_categorylist.addItemDecoration(decoration);
        arrayList = new ArrayList<>();
        adapter = new Ecommerce_Categorylist_Adapter(activity,arrayList,this);
        rv_categorylist.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {

        Listmodel listmodel = arrayList.get(position);

        if (view.getId() == R.id.iv_categoryimg) {
          //  init_category(listmodel.getId());
            Intent intent = new Intent(activity, ComingSoon.class);
            startActivity(intent);
        }

    }
    public void init_category(String id){
        AccountUtils.setMainCategoryId(activity,id);

        Subcategorylist fragment = new Subcategorylist();
        getFragmentManager().beginTransaction().replace(R.id.frame_layout,fragment).commit();


    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.iv_cart) {
//            Bundle bundle = new Bundle();
//            bundle.putString("from","maincategory");
//            Cartlist frgment = new Cartlist();
//            frgment.setArguments(bundle);
//            getFragmentManager().beginTransaction().replace(R.id.frame_layout, frgment).commit();
//        }
//        if (v.getId() == R.id.ivprofileimg) {
//            BottomNavigation(v);
//        }
    }
    TextView tveditprofile,tvnominee,tvkyc,tvbank,tvaddress,tvemail,tvname,tvcustomerid;
    ImageView ivprofileb;
    public void BottomNavigation(View view){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity,R.style.BottomSheetDialogTheme);
        bottomSheetDialog.setCancelable(true);
        View bottomSheet = LayoutInflater.from(activity).inflate(R.layout.profilebottomview,view.findViewById(R.id.bottomsheet));
        bottomSheetDialog.setContentView(bottomSheet);
        ((View) bottomSheet.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tveditprofile = bottomSheet.findViewById(R.id.tvprofileedit);
        tvnominee = bottomSheet.findViewById(R.id.tvnominee);
        tvkyc = bottomSheet.findViewById(R.id.tvkyc);
        tvbank = bottomSheet.findViewById(R.id.tvbankdetails);
        tvaddress = bottomSheet.findViewById(R.id.tvaddress);
        tvemail = bottomSheet.findViewById(R.id.tvemail);
        tvname = bottomSheet.findViewById(R.id.tvname);
        tvcustomerid = bottomSheet.findViewById(R.id.tvcustomerid);
        tvcustomerid.setText("CustomerId : "+AccountUtils.getCustomerID(activity));
        ivprofileb = bottomSheet.findViewById(R.id.ivprofile);
        if (AccountUtils.getAvathar(activity) != null){
            //  Picasso.with(activity).load(AccountUtils.getProfileImg(activity)).into(ivprofileb);
            Glide.with(activity)
                    .load(AccountUtils.getProfileImg(activity))
                    .into(ivprofileb);
        }else {
            ivprofileb.setImageResource(R.drawable.user);
        }
        tvname.setText(AccountUtils.getName(activity));
        tvemail.setText(AccountUtils.getEmail(activity));
        tvkyc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_kycdetails();
            }
        });
        tveditprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(activity)){
                    ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);

                }else {
                    Intent intent = new Intent(activity, Edit_coustomer_details.class);

                    intent.putExtra("name",AccountUtils.getName(activity));
                    intent.putExtra("email",AccountUtils.getEmail(activity));
                    intent.putExtra("mobile",AccountUtils.getMobile(activity));
                    intent.putExtra("profileimage",AccountUtils.getProfileImg(activity));
                    startActivity(intent);}
            }

        });
        tvnominee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_nomineedetails();
            }
        });
        tvaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(activity)){
                    ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
                }else {
                    Intent intent = new Intent(activity, CustomerAddressList.class);
                    startActivity(intent);
                }
            }
        });
        tvbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtils.isConnected(activity)){
                    ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);

                }else {
                    Intent intent = new Intent(activity, Customer_BankDetailslist.class);
                    startActivity(intent);
                }
            }
        });


        bottomSheetDialog.setContentView(bottomSheet);
        bottomSheetDialog.show();
    }
    public void get_nomineedetails(){
        if (!NetworkUtils.isConnected(activity)){
            ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
        }else {
            Intent intent = new Intent(activity, Nominee_Details.class);
            startActivity(intent);
        }
    }

    public void get_kycdetails(){
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();

        if (!NetworkUtils.isConnected(activity)){
            ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            dialog.dismiss();
        }else {
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(activity)).create(ApiDao.class);
            Call<Listmodel> iskyc = apiDao.checkkyc("Bearer "+AccountUtils.getAccessToken(activity));
            iskyc.enqueue(new Callback<Listmodel>() {
                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
                    int statuscode = response.code();
                    Listmodel listmodel = response.body();
                    if (statuscode == HttpsURLConnection.HTTP_OK){
                        dialog.dismiss();
                        if (listmodel.isKyc()){
                            Intent intent = new Intent(activity, Get_kyc_details_fragment.class);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(activity, Kyc_Details.class);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                    dialog.dismiss();
                }
            });


        }
    }

}
