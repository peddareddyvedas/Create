package com.goldsikka.goldsikka.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.goldsikka.goldsikka.R;
import com.goldsikka.goldsikka.model.dw_contentlist;

import java.util.ArrayList;

public class digital_wallet_content_adatper extends RecyclerView.Adapter<digital_wallet_content_adatper.SubCategoryViewHolder> {

    private Context context;
    private ArrayList<dw_contentlist> arrayList;
    Animation animBlink;

    public digital_wallet_content_adatper(ArrayList<dw_contentlist> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.faqs_design, parent, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

        dw_contentlist item = arrayList.get(position);

        holder.tvquestion.setText(item.getHeading());
        holder.tvanswer.setText(item.getSubheading());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class SubCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvquestion,tvanswer;
        ImageView ivdownarrow;
        LinearLayout answerll,ll_q_a;

        SubCategoryViewHolder(View itemView) {
            super(itemView);

            tvquestion = itemView.findViewById(R.id.tvquestion);
            tvanswer = itemView.findViewById(R.id.tvanswer);
            ivdownarrow = itemView.findViewById(R.id.ivdown_arrow);
            answerll = itemView.findViewById(R.id.answer_ll);
            ll_q_a = itemView.findViewById(R.id.ll_q_a);
            ll_q_a.setOnClickListener(this);
            ivdownarrow.setOnClickListener(this);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            switch (v.getId()){

                case R.id.ivdown_arrow:
                    onrotate();
                    return;

                case R.id.ll_q_a:
                    onrotate();
                    return;

            }

        }

        private void onrotate(){

            Animation rotateIvSwitch = AnimationUtils.loadAnimation(context,R.anim.rotate);
            ivdownarrow.startAnimation(rotateIvSwitch);

            if (answerll.getVisibility() == View.VISIBLE) {
                ivdownarrow.setImageResource(R.drawable.ic_expand_more);
                answerll.setVisibility(View.GONE);
            } else {
                ivdownarrow.setImageResource(R.drawable.ic_expand_less);
                answerll.setVisibility(View.VISIBLE);
            }

        }
    }
}