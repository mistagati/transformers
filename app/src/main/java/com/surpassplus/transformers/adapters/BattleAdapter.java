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
import com.surpassplus.transformers.model.Battle;
import com.surpassplus.transformers.model.Transformer;
import com.surpassplus.transformers.model.Winner;

import java.util.ArrayList;

import static android.view.View.VISIBLE;

/**
 * Adapter responsible for loading specific branch information in the Recycler View list.
 */
public class BattleAdapter extends RecyclerView.Adapter<BattleAdapter.MyViewHolder> {
    private Context mContext;
    private FragmentManager mFragmentManager;
    private TransformerClickListenerInterface mTransformerClickListenerInterface;
    private ArrayList<Battle> mBattles = new ArrayList<>();
    private int lastPosition=-1;

    public BattleAdapter(ArrayList<Battle> battles, Context context, FragmentManager fragmentManager, TransformerClickListenerInterface transformerClickListenerInterface) {
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        this.mTransformerClickListenerInterface = transformerClickListenerInterface;
        this.mBattles = battles;
    }

    @Override
    public BattleAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.battle_list_item, parent, false);
        return new BattleAdapter.MyViewHolder(itemView);
    }

    private String str(Integer integer) {
        return integer + "";
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        /*
        holder.nameTextView.setText(mTransformerResults.get(position).getName());
        holder.rankTextView.setText(str(mTransformerResults.get(position).getRank()));
        holder.strengthTextView.setText(str(mTransformerResults.get(position).getStrength()));
        holder.intelligenceTextView.setText(str(mTransformerResults.get(position).getIntelligence()));
        holder.speedTextView.setText(str(mTransformerResults.get(position).getSpeed()));
        holder.enduranceTextView.setText(str(mTransformerResults.get(position).getEndurance()));
        holder.courageTextView.setText(str(mTransformerResults.get(position).getCourage()));
        holder.firepowerTextView.setText(str(mTransformerResults.get(position).getFirepower()));
        holder.skillTextView.setText(str(mTransformerResults.get(position).getSkill()));
        holder.overallRatingTextView.setText(str(mTransformerResults.get(position).getOverallRating()));*/
        //holder.teamTextView.setText(mTransformerResults.get(position).getTeam());


        //String url = HeroesApp.getInstance().getBaseAPIUrl() + heroList.get(position).imageUrl();

        Battle battle = mBattles.get(position);
        Transformer autobot = battle.getAutobot();
        Transformer decepticon = battle.getDecepticon();

        String url = autobot.getTeamIcon();
        RequestOptions options = new RequestOptions().centerCrop();
        Glide.with(mContext).load(url).apply(options).into(holder.aTeamIconImageView);


        holder.aNameTextView.setText(autobot.getName());

        if (battle.getWinner() == Winner.AUTOBOT) {
            holder.aWinImageView.setVisibility(VISIBLE);
        } else if (battle.getWinner() == Winner.DECEPTICON) {
            holder.dWinImageView.setVisibility(VISIBLE);
        }

        Glide.with(mContext).load(decepticon.getTeamIcon()).apply(options).into(holder.dTeamIconImageView);
        holder.dNameTextView.setText(decepticon.getName());

        holder.mBattleNumberValue.setText("#"+battle.getBattleNumber());

        ArrayList<String> winnerStats = battle.getWinnerStats();
        if (winnerStats.size() > 0) {
            String stats = "";
            for (int i=0; i< winnerStats.size(); i++) {
                stats = stats + winnerStats.get(i);
                if (i<winnerStats.size()-1) {
                    stats = stats + "; ";
                }
            }
            holder.mWinStatsTextView.setText(stats);
        }


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
        return mBattles.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView aTeamIconImageView;
        public TextView aNameTextView;
        public ImageView dTeamIconImageView;
        public TextView dNameTextView;
        public ImageView dWinImageView;
        public ImageView aWinImageView;

        public TextView mBattleNumberValue;

        public TextView mWinStatsTextView;

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

            aTeamIconImageView = (ImageView) view.findViewById(R.id.aTeamIconImageView);
            aNameTextView = (TextView) view.findViewById(R.id.aNameTextView);
            dTeamIconImageView = (ImageView) view.findViewById(R.id.dTeamIconImageView);
            dNameTextView = (TextView) view.findViewById(R.id.dNameTextView);
            aWinImageView = (ImageView) view.findViewById(R.id.aWinImageView);
            dWinImageView = (ImageView) view.findViewById(R.id.dWinImageView);
            mBattleNumberValue = (TextView) view.findViewById(R.id.battleNumberValue);
            mWinStatsTextView = (TextView) view.findViewById(R.id.winStatsTextView);


            /*
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
            overallRatingTextView = (TextView) view.findViewById(R.id.overallRatingValue);*/


            mTransformerContainerLinearLayOut = (LinearLayout) view.findViewById(R.id.transformerContainerLinearLayout);
        }

    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.aNameTextView.clearAnimation();
    }
}
