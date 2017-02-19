package cm.aptoide.pt.shareapppsandroid;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public class ActivityView extends AppCompatActivity {

  private Presenter presenter;

  protected void attachPresenter(Presenter presenter) {
    this.presenter = presenter;
    presenter.onCreate();
  }

  @Override protected void onResume() {
    super.onResume();
    presenter.onResume();
  }

  @Override protected void onPause() {
    super.onPause();
    presenter.onPause();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    presenter.onDestroy();
  }
}
