package com.surpassplus.transformers.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.surpassplus.transformers.R;
import com.surpassplus.transformers.intefaces.TransformerClickListenerInterface;
import com.surpassplus.transformers.model.Transformer;

import java.util.ArrayList;

/**
 * Adapter responsible for loading specific branch information in the Recycler View list.
 */
public class TransformerAdapter extends RecyclerView.Adapter<TransformerAdapter.MyViewHolder> {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private TransformerClickListenerInterface mTransformerClickListenerInterface;
    private ArrayList<Transformer> mTransformerResults = new ArrayList<>();
    private int lastPosition=-1;

    public TransformerAdapter(ArrayList<Transformer> transformerResults, Context context, FragmentManager fragmentManager, TransformerClickListenerInterface transformerClickListenerInterface) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        this.mTransformerClickListenerInterface = transformerClickListenerInterface;
        this.mTransformerResults = transformerResults;
    }

    @Override
    public TransformerAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.transformer_list_item, parent, false);
        return new TransformerAdapter.MyViewHolder(itemView);
    }

    private String str(Integer integer) {
        return integer + "";
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.nameTextView.setText(mTransformerResults.get(position).getName());
        holder.rankTextView.setText(str(mTransformerResults.get(position).getRank()));
        holder.strengthTextView.setText(str(mTransformerResults.get(position).getStrength()));
        holder.intelligenceTextView.setText(str(mTransformerResults.get(position).getIntelligence()));
        holder.speedTextView.setText(str(mTransformerResults.get(position).getSpeed()));
        holder.enduranceTextView.setText(str(mTransformerResults.get(position).getEndurance()));
        holder.courageTextView.setText(str(mTransformerResults.get(position).getCourage()));
        holder.firepowerTextView.setText(str(mTransformerResults.get(position).getFirepower()));
        holder.skillTextView.setText(str(mTransformerResults.get(position).getSkill()));
        holder.overallRatingTextView.setText(str(mTransformerResults.get(position).getOverallRating()));
        //holder.teamTextView.setText(mTransformerResults.get(position).getTeam());


        //String url = HeroesApp.getInstance().getBaseAPIUrl() + heroList.get(position).imageUrl();
        String url = mTransformerResults.get(position).getTeamIcon();
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(mContext).load(url).apply(options).into(holder.teamIconImageView);


        //holder.textViewBranchName.setText(mBranchResults.get(position).getName());

        //PostalAddress address = mBranchResults.get(position).getPostalAddress();
        //String location = address.getTownName();
        //if (address.getCountrySubDivision().size() > 0) {
        //    location = location + ", " + address.getCountrySubDivision().get(0);
        //}
        //location = location + " " + address.getCountry();

        //holder.textViewLocationName.setText(location);
        holder.mTransformerContainerLinearLayOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTransformerClickListenerInterface.onMyItemClickCallBack(position);
            }
        });

        Animation animation = AnimationUtils.loadAnimation(mContext,
                (position > lastPosition) ? R.anim.up_from_bottom
                        : R.anim.down_from_top);
        holder.itemView.startAnimation(animation);
        lastPosition = position;

    }


    @Override
    public int getItemCount() {
        return mTransformerResults.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView teamIconImageView;
        public TextView nameTextView;
        //public TextView teamTextView;
        public TextView rankTextView;
        public TextView strengthTextView;
        public TextView intelligenceTextView;
        public TextView speedTextView;
        public TextView enduranceTextView;
        public TextView courageTextView;
        public TextView firepowerTextView;
        public TextView skillTextView;
        public TextView overallRatingTextView;

        private LinearLayout mTransformerContainerLinearLayOut;


        public MyViewHolder(View view) {
            super(view);
            nameTextView = (TextView) view.findViewById(R.id.nameTextView);
            //teamTextView = (TextView) view.findViewById(R.id.teamTextView);
            teamIconImageView = (ImageView) view.findViewById(R.id.teamIconImageView);
            rankTextView = (TextView) view.findViewById(R.id.rankValue);
            strengthTextView = (TextView) view.findViewById(R.id.strengthValue);
            intelligenceTextView = (TextView) view.findViewById(R.id.intelligenceValue);
            speedTextView = (TextView) view.findViewById(R.id.speedValue);
            enduranceTextView = (TextView) view.findViewById(R.id.enduranceValue);
            courageTextView = (TextView) view.findViewById(R.id.courageValue);
            firepowerTextView = (TextView) view.findViewById(R.id.firepowerValue);
            skillTextView = (TextView) view.findViewById(R.id.skillValue);
            overallRatingTextView = (TextView) view.findViewById(R.id.overallRatingValue);


            mTransformerContainerLinearLayOut = (LinearLayout) view.findViewById(R.id.transformerContainerLinearLayout);
        }

    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.nameTextView.clearAnimation();
    }
}
