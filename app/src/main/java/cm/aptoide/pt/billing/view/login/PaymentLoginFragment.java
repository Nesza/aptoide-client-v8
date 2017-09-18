package cm.aptoide.pt.billing.view.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.accountmanager.AptoideCredentials;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountErrorMapper;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.account.view.GooglePlayServicesFragment;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.view.navigator.ActivityResultNavigator;
import cm.aptoide.pt.view.navigator.FragmentNavigator;
import cm.aptoide.pt.view.rx.RxAlertDialog;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import com.trello.rxlifecycle.android.FragmentEvent;
import java.util.Arrays;
import rx.Observable;

public class PaymentLoginFragment extends GooglePlayServicesFragment implements PaymentLoginView {

  private static final String EXTRA_USERNAME_PASSWORD_CONTAINER_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.USERNAME_PASSWORD_CONTAINER_VISIBLE";
  private static final String EXTRA_LOGIN_VISIBLE =
      "cm.aptoide.pt.billing.view.login.extra.LOGIN_VISIBLE ";
  private int requestCode;
  private ClickHandler handler;
  private PublishRelay<Void> backButtonRelay;
  private PublishRelay<Void> upNavigationRelay;
  private Button facebookButton;
  private Button googleButton;
  private ProgressDialog progressDialog;
  private AccountNavigator accountNavigator;
  private AptoideAccountManager accountManager;
  private CrashReport crashReport;
  private RxAlertDialog facebookEmailRequiredDialog;
  private View rootView;
  private AccountErrorMapper errorMapper;

  private View aptoideLoginSignUpSeparator;
  private View aptoideLoginSignUpButtonContainer;
  private Button aptoideJoinToggle;
  private Button aptoideLoginToggle;
  private View usernamePasswordContainer;
  private View aptoideSignUpContainer;
  private View aptoideLoginContainer;
  private boolean usernamePasswordContainerVisible;
  private boolean loginVisible;
  private View recoverPasswordButton;
  private Button aptoideLoginButton;
  private Button aptoideSignUpButton;
  private EditText usernameEditText;
  private EditText passwordEditText;

  public static Fragment newInstance() {
    return new PaymentLoginFragment();
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestCode = getArguments().getInt(FragmentNavigator.REQUEST_CODE_EXTRA);
    backButtonRelay = PublishRelay.create();
    upNavigationRelay = PublishRelay.create();
    accountNavigator = ((ActivityResultNavigator) getContext()).getAccountNavigator();
    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    crashReport = CrashReport.getInstance();
    errorMapper = new AccountErrorMapper(getContext());
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_payment_login, container, false);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setHasOptionsMenu(true);

    rootView = getActivity().findViewById(android.R.id.content);

    final Toolbar toolbar = (Toolbar) view.findViewById(R.id.fragment_payment_login_toolbar);
    ((AppCompatActivity) getContext()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getContext()).getSupportActionBar()
        .setDisplayHomeAsUpEnabled(true);

    aptoideLoginSignUpSeparator =
        view.findViewById(R.id.fragment_payment_login_aptoide_buttons_separator_container);

    aptoideLoginSignUpButtonContainer =
        view.findViewById(R.id.fragment_payment_login_aptoide_buttons_container);

    aptoideSignUpContainer = view.findViewById(R.id.fragment_payment_sign_up_container);
    aptoideLoginContainer = view.findViewById(R.id.fragment_payment_login_container);

    aptoideJoinToggle = (Button) view.findViewById(R.id.fragment_payment_login_join_button);
    aptoideJoinToggle.setText(getString(R.string.join_company,
        ((AptoideApplication) getContext().getApplicationContext()).getMarketName()));
    aptoideLoginToggle = (Button) view.findViewById(R.id.fragment_payment_login_small_button);
    recoverPasswordButton = view.findViewById(R.id.fragment_payment_login_recover_password_button);
    aptoideLoginButton = (Button) view.findViewById(R.id.fragment_payment_login_large_login_button);
    aptoideSignUpButton = (Button) view.findViewById(R.id.fragment_payment_login_sign_up_button);
    usernameEditText = (EditText) view.findViewById(R.id.fragment_payment_login_username);
    passwordEditText = (EditText) view.findViewById(R.id.fragment_payment_login_password);

    usernamePasswordContainer =
        view.findViewById(R.id.fragment_payment_login_username_password_container);

    if (savedInstanceState != null) {
      if (savedInstanceState.getBoolean(EXTRA_USERNAME_PASSWORD_CONTAINER_VISIBLE)) {
        showUsernamePasswordContainer(savedInstanceState.getBoolean(EXTRA_LOGIN_VISIBLE));
      } else {
        hideUsernamePasswordContainer();
      }
    }

    RxView.clicks(aptoideJoinToggle)
        .doOnNext(__ -> showUsernamePasswordContainer(false))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    RxView.clicks(aptoideLoginToggle)
        .doOnNext(__ -> showUsernamePasswordContainer(true))
        .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
        .subscribe();

