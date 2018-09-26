package cm.aptoide.pt.comment;

import cm.aptoide.pt.comment.data.CommentsResponseModel;
import cm.aptoide.pt.comment.mock.FakeCommentsDataSource;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.presenter.View;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CommentsListManagerPresenterTest {

  @Mock private CommentsFragment view;
  @Mock private CommentsListManager commentsListManager;
  @Mock private CrashReport crashReporter;
  @Mock private CommentsNavigator commentsNavigator;

  private CommentsPresenter presenter;
  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private PublishSubject<Void> pullToRefreshEvent;
  private PublishSubject<Long> commentClickEvent;
  private FakeCommentsDataSource fakeCommentsDataSource;

  @Before public void setupCommentsPresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    pullToRefreshEvent = PublishSubject.create();
    commentClickEvent = PublishSubject.create();

    presenter =
        new CommentsPresenter(view, commentsListManager, commentsNavigator, Schedulers.immediate(),
            crashReporter);
    fakeCommentsDataSource = new FakeCommentsDataSource();

    when(view.getLifecycleEvent()).thenReturn(lifecycleEvent);
    when(view.refreshes()).thenReturn(pullToRefreshEvent);
    when(view.commentClick()).thenReturn(commentClickEvent);
  }

  @Test public void showCommentsTest() {
    when(commentsListManager.loadComments()).thenReturn(
        fakeCommentsDataSource.loadComments(15, false));
    //Given an initialized CommentsPresenter
    presenter.showComments();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentsListManager).loadComments();
    //Then the loading should be hidden
    verify(view).hideLoading();
  }

  @Test public void showErrorIfCommentsFail() {
    when(commentsListManager.loadComments()).thenReturn(
        Single.error(new IllegalStateException("test")));
    //Given an initialized CommentsPresenter
    presenter.showComments();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the comments should be requested
    verify(commentsListManager).loadComments();
    //Then the loading should be hidden
    verify(view).showGeneralError();
  }

  @Test public void pullRefreshTest() {
    Single<CommentsResponseModel> value = fakeCommentsDataSource.loadComments(15, false);
    when(commentsListManager.loadFreshComments()).thenReturn(value);
    //Given an initialized CommentsPresenter
    presenter.pullToRefresh();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And the user makes a pull to refresh action
    pullToRefreshEvent.onNext(null);
    //Then the loading should be shown
    verify(view).showLoading();
    //Then the fresh comments should be requests
    verify(commentsListManager).loadFreshComments();
    //Then the comments should be shown
    verify(view).showComments(value.toBlocking()
        .value()
        .getComments());
  }

  @Test public void commentClick() {
    //Given an initialized CommentsPresenter
    presenter.clickComment();
    //When the view is shown to the screen
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //And a comment is clicked
    commentClickEvent.onNext(100L);
    //Then navigation to the comment detail should start
    verify(commentsNavigator).navigateToCommentView(100L);
  }
}