package com.example.graduationproject;

import com.example.graduationproject.models.UserPrivateInfo;
import com.example.graduationproject.models.UserPublicInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class Converter {

    public static UserPrivateInfo ConvertMapToUserPrivateInfo(Map<String, Object> map) {
        return new ObjectMapper().convertValue(map, UserPrivateInfo.class);
    }

    public static UserPublicInfo ConvertMapToUserPublicInfo(Map<String, Object> map) {
        return new ObjectMapper().convertValue(map, UserPublicInfo.class);
    }

    public static Map<String, Object> convertUserPrivateInfoToMap(UserPrivateInfo userPrivateInfo) {
        return new ObjectMapper().convertValue(userPrivateInfo, new TypeReference<Map<String, Object>>() {
        });
    }

    public static Map<String, Object> convertUserPublicInfoToMap(UserPublicInfo userPublicInfo) {
        return new ObjectMapper().convertValue(userPublicInfo, new TypeReference<Map<String, Object>>() {
        });
    }

}
