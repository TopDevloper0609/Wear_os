package com.fastaccess.ui.adapter.viewholder;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.fastaccess.R;
import com.fastaccess.data.dao.ReviewModel;
import com.fastaccess.data.dao.TimelineModel;
import com.fastaccess.data.dao.types.ReviewStateType;
import com.fastaccess.helper.InputHelper;
import com.fastaccess.helper.ParseDateFormat;
import com.fastaccess.provider.scheme.LinkParserHelper;
import com.fastaccess.provider.timeline.HtmlHelper;
import com.fastaccess.provider.timeline.handler.drawable.DrawableGetter;
import com.fastaccess.ui.widgets.AvatarLayout;
import com.fastaccess.ui.widgets.FontTextView;
import com.fastaccess.ui.widgets.ForegroundImageView;
import com.fastaccess.ui.widgets.SpannableBuilder;
import com.fastaccess.ui.widgets.recyclerview.BaseRecyclerAdapter;
import com.fastaccess.ui.widgets.recyclerview.BaseViewHolder;

import butterknife.BindView;

/**
 * Created by Kosh on 13 Dec 2016, 1:42 AM
 */

public class ReviewsViewHolder extends BaseViewHolder<TimelineModel> {

    @BindView(R.id.stateImage) ForegroundImageView stateImage;
    @BindView(R.id.avatarLayout) AvatarLayout avatarLayout;
    @BindView(R.id.stateText) FontTextView stateText;
    @BindView(R.id.body) FontTextView body;
    private final ViewGroup viewGroup;

    private ReviewsViewHolder(@NonNull View itemView, @Nullable BaseRecyclerAdapter adapter, @NonNull ViewGroup viewGroup) {
        super(itemView, adapter);
        itemView.setOnLongClickListener(null);
        itemView.setOnClickListener(null);
        this.viewGroup = viewGroup;

    }

    public static ReviewsViewHolder newInstance(ViewGroup viewGroup, BaseRecyclerAdapter adapter) {
        return new ReviewsViewHolder(getView(viewGroup, R.layout.review_timeline_row_item), adapter, viewGroup);
    }

    @Override public void bind(@NonNull TimelineModel model) {
        ReviewModel review = model.getReview();
        if (review != null) {
            if (review.getUser() != null) {
                avatarLayout.setUrl(review.getUser().getAvatarUrl(), review.getUser().getLogin(), false,
                        LinkParserHelper.isEnterprise(review.getUser().getHtmlUrl()));
            } else {
                avatarLayout.setUrl(null, null, false, false);
            }
            ReviewStateType stateType = ReviewStateType.getType(review.getState());
            if (stateType != null) {
                stateImage.setImageResource(stateType.getDrawableRes());
            }
            if (review.getUser() != null && stateType != null) {
                stateText.setText(SpannableBuilder.builder().append(review.getUser().getLogin())
                        .append(" ")
                        .append(stateText.getResources().getString(stateType.getStringRes()))
                        .append(" ")
                        .append(ParseDateFormat.getTimeAgo(review.getSubmittedAt())));
            }
            if (!InputHelper.isEmpty(review.getBodyHtml())) {
                body.setVisibility(View.VISIBLE);
                HtmlHelper.htmlIntoTextView(body, review.getBodyHtml(), viewGroup.getWidth());
            } else {
                body.setVisibility(View.GONE);
            }
        }
    }

    @Override protected void onViewIsDetaching() {
        DrawableGetter drawableGetter = (DrawableGetter) stateText.getTag(R.id.drawable_callback);
        if (drawableGetter != null) {
            drawableGetter.clear(drawableGetter);
        }
    }
}
