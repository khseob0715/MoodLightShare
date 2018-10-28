package com.example.vclab.moodlightshare.model;

import java.util.List;

public class LightModel {
    public String ShareUserName;           // 공유한 사람 이름
    public String ShareLightDescription;  // 공유된 조명 설명 & 이름 등등
    public List<String> SharePixel;
    public String ShareDate;


    public LightModel(){}

    public LightModel(String ShareUserName, String ShareLightDescription, List<String> SharePixel, String ShareDate){
        this.ShareUserName = ShareUserName;
        this.ShareLightDescription = ShareLightDescription;
        this.SharePixel = SharePixel;
        this.ShareDate = ShareDate;
    }

}
