package org.naturenet;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.HashMap;

public class CloudinaryClient {
    static String PUBLIC_ID = "public_id";
    static String CLOUD_NAME = "cloud_name";
    static String API_KEY = "api_key";
    static String API_SECRET = "api_secret";
    static String CLOUD_NAME_VALUE = "university-of-colorado";
    static String API_KEY_VALUE = "893246586645466";
    static String API_SECRET_VALUE = "8Liy-YcDCvHZpokYZ8z3cUxCtyk";
    public static HashMap getMyConfigs() {
        HashMap config = new HashMap();
        config.put(CLOUD_NAME, CLOUD_NAME_VALUE);
        config.put(API_KEY, API_KEY_VALUE);
        config.put(API_SECRET, API_SECRET_VALUE);
        return config;
    }
    public static String resize(String imageName, int imageWidth, int imageHeight) {
        Cloudinary cloud = new Cloudinary(getMyConfigs());
        Transformation t = new Transformation();
        t.width(imageWidth);
        t.height(imageHeight);
        return cloud.url().transformation(t).generate(imageName);
    }
    public static String upload(String imageName, String savedName) throws IOException {
        Cloudinary cloud = new Cloudinary(getMyConfigs());
        cloud.uploader().upload(imageName, ObjectUtils.asMap(PUBLIC_ID, savedName));
        return cloud.url().generate(savedName);
    }
}