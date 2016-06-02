/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 26/05/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.appView;

import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.Type;
import cm.aptoide.pt.v8engine.R;

/**
 * Created by sithengineer on 04/05/16.
 */
public class AppViewCommentsDisplayable extends AppViewDisplayable {

	public AppViewCommentsDisplayable() {
	}

	public AppViewCommentsDisplayable(GetApp getApp) {
		super(getApp);
	}

	public AppViewCommentsDisplayable(GetApp getApp, boolean fixedPerLineCount) {
		super(getApp, fixedPerLineCount);
	}

	@Override
	public Type getType() {
		return Type.APP_VIEW_COMMENTS;
	}

	@Override
	public int getViewLayout() {
		return R.layout.displayable_app_view_comments;
	}
}
