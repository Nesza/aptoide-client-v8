package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.ListAppCoinsCampaigns;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by filipegoncalves on 4/26/18.
 */

public class GetAppCoinsCampaignsRequest
    extends V7<ListAppCoinsCampaigns, GetAppCoinsCampaignsRequest.Body> {

  public GetAppCoinsCampaignsRequest(Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences) {
    super(body, getHost(sharedPreferences), httpClient, converterFactory, bodyInterceptor,
        tokenInvalidator);
  }

  @Override protected Observable<ListAppCoinsCampaigns> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {

    return interfaces.getAppCoinsAds(body, bypassCache, body.getLimit());
  }

  public static class Body extends BaseBody implements Endless {
    private int offset;
    private int limit;
    private String packageName;
    private Integer vercode;

    public Body(int offset, int limit) {
      this.offset = offset;
      this.limit = limit;
    }

    public Body(String packageName, int vercode) {
      this.packageName = packageName;
      this.vercode = vercode;
      this.limit = 5;
      this.offset = 0;
    }

    public int getOffset() {
      return offset;
    }

    public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return this.limit;
    }

    public String getPackageName() {
      return packageName;
    }

    public Integer getVercode() {
      return vercode;
    }
  }
}
