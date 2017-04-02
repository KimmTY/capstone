package com.capstone.sejong.homenect;

/**
 * Created by 12aud on 2017-03-31.
 */

public class Thing {
    String name; // things 이름 or 맥주소
    boolean status; // On/off 여부
    int photoId; // 사진 찍어서 뷰 올리기

    Thing(String name, boolean status, int photoId){
        this.name = name;
        this.status = status;
        this.photoId = photoId;
    }
}
