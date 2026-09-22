package io.github.trip.shiv.vcledger.business.imagestorage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.cloudinary.utils.ObjectUtils;

import com.cloudinary.Cloudinary;

/**
 * Builds the Cloudinary client bean from application.properties:
 *
 *   cloudinary.cloud-name=your_cloud_name
 *   cloudinary.api-key=your_api_key
 *   cloudinary.api-secret=your_api_secret
 *
 * (values come from your Cloudinary dashboard, free tier is fine)
 *
 * Requires in pom.xml:
 *   <dependency>
 *       <groupId>com.cloudinary</groupId>
 *       <artifactId>cloudinary-http5</artifactId>
 *       <version>2.0.0</version>
 *   </dependency>
 */
@Configuration
public class CloudinaryConfig {

	@Bean
	public Cloudinary cloudinary(
	        @Value("${cloudinary.cloud-name}") String cloudName,
	        @Value("${cloudinary.api-key}") String apiKey,
	        @Value("${cloudinary.api-secret}") String apiSecret) {

//	    System.out.println("=== CLOUDINARY CONFIG ===");
//	    System.out.println("Cloud name: [" + cloudName + "]");
//	    System.out.println("API key: [" + apiKey + "]");
//	    System.out.println("API key length: " + (apiKey == null ? 0 : apiKey.length()));
//	    System.out.println("Secret present: " + (apiSecret != null && !apiSecret.isBlank()));
//	    System.out.println("=========================");

	    return new Cloudinary(ObjectUtils.asMap(
	            "cloud_name", cloudName,
	            "api_key", apiKey,
	            "api_secret", apiSecret,
	            "secure", true
	    ));
	}
}