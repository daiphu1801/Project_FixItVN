package com.fixit;

import android.app.Activity;
import android.app.Service;
import android.content.SharedPreferences;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import com.fixit.core.di.AppModule;
import com.fixit.core.di.AppModule_ProvideSessionStorageFactory;
import com.fixit.core.di.AppModule_ProvideSharedPreferencesFactory;
import com.fixit.core.network.AuthInterceptor;
import com.fixit.core.network.NetworkModule;
import com.fixit.core.network.NetworkModule_ProvideLoggingInterceptorFactory;
import com.fixit.core.network.NetworkModule_ProvideOkHttpClientFactory;
import com.fixit.core.network.NetworkModule_ProvideRetrofitFactory;
import com.fixit.core.storage.SessionStorage;
import com.fixit.core.storage.SharedPrefsSessionStorage;
import com.fixit.feature.auth.data.remote.api.AuthApi;
import com.fixit.feature.auth.data.repository.AuthRepositoryImpl;
import com.fixit.feature.auth.di.AuthModule;
import com.fixit.feature.auth.di.AuthModule_ProvideAuthApiFactory;
import com.fixit.feature.auth.di.AuthModule_ProvideAuthRepositoryFactory;
import com.fixit.feature.auth.domain.repository.AuthRepository;
import com.fixit.feature.auth.domain.usecase.LoginUseCase;
import com.fixit.feature.auth.domain.usecase.LogoutUseCase;
import com.fixit.feature.auth.domain.usecase.RegisterUseCase;
import com.fixit.feature.auth.presentation.AuthActivity;
import com.fixit.feature.auth.presentation.AuthViewModel;
import com.fixit.feature.auth.presentation.AuthViewModel_HiltModules;
import com.fixit.feature.auth.presentation.ForgotPasswordFragment;
import com.fixit.feature.auth.presentation.LoginFragment;
import com.fixit.feature.auth.presentation.RegisterFragment;
import com.fixit.feature.customer.booking.presentation.CustomerBookingFragment;
import com.fixit.feature.customer.booking.presentation.CustomerFindingWorkerFragment;
import com.fixit.feature.customer.booking.presentation.CustomerLocationPickerFragment;
import com.fixit.feature.customer.booking.presentation.NoteInputFragment;
import com.fixit.feature.customer.favorite.presentation.FavoriteWorkersFragment;
import com.fixit.feature.customer.history.presentation.OrderDetailFinishedFragment;
import com.fixit.feature.customer.history.presentation.OrderHistoryFragment;
import com.fixit.feature.customer.home.presentation.CustomerHomeFragment;
import com.fixit.feature.customer.order.presentation.CustomerCancelOrderFragment;
import com.fixit.feature.customer.order.presentation.CustomerOrderContainerFragment;
import com.fixit.feature.customer.order.presentation.CustomerOrderDetailFragment;
import com.fixit.feature.customer.presentation.CustomerActivity;
import com.fixit.feature.customer.profile.presentation.CustomerAccountInfoFragment;
import com.fixit.feature.customer.profile.presentation.ProfileCustomerFragment;
import com.fixit.feature.customer.search.presentation.CustomerSearchFragment;
import com.fixit.feature.worker.availability.data.repository.WorkerAvailabilityRepositoryImpl;
import com.fixit.feature.worker.availability.domain.usecase.GetWorkerAvailabilityUseCase;
import com.fixit.feature.worker.availability.domain.usecase.ToggleWorkerAvailabilityUseCase;
import com.fixit.feature.worker.chat.presentation.WorkerChatFragment;
import com.fixit.feature.worker.home.data.repository.WorkerHomeRepositoryImpl;
import com.fixit.feature.worker.home.domain.usecase.GetTodayAppointmentsUseCase;
import com.fixit.feature.worker.home.presentation.WorkerHomeFragment;
import com.fixit.feature.worker.home.presentation.WorkerHomeViewModel;
import com.fixit.feature.worker.home.presentation.WorkerHomeViewModel_HiltModules;
import com.fixit.feature.worker.job.data.repository.WorkerJobRepositoryImpl;
import com.fixit.feature.worker.job.domain.usecase.GetWorkerJobSummaryUseCase;
import com.fixit.feature.worker.job.presentation.WorkerJobFragment;
import com.fixit.feature.worker.job.presentation.WorkerJobViewModel;
import com.fixit.feature.worker.job.presentation.WorkerJobViewModel_HiltModules;
import com.fixit.feature.worker.orders.data.repository.WorkerOrdersRepositoryImpl;
import com.fixit.feature.worker.orders.domain.usecase.AdvanceJobStatusUseCase;
import com.fixit.feature.worker.orders.domain.usecase.CalculateTotalExtraUseCase;
import com.fixit.feature.worker.orders.domain.usecase.FilterWorkerOrdersUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GenerateWorkerPaymentQrUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetExtraCostsUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetInitialJobStatusUseCase;
import com.fixit.feature.worker.orders.domain.usecase.GetWorkerOrderByIdUseCase;
import com.fixit.feature.worker.orders.domain.usecase.SaveExtraCostsUseCase;
import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel;
import com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel_HiltModules;
import com.fixit.feature.worker.orders.presentation.complaint.WorkerComplaintFragment;
import com.fixit.feature.worker.orders.presentation.detail.WorkerOrderDetailFragment;
import com.fixit.feature.worker.orders.presentation.extra_cost.WorkerExtraCostFragment;
import com.fixit.feature.worker.orders.presentation.invoice.WorkerInvoiceFragment;
import com.fixit.feature.worker.orders.presentation.list.WorkerOrdersFragment;
import com.fixit.feature.worker.orders.presentation.questionnaire.WorkerQuestionnaireFragment;
import com.fixit.feature.worker.presentation.WorkerActivity;
import com.fixit.feature.worker.presentation.WorkerStatusViewModel;
import com.fixit.feature.worker.presentation.WorkerStatusViewModel_HiltModules;
import com.fixit.feature.worker.profile.presentation.ChangePasswordFragment;
import com.fixit.feature.worker.profile.presentation.WorkerEditSpecializationFragment;
import com.fixit.feature.worker.profile.presentation.WorkerProfileFragment;
import com.fixit.feature.worker.profile.presentation.WorkerProfileViewModel;
import com.fixit.feature.worker.profile.presentation.WorkerProfileViewModel_HiltModules;
import com.fixit.feature.worker.stats.presentation.WorkerStatsFragment;
import com.fixit.feature.worker.wallet.data.repository.WorkerWalletRepositoryImpl;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletBalanceUseCase;
import com.fixit.feature.worker.wallet.domain.usecase.GetWalletTransactionsUseCase;
import com.fixit.feature.worker.wallet.presentation.WorkerWalletFragment;
import com.fixit.feature.worker.wallet.presentation.WorkerWalletViewModel;
import com.fixit.feature.worker.wallet.presentation.WorkerWalletViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class DaggerFixitApp_HiltComponents_SingletonC {
  private DaggerFixitApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private AppModule appModule;

    private ApplicationContextModule applicationContextModule;

    private AuthModule authModule;

    private NetworkModule networkModule;

    private Builder() {
    }

    public Builder appModule(AppModule appModule) {
      this.appModule = Preconditions.checkNotNull(appModule);
      return this;
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public Builder authModule(AuthModule authModule) {
      this.authModule = Preconditions.checkNotNull(authModule);
      return this;
    }

    public Builder networkModule(NetworkModule networkModule) {
      this.networkModule = Preconditions.checkNotNull(networkModule);
      return this;
    }

    public FixitApp_HiltComponents.SingletonC build() {
      if (appModule == null) {
        this.appModule = new AppModule();
      }
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      if (authModule == null) {
        this.authModule = new AuthModule();
      }
      if (networkModule == null) {
        this.networkModule = new NetworkModule();
      }
      return new SingletonCImpl(appModule, applicationContextModule, authModule, networkModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements FixitApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements FixitApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements FixitApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements FixitApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements FixitApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements FixitApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements FixitApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public FixitApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends FixitApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends FixitApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public void injectForgotPasswordFragment(ForgotPasswordFragment forgotPasswordFragment) {
    }

    @Override
    public void injectLoginFragment(LoginFragment loginFragment) {
    }

    @Override
    public void injectRegisterFragment(RegisterFragment registerFragment) {
    }

    @Override
    public void injectCustomerBookingFragment(CustomerBookingFragment customerBookingFragment) {
    }

    @Override
    public void injectCustomerFindingWorkerFragment(
        CustomerFindingWorkerFragment customerFindingWorkerFragment) {
    }

    @Override
    public void injectCustomerLocationPickerFragment(
        CustomerLocationPickerFragment customerLocationPickerFragment) {
    }

    @Override
    public void injectNoteInputFragment(NoteInputFragment noteInputFragment) {
    }

    @Override
    public void injectFavoriteWorkersFragment(FavoriteWorkersFragment favoriteWorkersFragment) {
    }

    @Override
    public void injectOrderDetailFinishedFragment(
        OrderDetailFinishedFragment orderDetailFinishedFragment) {
    }

    @Override
    public void injectOrderHistoryFragment(OrderHistoryFragment orderHistoryFragment) {
    }

    @Override
    public void injectCustomerHomeFragment(CustomerHomeFragment customerHomeFragment) {
    }

    @Override
    public void injectCustomerCancelOrderFragment(
        CustomerCancelOrderFragment customerCancelOrderFragment) {
    }

    @Override
    public void injectCustomerOrderContainerFragment(
        CustomerOrderContainerFragment customerOrderContainerFragment) {
    }

    @Override
    public void injectCustomerOrderDetailFragment(
        CustomerOrderDetailFragment customerOrderDetailFragment) {
    }

    @Override
    public void injectCustomerAccountInfoFragment(
        CustomerAccountInfoFragment customerAccountInfoFragment) {
    }

    @Override
    public void injectProfileCustomerFragment(ProfileCustomerFragment profileCustomerFragment) {
    }

    @Override
    public void injectCustomerSearchFragment(CustomerSearchFragment customerSearchFragment) {
    }

    @Override
    public void injectWorkerChatFragment(WorkerChatFragment workerChatFragment) {
    }

    @Override
    public void injectWorkerHomeFragment(WorkerHomeFragment workerHomeFragment) {
    }

    @Override
    public void injectWorkerJobFragment(WorkerJobFragment workerJobFragment) {
    }

    @Override
    public void injectWorkerComplaintFragment(WorkerComplaintFragment workerComplaintFragment) {
    }

    @Override
    public void injectWorkerOrderDetailFragment(
        WorkerOrderDetailFragment workerOrderDetailFragment) {
    }

    @Override
    public void injectWorkerExtraCostFragment(WorkerExtraCostFragment workerExtraCostFragment) {
    }

    @Override
    public void injectWorkerInvoiceFragment(WorkerInvoiceFragment workerInvoiceFragment) {
    }

    @Override
    public void injectWorkerOrdersFragment(WorkerOrdersFragment workerOrdersFragment) {
    }

    @Override
    public void injectWorkerQuestionnaireFragment(
        WorkerQuestionnaireFragment workerQuestionnaireFragment) {
    }

    @Override
    public void injectChangePasswordFragment(ChangePasswordFragment changePasswordFragment) {
    }

    @Override
    public void injectWorkerEditSpecializationFragment(
        WorkerEditSpecializationFragment workerEditSpecializationFragment) {
    }

    @Override
    public void injectWorkerProfileFragment(WorkerProfileFragment workerProfileFragment) {
    }

    @Override
    public void injectWorkerStatsFragment(WorkerStatsFragment workerStatsFragment) {
    }

    @Override
    public void injectWorkerWalletFragment(WorkerWalletFragment workerWalletFragment) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends FixitApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends FixitApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectAuthActivity(AuthActivity authActivity) {
    }

    @Override
    public void injectCustomerActivity(CustomerActivity customerActivity) {
    }

    @Override
    public void injectWorkerActivity(WorkerActivity workerActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(7).put(LazyClassKeyProvider.com_fixit_feature_auth_presentation_AuthViewModel, AuthViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fixit_feature_worker_home_presentation_WorkerHomeViewModel, WorkerHomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fixit_feature_worker_job_presentation_WorkerJobViewModel, WorkerJobViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fixit_feature_worker_orders_presentation_WorkerOrdersViewModel, WorkerOrdersViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fixit_feature_worker_profile_presentation_WorkerProfileViewModel, WorkerProfileViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fixit_feature_worker_presentation_WorkerStatusViewModel, WorkerStatusViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.com_fixit_feature_worker_wallet_presentation_WorkerWalletViewModel, WorkerWalletViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_fixit_feature_worker_orders_presentation_WorkerOrdersViewModel = "com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel";

      static String com_fixit_feature_worker_home_presentation_WorkerHomeViewModel = "com.fixit.feature.worker.home.presentation.WorkerHomeViewModel";

      static String com_fixit_feature_worker_profile_presentation_WorkerProfileViewModel = "com.fixit.feature.worker.profile.presentation.WorkerProfileViewModel";

      static String com_fixit_feature_worker_presentation_WorkerStatusViewModel = "com.fixit.feature.worker.presentation.WorkerStatusViewModel";

      static String com_fixit_feature_auth_presentation_AuthViewModel = "com.fixit.feature.auth.presentation.AuthViewModel";

      static String com_fixit_feature_worker_job_presentation_WorkerJobViewModel = "com.fixit.feature.worker.job.presentation.WorkerJobViewModel";

      static String com_fixit_feature_worker_wallet_presentation_WorkerWalletViewModel = "com.fixit.feature.worker.wallet.presentation.WorkerWalletViewModel";

      @KeepFieldType
      WorkerOrdersViewModel com_fixit_feature_worker_orders_presentation_WorkerOrdersViewModel2;

      @KeepFieldType
      WorkerHomeViewModel com_fixit_feature_worker_home_presentation_WorkerHomeViewModel2;

      @KeepFieldType
      WorkerProfileViewModel com_fixit_feature_worker_profile_presentation_WorkerProfileViewModel2;

      @KeepFieldType
      WorkerStatusViewModel com_fixit_feature_worker_presentation_WorkerStatusViewModel2;

      @KeepFieldType
      AuthViewModel com_fixit_feature_auth_presentation_AuthViewModel2;

      @KeepFieldType
      WorkerJobViewModel com_fixit_feature_worker_job_presentation_WorkerJobViewModel2;

      @KeepFieldType
      WorkerWalletViewModel com_fixit_feature_worker_wallet_presentation_WorkerWalletViewModel2;
    }
  }

  private static final class ViewModelCImpl extends FixitApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<WorkerHomeViewModel> workerHomeViewModelProvider;

    private Provider<WorkerJobViewModel> workerJobViewModelProvider;

    private Provider<WorkerOrdersViewModel> workerOrdersViewModelProvider;

    private Provider<WorkerProfileViewModel> workerProfileViewModelProvider;

    private Provider<WorkerStatusViewModel> workerStatusViewModelProvider;

    private Provider<WorkerWalletViewModel> workerWalletViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;

      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    private LoginUseCase loginUseCase() {
      return new LoginUseCase(singletonCImpl.provideAuthRepositoryProvider.get());
    }

    private RegisterUseCase registerUseCase() {
      return new RegisterUseCase(singletonCImpl.provideAuthRepositoryProvider.get());
    }

    private GetTodayAppointmentsUseCase getTodayAppointmentsUseCase() {
      return new GetTodayAppointmentsUseCase(singletonCImpl.workerHomeRepositoryImplProvider.get());
    }

    private GetWorkerJobSummaryUseCase getWorkerJobSummaryUseCase() {
      return new GetWorkerJobSummaryUseCase(singletonCImpl.workerJobRepositoryImplProvider.get());
    }

    private FilterWorkerOrdersUseCase filterWorkerOrdersUseCase() {
      return new FilterWorkerOrdersUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private GetWorkerOrderByIdUseCase getWorkerOrderByIdUseCase() {
      return new GetWorkerOrderByIdUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private GetInitialJobStatusUseCase getInitialJobStatusUseCase() {
      return new GetInitialJobStatusUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private AdvanceJobStatusUseCase advanceJobStatusUseCase() {
      return new AdvanceJobStatusUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private SaveExtraCostsUseCase saveExtraCostsUseCase() {
      return new SaveExtraCostsUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private GetExtraCostsUseCase getExtraCostsUseCase() {
      return new GetExtraCostsUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private CalculateTotalExtraUseCase calculateTotalExtraUseCase() {
      return new CalculateTotalExtraUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private GenerateWorkerPaymentQrUseCase generateWorkerPaymentQrUseCase() {
      return new GenerateWorkerPaymentQrUseCase(singletonCImpl.workerOrdersRepositoryImplProvider.get());
    }

    private LogoutUseCase logoutUseCase() {
      return new LogoutUseCase(singletonCImpl.provideAuthRepositoryProvider.get());
    }

    private GetWorkerAvailabilityUseCase getWorkerAvailabilityUseCase() {
      return new GetWorkerAvailabilityUseCase(singletonCImpl.workerAvailabilityRepositoryImplProvider.get());
    }

    private ToggleWorkerAvailabilityUseCase toggleWorkerAvailabilityUseCase() {
      return new ToggleWorkerAvailabilityUseCase(singletonCImpl.workerAvailabilityRepositoryImplProvider.get());
    }

    private GetWalletBalanceUseCase getWalletBalanceUseCase() {
      return new GetWalletBalanceUseCase(singletonCImpl.workerWalletRepositoryImplProvider.get());
    }

    private GetWalletTransactionsUseCase getWalletTransactionsUseCase() {
      return new GetWalletTransactionsUseCase(singletonCImpl.workerWalletRepositoryImplProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.workerHomeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.workerJobViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.workerOrdersViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.workerProfileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.workerStatusViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.workerWalletViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(7).put(LazyClassKeyProvider.com_fixit_feature_auth_presentation_AuthViewModel, ((Provider) authViewModelProvider)).put(LazyClassKeyProvider.com_fixit_feature_worker_home_presentation_WorkerHomeViewModel, ((Provider) workerHomeViewModelProvider)).put(LazyClassKeyProvider.com_fixit_feature_worker_job_presentation_WorkerJobViewModel, ((Provider) workerJobViewModelProvider)).put(LazyClassKeyProvider.com_fixit_feature_worker_orders_presentation_WorkerOrdersViewModel, ((Provider) workerOrdersViewModelProvider)).put(LazyClassKeyProvider.com_fixit_feature_worker_profile_presentation_WorkerProfileViewModel, ((Provider) workerProfileViewModelProvider)).put(LazyClassKeyProvider.com_fixit_feature_worker_presentation_WorkerStatusViewModel, ((Provider) workerStatusViewModelProvider)).put(LazyClassKeyProvider.com_fixit_feature_worker_wallet_presentation_WorkerWalletViewModel, ((Provider) workerWalletViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String com_fixit_feature_worker_profile_presentation_WorkerProfileViewModel = "com.fixit.feature.worker.profile.presentation.WorkerProfileViewModel";

      static String com_fixit_feature_worker_orders_presentation_WorkerOrdersViewModel = "com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel";

      static String com_fixit_feature_auth_presentation_AuthViewModel = "com.fixit.feature.auth.presentation.AuthViewModel";

      static String com_fixit_feature_worker_presentation_WorkerStatusViewModel = "com.fixit.feature.worker.presentation.WorkerStatusViewModel";

      static String com_fixit_feature_worker_home_presentation_WorkerHomeViewModel = "com.fixit.feature.worker.home.presentation.WorkerHomeViewModel";

      static String com_fixit_feature_worker_wallet_presentation_WorkerWalletViewModel = "com.fixit.feature.worker.wallet.presentation.WorkerWalletViewModel";

      static String com_fixit_feature_worker_job_presentation_WorkerJobViewModel = "com.fixit.feature.worker.job.presentation.WorkerJobViewModel";

      @KeepFieldType
      WorkerProfileViewModel com_fixit_feature_worker_profile_presentation_WorkerProfileViewModel2;

      @KeepFieldType
      WorkerOrdersViewModel com_fixit_feature_worker_orders_presentation_WorkerOrdersViewModel2;

      @KeepFieldType
      AuthViewModel com_fixit_feature_auth_presentation_AuthViewModel2;

      @KeepFieldType
      WorkerStatusViewModel com_fixit_feature_worker_presentation_WorkerStatusViewModel2;

      @KeepFieldType
      WorkerHomeViewModel com_fixit_feature_worker_home_presentation_WorkerHomeViewModel2;

      @KeepFieldType
      WorkerWalletViewModel com_fixit_feature_worker_wallet_presentation_WorkerWalletViewModel2;

      @KeepFieldType
      WorkerJobViewModel com_fixit_feature_worker_job_presentation_WorkerJobViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.fixit.feature.auth.presentation.AuthViewModel 
          return (T) new AuthViewModel(viewModelCImpl.loginUseCase(), viewModelCImpl.registerUseCase());

          case 1: // com.fixit.feature.worker.home.presentation.WorkerHomeViewModel 
          return (T) new WorkerHomeViewModel(viewModelCImpl.getTodayAppointmentsUseCase());

          case 2: // com.fixit.feature.worker.job.presentation.WorkerJobViewModel 
          return (T) new WorkerJobViewModel(viewModelCImpl.getWorkerJobSummaryUseCase());

          case 3: // com.fixit.feature.worker.orders.presentation.WorkerOrdersViewModel 
          return (T) new WorkerOrdersViewModel(viewModelCImpl.filterWorkerOrdersUseCase(), viewModelCImpl.getWorkerOrderByIdUseCase(), viewModelCImpl.getInitialJobStatusUseCase(), viewModelCImpl.advanceJobStatusUseCase(), viewModelCImpl.saveExtraCostsUseCase(), viewModelCImpl.getExtraCostsUseCase(), viewModelCImpl.calculateTotalExtraUseCase(), viewModelCImpl.generateWorkerPaymentQrUseCase());

          case 4: // com.fixit.feature.worker.profile.presentation.WorkerProfileViewModel 
          return (T) new WorkerProfileViewModel(viewModelCImpl.logoutUseCase());

          case 5: // com.fixit.feature.worker.presentation.WorkerStatusViewModel 
          return (T) new WorkerStatusViewModel(viewModelCImpl.getWorkerAvailabilityUseCase(), viewModelCImpl.toggleWorkerAvailabilityUseCase());

          case 6: // com.fixit.feature.worker.wallet.presentation.WorkerWalletViewModel 
          return (T) new WorkerWalletViewModel(viewModelCImpl.getWalletBalanceUseCase(), viewModelCImpl.getWalletTransactionsUseCase());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends FixitApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends FixitApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends FixitApp_HiltComponents.SingletonC {
    private final AuthModule authModule;

    private final NetworkModule networkModule;

    private final AppModule appModule;

    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<SharedPreferences> provideSharedPreferencesProvider;

    private Provider<SharedPrefsSessionStorage> sharedPrefsSessionStorageProvider;

    private Provider<SessionStorage> provideSessionStorageProvider;

    private Provider<AuthInterceptor> authInterceptorProvider;

    private Provider<HttpLoggingInterceptor> provideLoggingInterceptorProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<AuthApi> provideAuthApiProvider;

    private Provider<AuthRepositoryImpl> authRepositoryImplProvider;

    private Provider<AuthRepository> provideAuthRepositoryProvider;

    private Provider<WorkerHomeRepositoryImpl> workerHomeRepositoryImplProvider;

    private Provider<WorkerJobRepositoryImpl> workerJobRepositoryImplProvider;

    private Provider<WorkerOrdersRepositoryImpl> workerOrdersRepositoryImplProvider;

    private Provider<WorkerAvailabilityRepositoryImpl> workerAvailabilityRepositoryImplProvider;

    private Provider<WorkerWalletRepositoryImpl> workerWalletRepositoryImplProvider;

    private SingletonCImpl(AppModule appModuleParam,
        ApplicationContextModule applicationContextModuleParam, AuthModule authModuleParam,
        NetworkModule networkModuleParam) {
      this.authModule = authModuleParam;
      this.networkModule = networkModuleParam;
      this.appModule = appModuleParam;
      this.applicationContextModule = applicationContextModuleParam;
      initialize(appModuleParam, applicationContextModuleParam, authModuleParam, networkModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final AppModule appModuleParam,
        final ApplicationContextModule applicationContextModuleParam,
        final AuthModule authModuleParam, final NetworkModule networkModuleParam) {
      this.provideSharedPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SharedPreferences>(singletonCImpl, 8));
      this.sharedPrefsSessionStorageProvider = DoubleCheck.provider(new SwitchingProvider<SharedPrefsSessionStorage>(singletonCImpl, 7));
      this.provideSessionStorageProvider = DoubleCheck.provider(new SwitchingProvider<SessionStorage>(singletonCImpl, 6));
      this.authInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<AuthInterceptor>(singletonCImpl, 5));
      this.provideLoggingInterceptorProvider = DoubleCheck.provider(new SwitchingProvider<HttpLoggingInterceptor>(singletonCImpl, 9));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 4));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 3));
      this.provideAuthApiProvider = DoubleCheck.provider(new SwitchingProvider<AuthApi>(singletonCImpl, 2));
      this.authRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepositoryImpl>(singletonCImpl, 1));
      this.provideAuthRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 0));
      this.workerHomeRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WorkerHomeRepositoryImpl>(singletonCImpl, 10));
      this.workerJobRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WorkerJobRepositoryImpl>(singletonCImpl, 11));
      this.workerOrdersRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WorkerOrdersRepositoryImpl>(singletonCImpl, 12));
      this.workerAvailabilityRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WorkerAvailabilityRepositoryImpl>(singletonCImpl, 13));
      this.workerWalletRepositoryImplProvider = DoubleCheck.provider(new SwitchingProvider<WorkerWalletRepositoryImpl>(singletonCImpl, 14));
    }

    @Override
    public void injectFixitApp(FixitApp fixitApp) {
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.fixit.feature.auth.domain.repository.AuthRepository 
          return (T) AuthModule_ProvideAuthRepositoryFactory.provideAuthRepository(singletonCImpl.authModule, singletonCImpl.authRepositoryImplProvider.get());

          case 1: // com.fixit.feature.auth.data.repository.AuthRepositoryImpl 
          return (T) new AuthRepositoryImpl(singletonCImpl.provideAuthApiProvider.get(), singletonCImpl.provideSessionStorageProvider.get());

          case 2: // com.fixit.feature.auth.data.remote.api.AuthApi 
          return (T) AuthModule_ProvideAuthApiFactory.provideAuthApi(singletonCImpl.authModule, singletonCImpl.provideRetrofitProvider.get());

          case 3: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.networkModule, singletonCImpl.provideOkHttpClientProvider.get());

          case 4: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.networkModule, singletonCImpl.authInterceptorProvider.get(), singletonCImpl.provideLoggingInterceptorProvider.get());

          case 5: // com.fixit.core.network.AuthInterceptor 
          return (T) new AuthInterceptor(singletonCImpl.provideSessionStorageProvider.get());

          case 6: // com.fixit.core.storage.SessionStorage 
          return (T) AppModule_ProvideSessionStorageFactory.provideSessionStorage(singletonCImpl.appModule, singletonCImpl.sharedPrefsSessionStorageProvider.get());

          case 7: // com.fixit.core.storage.SharedPrefsSessionStorage 
          return (T) new SharedPrefsSessionStorage(singletonCImpl.provideSharedPreferencesProvider.get());

          case 8: // android.content.SharedPreferences 
          return (T) AppModule_ProvideSharedPreferencesFactory.provideSharedPreferences(singletonCImpl.appModule, ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // okhttp3.logging.HttpLoggingInterceptor 
          return (T) NetworkModule_ProvideLoggingInterceptorFactory.provideLoggingInterceptor(singletonCImpl.networkModule);

          case 10: // com.fixit.feature.worker.home.data.repository.WorkerHomeRepositoryImpl 
          return (T) new WorkerHomeRepositoryImpl();

          case 11: // com.fixit.feature.worker.job.data.repository.WorkerJobRepositoryImpl 
          return (T) new WorkerJobRepositoryImpl();

          case 12: // com.fixit.feature.worker.orders.data.repository.WorkerOrdersRepositoryImpl 
          return (T) new WorkerOrdersRepositoryImpl();

          case 13: // com.fixit.feature.worker.availability.data.repository.WorkerAvailabilityRepositoryImpl 
          return (T) new WorkerAvailabilityRepositoryImpl();

          case 14: // com.fixit.feature.worker.wallet.data.repository.WorkerWalletRepositoryImpl 
          return (T) new WorkerWalletRepositoryImpl();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
