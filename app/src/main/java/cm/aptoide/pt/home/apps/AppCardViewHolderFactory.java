package cm.aptoide.pt.home.apps;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import rx.subjects.PublishSubject;

import static cm.aptoide.pt.home.apps.AppsAdapter.COMPLETED_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.ERROR_DOWNLOAD;
import static cm.aptoide.pt.home.apps.AppsAdapter.HEADER;
import static cm.aptoide.pt.home.apps.AppsAdapter.INSTALLED;
import static cm.aptoide.pt.home.apps.AppsAdapter.UPDATE;

/**
 * Created by filipegoncalves on 3/12/18.
 */

public class AppCardViewHolderFactory {

  private PublishSubject<App> pauseDownload;

  public AppCardViewHolderFactory(PublishSubject<App> pauseDownload) {
    this.pauseDownload = pauseDownload;
  }

  public AppsViewHolder createViewHolder(int viewType, ViewGroup parent) {
    AppsViewHolder appViewHolder;
    switch (viewType) {
      case HEADER:
        appViewHolder = new HeaderViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case AppsAdapter.ACTIVE_DOWNLOAD:
        appViewHolder = new ActiveAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_active_download_app_item, parent, false), pauseDownload);
        break;
      case AppsAdapter.STANDBY_DOWNLOAD:
        appViewHolder = new StandByAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case COMPLETED_DOWNLOAD:
        appViewHolder = new CompletedAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case ERROR_DOWNLOAD:
        appViewHolder = new ErrorAppDownloadViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case UPDATE:
        appViewHolder = new UpdateAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      case INSTALLED:
        appViewHolder = new InstalledAppViewHolder(LayoutInflater.from(parent.getContext())
            .inflate(R.layout.apps_header_item, parent, false));
        break;
      default:
        throw new IllegalStateException("Wrong cardType" + viewType);
    }

    return appViewHolder;
  }
}