package com.goldsikka.goldsikka.Activitys.MoneyWallet.Fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.goldsikka.goldsikka.Activitys.Events.EventModel;
import com.goldsikka.goldsikka.Activitys.LoginActivity;
import com.goldsikka.goldsikka.Activitys.RazorpayPayment;
import com.goldsikka.goldsikka.Fragments.baseinterface;
import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.Utils.AccountUtils;
import com.goldsikka.goldsikka.Utils.NetworkUtils;
import com.goldsikka.goldsikka.Utils.ToastMessage;
import com.goldsikka.goldsikka.Withdraw_popup;
import com.goldsikka.goldsikka.interfaces.ApiDao;
import com.goldsikka.goldsikka.model.Listmodel;
import com.goldsikka.goldsikka.netwokconnection.ApiClient;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.goldsikka.goldsikka.Activitys.Predict_price.PaginationListener.PAGE_START;

public class AddMoney extends Fragment implements View.OnClickListener {

    EditText et_add_money;
    Button btn_add;

    String st_money;

    ApiDao apiDao;

    TextView tv_withdraw;

    TextView tvwalletmoney;

    String Walletamount;

    TextView unameTv, uidTv;
    private Activity activity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.add_money, container, false);
        ButterKnife.bind(view, activity);
        initlizeview(view);
        wallet_amount();
        //Checkout.preload(activity);

        apiDao = ApiClient.getClient(AccountUtils.getAccessToken(activity)).create(ApiDao.class);

        return  view;
    }

    public void wallet_amount(){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();
        if (!NetworkUtils.isConnected(activity)){
            ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            dialog.dismiss();
            return;
        }else {
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(activity)).create(ApiDao.class);
            Call<Listmodel> getdetails = apiDao.walletAmount("Bearer "+AccountUtils.getAccessToken(activity));
            getdetails.enqueue(new Callback<Listmodel>() {
                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
                    int statuscode  = response.code();
                    Log.e("statuscode dd" , String.valueOf(statuscode));
                    if (statuscode == HttpsURLConnection.HTTP_OK) {
                        Listmodel list = response.body();
                        Walletamount = list.getAmount_wallet();
                        tvwalletmoney.setText("Wallet Balance : "+getString(R.string.Rs)+" "+Walletamount);

                        dialog.dismiss();

                    }else {

                        dialog.dismiss();

                        ToastMessage.onToast(activity,"Technical issue",ToastMessage.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                    Log.e("on fails",t.toString());
                    dialog.dismiss();
                    ToastMessage.onToast(activity,"We have some issue",ToastMessage.ERROR);
                }
            });
        }

    }

    public void initlizeview(View view){
        tvwalletmoney = view.findViewById(R.id.tvwalletmoney);
        et_add_money = view.findViewById(R.id.et_addmoney);
        btn_add = view.findViewById(R.id.btn_addmoney);
        tv_withdraw = view.findViewById(R.id.tv_withdraw);
        tv_withdraw.setOnClickListener(this);
        btn_add.setOnClickListener(this::onClick);


        et_add_money.setHint(Html.fromHtml(getString(R.string.wallet_hint)));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_addmoney:
                addmoney();
                break;

            case R.id.tv_withdraw:
                opneWithdrae();
                break;

        }
    }

    public void opneWithdrae(){
        Intent intent = new Intent(activity, Withdraw_popup.class);
        intent.putExtra("Walletamount",Walletamount);
        startActivity(intent);
    }

    public void addmoney(){
        st_money = et_add_money.getText().toString().trim();
        if (st_money.isEmpty()){
            ToastMessage.onToast(activity,"Please enter amount",ToastMessage.ERROR);
        }else if (Integer.parseInt(st_money) < 50){
            ToastMessage.onToast(activity,"Please add min 50rs",ToastMessage.ERROR);

        }else  if (Integer.parseInt(st_money) > 1000000){
            ToastMessage.onToast(activity,"please add max 1,00,000",ToastMessage.ERROR);

        }else {
            Intent intent = new Intent(activity, RazorpayPayment.class);
            intent.putExtra("amount",st_money);
            startActivityForResult(intent,1212);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1212){
            String paymentid = data.getStringExtra("PaymentID");
            sendMoney(paymentid);
            Log.e("Payment Result", String.valueOf(paymentid));
        }
    }

    @Override
    public void onDetach() {
        this.activity = null;
        super.onDetach();
    }




    public void sendMoney(String paymentid){

        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage("Please Wait....");
        dialog.setCancelable(false);
        dialog.show();
        if (!NetworkUtils.isConnected(activity)){
            ToastMessage.onToast(activity, getString(R.string.error_no_internet_connection), ToastMessage.ERROR);
            dialog.dismiss();
            return;
        }else {
            apiDao = ApiClient.getClient(AccountUtils.getAccessToken(activity)).create(ApiDao.class);
            Call<Listmodel> getdetails = apiDao.addmoney_wallet("Bearer "+AccountUtils.getAccessToken(activity),st_money,paymentid);
            getdetails.enqueue(new Callback<Listmodel>() {
                @Override
                public void onResponse(Call<Listmodel> call, Response<Listmodel> response) {
                    int statuscode  = response.code();
                    Log.e("statuscode dd" , String.valueOf(statuscode));
                    if (statuscode == HttpsURLConnection.HTTP_OK) {
                        Listmodel list = response.body();
                        et_add_money.setText("");
                        wallet_amount();

                        ToastMessage.onToast(activity,"Money Added",ToastMessage.SUCCESS);

                        dialog.dismiss();
                    }else {
                        dialog.dismiss();
                        ToastMessage.onToast(activity,"Technical issue",ToastMessage.ERROR);
                    }
                }

                @Override
                public void onFailure(Call<Listmodel> call, Throwable t) {
                    Log.e("on fails",t.toString());
                    dialog.dismiss();
                    ToastMessage.onToast(activity,"We have some issue",ToastMessage.ERROR);
                }
            });
        }

    }


}
