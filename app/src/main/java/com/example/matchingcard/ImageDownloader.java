package com.example.matchingcard;

import android.graphics.Bitmap;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageDownloader {
	public boolean downloadImage(String imageURL, File destFile) {
		try {
			Bitmap bitmap = Picasso.get().load(imageURL).get();
			try (FileOutputStream fos = new FileOutputStream(destFile)) {
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
