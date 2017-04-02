package com.capstone.sejong.homenect;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by 12aud on 2017-03-31.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ThingViewHolder>{

    public class ThingViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView thingName;
        Switch status;
        ImageView thingPhoto;

        ThingViewHolder(View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            thingName = (TextView)itemView.findViewById(R.id.thing_name);
            status = (Switch)itemView.findViewById(R.id.thing_status);
            thingPhoto = (ImageView)itemView.findViewById(R.id.thing_photo);
            thingPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if((Integer)thingPhoto.getTag() == R.drawable.default_photo){
                        Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                    } else{
                        // 사진 찍어서 넣는 곳
                        Toast.makeText(v.getContext(), "있던 사진 눌린겨", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    List<Thing> things;

    RVAdapter(List<Thing> things){
        this.things = things;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ThingViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        ThingViewHolder tvh = new ThingViewHolder(v);
        return tvh;
    }

    @Override
    public void onBindViewHolder(ThingViewHolder thingViewHolder, int i){
        thingViewHolder.thingName.setText(things.get(i).name);
        thingViewHolder.status.setChecked(things.get(i).status);
        thingViewHolder.thingPhoto.setImageResource(things.get(i).photoId);
        thingViewHolder.thingPhoto.setTag(things.get(i).photoId);
    }

    @Override
    public int getItemCount(){
        return things.size();
    }

}
