package cm.aptoide.pt.v8engine.social.data;

import java.util.List;
import rx.Single;

/**
 * Created by jdandrade on 31/05/2017.
 */

public class SocialManager {
  private final SocialService service;

  public SocialManager(SocialService service) {
    this.service = service;
  }

  public Single<List<Article>> getCards() {
    return service.getCards();
  }

  public Single<List<Article>> getNextCards() {
    return service.getNextCards();
  }
}
