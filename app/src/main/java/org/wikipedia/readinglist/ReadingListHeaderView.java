package org.wikipedia.readinglist;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.drawee.drawable.ScalingUtils;

import org.wikipedia.R;
import org.wikipedia.util.GradientUtil;
import org.wikipedia.views.FaceAndColorDetectImageView;
import org.wikipedia.views.ViewUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class ReadingListHeaderView extends FrameLayout {
    @BindView(R.id.reading_list_header_image_gradient) View gradientView;
    @BindView(R.id.reading_list_header_empty_image) View emptyView;
    @BindView(R.id.reading_list_header_image_container) View imageContainerView;
    @BindViews({R.id.reading_list_header_image_0,
            R.id.reading_list_header_image_1,
            R.id.reading_list_header_image_2,
            R.id.reading_list_header_image_3,
            R.id.reading_list_header_image_4,
            R.id.reading_list_header_image_5}) List<FaceAndColorDetectImageView> imageViews;

    @Nullable private ReadingList readingList;

    public ReadingListHeaderView(Context context) {
        super(context);
        init();
    }

    public ReadingListHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReadingListHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ReadingListHeaderView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setReadingList(@NonNull ReadingList readingList) {
        this.readingList = readingList;
        if (readingList.getPages().isEmpty()) {
            imageContainerView.setVisibility(GONE);
            emptyView.setVisibility(VISIBLE);
        } else {
            imageContainerView.setVisibility(VISIBLE);
            emptyView.setVisibility(GONE);
            getThumbnails();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_reading_list_header, this);
        ButterKnife.bind(this);

        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        ViewUtil.setBackgroundDrawable(gradientView,
                GradientUtil.getCubicGradient(ContextCompat.getColor(getContext(),
                        R.color.new_lead_gradient_start), Gravity.TOP));

        if (isInEditMode()) {
            return;
        }
        clearThumbnails();
    }

    private void getThumbnails() {
        ReadingListPageDetailFetcher.updateInfo(readingList, new ReadingListPageDetailFetcher.Callback() {
            @Override public void success() {
                if (getWindowToken() == null) {
                    return;
                }
                updateThumbnails();
            }

            @Override public void failure(Throwable e) {
            }
        });
        updateThumbnails();
    }

    private void clearThumbnails() {
        for (FaceAndColorDetectImageView imageView : imageViews) {
            ViewUtil.loadImageUrlInto(imageView, null);
            imageView.getHierarchy().setFailureImage(null);
        }
    }

    private void updateThumbnails() {
        if (readingList == null) {
            return;
        }
        clearThumbnails();
        for (int i = 0; i < readingList.getPages().size() && i < imageViews.size(); ++i) {
            loadThumbnail(imageViews.get(i), readingList.getPages().get(i).thumbnailUrl());
        }
    }

    private void loadThumbnail(@NonNull FaceAndColorDetectImageView view, @Nullable String url) {
        if (TextUtils.isEmpty(url)) {
            view.getHierarchy().setFailureImage(R.drawable.ic_image_gray_24dp,
                    ScalingUtils.ScaleType.CENTER);
        } else {
            view.loadImage(Uri.parse(url));
        }
    }
}
