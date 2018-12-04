package cm.aptoide.pt.promotions;

public class PromotionApp {

  private String name;
  private String packageName;
  private long appId;
  private String downloadPath;
  private String alternativePath;
  private String appIcon;
  private PromotionAppState state;
  private String description;
  private long size;
  private float rating;
  private int numberOfDownloads;

  public PromotionApp(String name, String packageName, long appId, String downloadPath,
      String alternativePath, String appIcon, PromotionAppState state, String description,
      long size, float rating, int numberOfDownloads) {
    this.name = name;
    this.packageName = packageName;
    this.appId = appId;
    this.downloadPath = downloadPath;
    this.alternativePath = alternativePath;
    this.appIcon = appIcon;
    this.state = state;
    this.description = description;
    this.size = size;
    this.rating = rating;
    this.numberOfDownloads = numberOfDownloads;
  }

  public String getName() {
    return name;
  }

  public String getPackageName() {
    return packageName;
  }

  public long getAppId() {
    return appId;
  }

  public String getDownloadPath() {
    return downloadPath;
  }

  public String getAlternativePath() {
    return alternativePath;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public PromotionAppState getState() {
    return state;
  }

  public void setState(PromotionAppState state) {
    this.state = state;
  }

  public String getDescription() {
    return description;
  }

  public long getSize() {
    return size;
  }

  public float getRating() {
    return rating;
  }

  public int getNumberOfDownloads() {
    return numberOfDownloads;
  }

  enum PromotionAppState {
    DOWNLOAD, UPDATE, DOWNLOADING, INSTALL, CLAIM, CLAIMED
  }
}