package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.StyleSpan;

import java.util.Date;

import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.model.v7.timeline.Article;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.SpannableFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by marcelobenites on 6/17/16.
 */
@AllArgsConstructor
public class ArticleDisplayable extends Displayable {

	@Getter private String articleTitle;
	@Getter private String url;
	@Getter private String title;
	@Getter private String thumbnailUrl;
	@Getter private String avatarUrl;
	@Getter private int getAppId;

	private String getAppName;
	private Date date;
	private DateCalculator dateCalculator;
	private SpannableFactory spannableFactory;

	public static ArticleDisplayable from(Article article, DateCalculator dateCalculator, SpannableFactory
			spannableFactory) {
		return new ArticleDisplayable(article.getTitle(), article.getUrl(), article
				.getPublisher().getName(), article.getThumbnailUrl(), article.getPublisher()
				.getLogoUrl(), 19347406, "Clash of Clans", article.getDate(), dateCalculator, spannableFactory);
	}

	public ArticleDisplayable() {
	}

	public String getHoursSinceLastUpdate(Context context) {
		return context.getString(R.string.fragment_social_timeline_hours_since_last_update, dateCalculator
				.getHoursSinceDate(date));
	}

	public Spannable getAppText(Context context) {
		return spannableFactory.create(context
				.getString(R.string.displayable_social_timeline_article_get_app_button, getAppName), getAppName, new
				StyleSpan(Typeface.BOLD));
	}

	public long getAppId() {
		return getAppId;
	}

	@Override
	public Type getType() {
		return Type.SOCIAL_TIMELINE;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_social_timeline_article;
	}
}