    facebookEmailRequiredDialog = new RxAlertDialog.Builder(getContext()).setMessage(
        R.string.facebook_email_permission_regected_message)
        .setPositiveButton(R.string.facebook_grant_permission_button)
        .setNegativeButton(android.R.string.cancel)
        .build();

    facebookButton = (Button) view.findViewById(R.id.fragment_payment_login_facebook_button);
    googleButton = (Button) view.findViewById(R.id.fragment_payment_login_google_button);
    progressDialog = new ProgressDialog(getContext());
    progressDialog.setMessage(getString(cm.aptoide.pt.utils.R.string.please_wait));
    progressDialog.setCancelable(false);

    handler = () -> {
      backButtonRelay.call(null);
      return true;
    };
    registerClickHandler(handler);

    attachPresenter(
        new PaymentLoginPresenter(this, requestCode, Arrays.asList("email", "user_friends"),
            accountNavigator, accountManager, crashReport, errorMapper), savedInstanceState);
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putBoolean(EXTRA_USERNAME_PASSWORD_CONTAINER_VISIBLE,
        usernamePasswordContainerVisible);
    outState.putBoolean(EXTRA_LOGIN_VISIBLE, loginVisible);
    super.onSaveInstanceState(outState);
  }

  @Override public Observable<Void> backButtonEvent() {
    return backButtonRelay.filter(__ -> {
      if (usernamePasswordContainer.getVisibility() == View.VISIBLE) {
        hideUsernamePasswordContainer();
        return false;
      }
      return true;
    });
  }

  @Override public Observable<Void> upNavigationEvent() {
    return upNavigationRelay;
  }

  @Override public Observable<Void> facebookSignUpEvent() {
    return RxView.clicks(facebookButton);
  }

  @Override public Observable<Void> googleSignUpEvent() {
    return RxView.clicks(googleButton);
  }

  @Override public void showLoading() {
    progressDialog.show();
  }

  @Override public void hideLoading() {
    progressDialog.dismiss();
  }

  @Override public void showError(String message) {
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        .show();
  }

  @Override public void showFacebookPermissionsRequiredError(Throwable throwable) {
    if (!facebookEmailRequiredDialog.isShowing()) {
      facebookEmailRequiredDialog.show();
    }
  }

  @Override public Observable<Void> recoverPasswordEvent() {
    return RxView.clicks(recoverPasswordButton);
  }

  @Override public Observable<AptoideCredentials> aptoideLoginEvent() {
    return RxView.clicks(aptoideLoginButton)
        .map(__ -> new AptoideCredentials(usernameEditText.getText()
            .toString(), passwordEditText.getText()
            .toString()));
  }

  @Override public Observable<AptoideCredentials> aptoideSignUpEvent() {
    return RxView.clicks(aptoideSignUpButton)
        .map(__ -> new AptoideCredentials(usernameEditText.getText()
            .toString(), passwordEditText.getText()
            .toString()));
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    if (item.getItemId() == android.R.id.home) {
      upNavigationRelay.call(null);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onDestroyView() {
    unregisterClickHandler(handler);
    facebookEmailRequiredDialog.dismiss();
    facebookEmailRequiredDialog = null;
    facebookButton = null;
    googleButton = null;
    progressDialog.dismiss();
    progressDialog = null;
    rootView = null;
    aptoideLoginSignUpSeparator = null;
    aptoideLoginSignUpButtonContainer = null;
    aptoideSignUpContainer = null;
    aptoideLoginContainer = null;
    aptoideJoinToggle = null;
    aptoideLoginToggle = null;
    usernamePasswordContainer = null;
    recoverPasswordButton = null;
    aptoideLoginButton = null;
    aptoideSignUpButton = null;
    usernameEditText = null;
    passwordEditText = null;
    super.onDestroyView();
  }

  private void showUsernamePasswordContainer(boolean showLogin) {
    aptoideLoginSignUpSeparator.setVisibility(View.GONE);
    aptoideLoginSignUpButtonContainer.setVisibility(View.GONE);
    usernamePasswordContainer.setVisibility(View.VISIBLE);
    usernamePasswordContainerVisible = true;

    if (showLogin) {
      loginVisible = true;
      aptoideLoginContainer.setVisibility(View.VISIBLE);
      aptoideSignUpContainer.setVisibility(View.GONE);
    } else {
      loginVisible = false;
      aptoideLoginContainer.setVisibility(View.GONE);
      aptoideSignUpContainer.setVisibility(View.VISIBLE);
    }
  }

  private void hideUsernamePasswordContainer() {
    aptoideLoginSignUpSeparator.setVisibility(View.VISIBLE);
    aptoideLoginSignUpButtonContainer.setVisibility(View.VISIBLE);
    usernamePasswordContainer.setVisibility(View.GONE);
    aptoideLoginContainer.setVisibility(View.GONE);
    aptoideSignUpContainer.setVisibility(View.GONE);
    usernamePasswordContainerVisible = false;
  }
}
