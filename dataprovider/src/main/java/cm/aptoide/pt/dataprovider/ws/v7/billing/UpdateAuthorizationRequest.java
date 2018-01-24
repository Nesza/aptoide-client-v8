package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class UpdateAuthorizationRequest
    extends V7<Response<UpdateAuthorizationRequest.ResponseBody>, UpdateAuthorizationRequest.RequestBody> {

  private UpdateAuthorizationRequest(RequestBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static UpdateAuthorizationRequest ofPayPal(long transactionId, String payKey,
      SharedPreferences sharedPreferences, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptorV7,
      TokenInvalidator tokenInvalidator) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setTransactionId(transactionId);
    final RequestBody.Data data = new RequestBody.Data();
    data.setPayKey(payKey);
    requestBody.setServiceData(data);
    return new UpdateAuthorizationRequest(requestBody, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator);
  }

  public static UpdateAuthorizationRequest ofAdyen(long authorizationId, String payload,
      SharedPreferences sharedPreferences, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor<BaseBody> bodyInterceptorV7,
      TokenInvalidator tokenInvalidator) {
    final RequestBody requestBody = new RequestBody();
    requestBody.setAuthorizationId(authorizationId);
    final RequestBody.Data data = new RequestBody.Data();
    data.setPayKey(payload);
    requestBody.setServiceData(data);
    return new UpdateAuthorizationRequest(requestBody, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptorV7, tokenInvalidator);
  }

  @Override protected Observable<Response<ResponseBody>> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.updateBillingAuthorization(body, bypassCache);
  }

  public static class RequestBody extends BaseBody {

    private Long transactionId;
    private Long authorizationId;
    private Data serviceData;

    public Data getServiceData() {
      return serviceData;
    }

    public void setServiceData(Data serviceData) {
      this.serviceData = serviceData;
    }

    public Long getTransactionId() {
      return transactionId;
    }

    public void setTransactionId(Long transactionId) {
      this.transactionId = transactionId;
    }

    public Long getAuthorizationId() {
      return authorizationId;
    }

    public void setAuthorizationId(Long authorizationId) {
      this.authorizationId = authorizationId;
    }

    public static class Data {
      @JsonProperty("paykey") private String payKey;

      public String getPayKey() {
        return payKey;
      }

      public void setPayKey(String payKey) {
        this.payKey = payKey;
      }

    }
  }

  public static class ResponseBody extends BaseV7Response {

    private GetAuthorizationRequest.ResponseBody.Authorization data;

    public GetAuthorizationRequest.ResponseBody.Authorization getData() {
      return data;
    }

    public void setData(GetAuthorizationRequest.ResponseBody.Authorization data) {
      this.data = data;
    }
  }
}
