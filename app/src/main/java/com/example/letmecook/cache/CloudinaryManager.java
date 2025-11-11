package com.example.letmecook.utils;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryManager {

    private static Cloudinary cloudinaryInstance;
    public static Cloudinary getInstance() {
        if (cloudinaryInstance == null) {
            cloudinaryInstance = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", "dqbehf1sz",
                    "api_key", "374825885647158",
                    "api_secret", "iTfgtx7HD5oNRDZwcp-mSIY5IIo"
            ));
        }
        return cloudinaryInstance;
    }
}
