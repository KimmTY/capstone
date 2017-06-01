package com.capstone.sejong.homenect;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 12aud on 2017-03-31.
 */

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ThingViewHolder>{

    public class ThingViewHolder extends RecyclerView.ViewHolder{
        CardView cv; // cardView
        TextView thingName; // Thing 이름
        TextView status; // Thing 상태 표시 (on/off)
        TextView time; // 예약제어 시간 표시 (11:05)
        ImageView wifi; // Thing wifi 연결상태 표시 (red / green)
        ImageView timer; // Thing Timer 아이콘
        TextView timerTimeControl; // 예약시 on/off 여부
        ImageView gps; // Thing GPS 아이콘
        ImageView thingIcon; // Power 아이콘
        RelativeLayout relativeLayout; // 배경

        ThingViewHolder(final View itemView){
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.thing_background);
            thingName = (TextView)itemView.findViewById(R.id.thing_name);
            status = (TextView)itemView.findViewById(R.id.status);
            time = (TextView)itemView.findViewById(R.id.time);
            wifi = (ImageView)itemView.findViewById(R.id.iv_wifi_status);
            timer = (ImageView)itemView.findViewById(R.id.iv_timer);
            timer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(things.get(getPosition()).isTimerStatus()){ // 켜진 상태라면 (꺼야함)
                        things.get(getPosition()).setTimerStatus(false);
                        timer.setImageResource(R.drawable.timer);
                        pref.putValue("timerStatus", false, things.get(getPosition()).getName());
                    } else { // 꺼진 상태라면 (켜야함)
                        things.get(getPosition()).setTimerStatus(true);
                        timer.setImageResource(R.drawable.timer_on);
                        if(mContext instanceof AdapterCallback){
                            ((AdapterCallback)mContext).showTimerDialog(getPosition()); // 타이머 다이얼로그
                        }
                        pref.putValue("timerStatus", true, things.get(getPosition()).getName());
                        time.setText(things.get(getPosition()).getTimerTime());
                    }
                }
            });
            gps = (ImageView)itemView.findViewById(R.id.iv_gps_status);
            gps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(things.get(getPosition()).isGpsStatus()){ // 켜진 상태라면 (꺼야함)
                        things.get(getPosition()).setGpsStatus(false);
                        gps.setImageResource(R.drawable.gps);
                    } else { // 꺼진 상태라면 (켜야함)
                        things.get(getPosition()).setGpsStatus(true);
                        gps.setImageResource(R.drawable.gps_on);
                    }
                    pref.putValue("gpsStatus", things.get(getPosition()).isGpsStatus(), things.get(getPosition()).getName());
                }
            });
            thingIcon = (ImageView)itemView.findViewById(R.id.thing_icon);
            thingIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(status.getText().equals("OFF")) { // 꺼진상태라면 (켜야함)
                        relativeLayout.setBackgroundColor(ContextCompat.getColor(
                                itemView.getContext(), R.color.colorLightBlue));
                        ((AdapterCallback)mContext).mqttPublish("powerOn", getPosition()); // 파워 컨트롤
                        things.get(getPosition()).setStatus(true); // 전원 on
                        status.setText("ON");
                    } else { // 켜진상태라면 (꺼야함)
                        relativeLayout.setBackgroundColor(ContextCompat.getColor(
                                itemView.getContext(), R.color.colorDarkGray));
                        ((AdapterCallback)mContext).mqttPublish("powerOff", getPosition()); // 파워 컨트롤
                        things.get(getPosition()).setStatus(false); // 전원 off
                        status.setText("OFF");
                    }
                }
            });
            timerTimeControl = (TextView) itemView.findViewById(R.id.timerTimeControl);
            timerTimeControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(timerTimeControl.getText().equals("OFF")){ // 꺼진 상태라면 켜야함
                        // mqtt publish
                        timerTimeControl.setText("ON");
                        things.get(getPosition()).setTimerTimeControl(true);
                        ((AdapterCallback)mContext).mqttPublish("saveSchedule", getPosition()); // 예약 기능
                    }else {
                        timerTimeControl.setText("OFF");
                        things.get(getPosition()).setTimerTimeControl(false);
                        ((AdapterCallback)mContext).mqttPublish("saveSchedule", getPosition()); // 예약 기능
                    }
                }
            });
        }
    }

    private Context mContext;
    List<Thing> things;
    SharedPreferences pref;

    RVAdapter(Context context, List<Thing> things){
            this.things = things;
            this.mContext = context;
        pref = new SharedPreferences(mContext);
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

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(ThingViewHolder thingViewHolder, int i){
        thingViewHolder.thingName.setText(things.get(i).getName());
        thingViewHolder.thingIcon.setImageResource(R.drawable.power_button);
        thingViewHolder.time.setText(things.get(i).getTimerTime());
        thingViewHolder.wifi.setImageResource(R.drawable.circle_red);
        thingViewHolder.timer.setImageResource(R.drawable.timer);
        thingViewHolder.gps.setImageResource(R.drawable.gps);

        if(things.get(i).isStatus()){ // 상태 초기화
            thingViewHolder.status.setText("ON");
            thingViewHolder.relativeLayout.setBackgroundColor(ContextCompat.getColor(
                    thingViewHolder.relativeLayout.getContext(), R.color.colorLightBlue));
        } else{
            thingViewHolder.status.setText("OFF");
            thingViewHolder.relativeLayout.setBackgroundColor(ContextCompat.getColor(
                    thingViewHolder.relativeLayout.getContext(), R.color.colorDarkGray));
        }
        if(things.get(i).isWifiStatus()){
            // wifi 색 초기화
            thingViewHolder.wifi.setImageResource(R.drawable.circle_green);
        } else{
            thingViewHolder.wifi.setImageResource(R.drawable.circle_red);
        }
        if(things.get(i).isGpsStatus()){
            // GPS 아이콘 색 초기화
            thingViewHolder.gps.setImageResource(R.drawable.gps_on);
        } else{
            thingViewHolder.gps.setImageResource(R.drawable.gps);
        }
        if(things.get(i).isTimerStatus()){
            // Timer 아이콘 색 초기화
            thingViewHolder.timer.setImageResource(R.drawable.timer_on);
        } else{
            thingViewHolder.timer.setImageResource(R.drawable.timer);
        }
        if(things.get(i).isTimerTimeControl()){
            // Timer 제어 on/off 표시
            thingViewHolder.timerTimeControl.setText("ON");
        } else {
            thingViewHolder.timerTimeControl.setText("OFF");
        }
    }

    @Override
    public int getItemCount(){
        return things.size();
    }

}
