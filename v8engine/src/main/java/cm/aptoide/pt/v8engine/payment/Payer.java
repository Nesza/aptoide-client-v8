/*
 * Copyright (c) 2017.
 * Modified by Marcelo Benites on 06/01/2017.
 */

package cm.aptoide.pt.v8engine.payment;

import android.content.Context;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.navigation.AccountNavigator;

/**
 * Created by marcelobenites on 06/01/17.
 */
public class Payer {

  private final AptoideAccountManager accountManager;

  public Payer(AptoideAccountManager accountManager) {
    this.accountManager = accountManager;
  }

  public String getId() {
    return accountManager.getUserEmail();
  }
}
