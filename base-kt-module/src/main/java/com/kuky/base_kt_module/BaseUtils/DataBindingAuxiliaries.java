package com.kuky.base_kt_module.BaseUtils;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;

/**
 * @author Kuky
 */
public class DataBindingAuxiliaries {

    @BindingAdapter({"image_url", "place_image", "error_image"})
    public static void imageLoader(ImageView imageView, Bitmap bitmap, Drawable place, Drawable error) {
        Glide.with(imageView.getContext())
                .load(bitmap)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place)
                        .error(error))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image", "error_image"})
    public static void imageLoader(ImageView imageView, Drawable drawable, Drawable place, Drawable error) {
        Glide.with(imageView.getContext())
                .load(drawable)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place)
                        .error(error))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image", "error_image"})
    public static void imageLoader(ImageView imageView, String url, Drawable place, Drawable error) {
        Glide.with(imageView.getContext())
                .load(url)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place)
                        .error(error))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image", "error_image"})
    public static void imageLoader(ImageView imageView, Uri imageUri, Drawable place, Drawable error) {
        Glide.with(imageView.getContext())
                .load(imageUri)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place)
                        .error(error))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image", "error_image"})
    public static void imageLoader(ImageView imageView, File imageFile, Drawable place, Drawable error) {
        Glide.with(imageView.getContext())
                .load(imageFile)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place)
                        .error(error))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image", "error_image"})
    public static void imageLoader(ImageView imageView, int drawableRes, Drawable place, Drawable error) {
        Glide.with(imageView.getContext())
                .load(drawableRes)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place)
                        .error(error))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image"})
    public static void imageLoader(ImageView imageView, Bitmap bitmap, Drawable place) {
        Glide.with(imageView.getContext())
                .load(bitmap)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image"})
    public static void imageLoader(ImageView imageView, Drawable drawable, Drawable place) {
        Glide.with(imageView.getContext())
                .load(drawable)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image"})
    public static void imageLoader(ImageView imageView, String url, Drawable place) {
        Glide.with(imageView.getContext())
                .load(url)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image"})
    public static void imageLoader(ImageView imageView, Uri imageUri, Drawable place) {
        Glide.with(imageView.getContext())
                .load(imageUri)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image"})
    public static void imageLoader(ImageView imageView, File imageFile, Drawable place) {
        Glide.with(imageView.getContext())
                .load(imageFile)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place))
                .into(imageView);
    }

    @BindingAdapter({"image_url", "place_image"})
    public static void imageLoader(ImageView imageView, int drawableRes, Drawable place) {
        Glide.with(imageView.getContext())
                .load(drawableRes)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(place))
                .into(imageView);
    }

    @BindingAdapter("image_url")
    public static void imageLoader(ImageView imageView, Bitmap bitmap) {
        Glide.with(imageView.getContext())
                .load(bitmap)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }

    @BindingAdapter("image_url")
    public static void imageLoader(ImageView imageView, Drawable drawable) {
        Glide.with(imageView.getContext())
                .load(drawable)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }

    @BindingAdapter("image_url")
    public static void imageLoader(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }

    @BindingAdapter("image_url")
    public static void imageLoader(ImageView imageView, Uri imageUri) {
        Glide.with(imageView.getContext())
                .load(imageUri)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }

    @BindingAdapter("image_url")
    public static void imageLoader(ImageView imageView, File imageFile) {
        Glide.with(imageView.getContext())
                .load(imageFile)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }

    @BindingAdapter("image_url")
    public static void imageLoader(ImageView imageView, int drawableRes) {
        Glide.with(imageView.getContext())
                .load(drawableRes)
                .thumbnail(0.7f)
                .apply(new RequestOptions()
                        .centerCrop())
                .into(imageView);
    }
}
