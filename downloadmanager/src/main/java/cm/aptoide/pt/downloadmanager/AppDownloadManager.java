package cm.aptoide.pt.downloadmanager;

import android.support.annotation.VisibleForTesting;
import android.util.Log;
import cm.aptoide.pt.logger.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import rx.Completable;
import rx.Observable;
import rx.Subscription;
import rx.subjects.PublishSubject;

/**
 * Created by filipegoncalves on 7/27/18.
 */

public class AppDownloadManager implements AppDownloader {

  private static final String TAG = "AppDownloadManager";
  private final DownloadApp app;
  private RetryFileDownloaderProvider fileDownloaderProvider;
  private HashMap<String, RetryFileDownloader> fileDownloaderPersistence;
  private PublishSubject<FileDownloadCallback> fileDownloadSubject;
  private AppDownloadStatus appDownloadStatus;
  private Subscription subscribe;

  public AppDownloadManager(RetryFileDownloaderProvider fileDownloaderProvider, DownloadApp app,
      HashMap<String, RetryFileDownloader> fileDownloaderPersistence) {
    this.fileDownloaderProvider = fileDownloaderProvider;
    this.app = app;
    this.fileDownloaderPersistence = fileDownloaderPersistence;
    fileDownloadSubject = PublishSubject.create();
    appDownloadStatus = new AppDownloadStatus(app.getMd5(), new ArrayList<>(),
        AppDownloadStatus.AppDownloadState.PENDING);
  }

  @Override public void startAppDownload() {
    subscribe = Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> startFileDownload(downloadAppFile))
        .subscribe(__ -> {
          Log.d(TAG, "startAppDownload: completed");
        }, Throwable::printStackTrace);
  }

  @Override public Completable pauseAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .flatMapCompletable(fileDownloader -> fileDownloader.pauseDownload())
        .toCompletable();
  }

  @Override public Completable removeAppDownload() {
    return Observable.from(app.getDownloadFiles())
        .flatMap(downloadAppFile -> getFileDownloader(downloadAppFile.getMainDownloadPath()))
        .flatMapCompletable(fileDownloader -> fileDownloader.removeDownloadFile())
        .toCompletable();
  }

  @Override public Observable<AppDownloadStatus> observeDownloadProgress() {
    return observeFileDownload().flatMap(fileDownloadCallback -> {
      setAppDownloadStatus(fileDownloadCallback);
      return Observable.just(appDownloadStatus);
    })
        .doOnError(throwable -> throwable.printStackTrace())
        .map(__ -> appDownloadStatus);
  }

  public void stop() {
    if (!subscribe.isUnsubscribed()) {
      subscribe.unsubscribe();
    }
  }

  private Observable<FileDownloadCallback> startFileDownload(DownloadAppFile downloadAppFile) {
    return Observable.just(
        fileDownloaderProvider.createRetryFileDownloader(downloadAppFile.getDownloadMd5(),
            downloadAppFile.getMainDownloadPath(), downloadAppFile.getFileType(),
            downloadAppFile.getPackageName(), downloadAppFile.getVersionCode(),
            downloadAppFile.getFileName(), PublishSubject.create(),
            downloadAppFile.getAlternativeDownloadPath()))
        .doOnNext(
            fileDownloader -> fileDownloaderPersistence.put(downloadAppFile.getMainDownloadPath(),
                fileDownloader))
        .doOnNext(__ -> Logger.getInstance()
            .d("AppDownloader", "Starting app file download " + downloadAppFile.getFileName()))
        .doOnNext(fileDownloader -> fileDownloader.startFileDownload())
        .flatMap(fileDownloader -> handleFileDownloadProgress(fileDownloader))
        .doOnError(Throwable::printStackTrace);
  }

  private Observable<FileDownloadCallback> observeFileDownload() {
    return fileDownloadSubject;
  }

  private void setAppDownloadStatus(FileDownloadCallback fileDownloadCallback) {
    appDownloadStatus.setFileDownloadCallback(fileDownloadCallback);
  }

  private Observable<FileDownloadCallback> handleFileDownloadProgress(
      RetryFileDownloader fileDownloader) {
    return Completable.fromAction(() -> Log.d(TAG, "handleFileDownloadProgress: started"))
        .andThen(fileDownloader.observeFileDownloadProgress())
        .doOnNext(fileDownloadCallback -> Log.d(TAG, "handleFileDownloadProgress: cenas"))
        .doOnNext(fileDownloadCallback -> fileDownloadSubject.onNext(fileDownloadCallback));
  }

  @VisibleForTesting
  public Observable<RetryFileDownloader> getFileDownloader(String mainDownloadPath) {
    return Observable.just(fileDownloaderPersistence.get(mainDownloadPath));
  }
}
