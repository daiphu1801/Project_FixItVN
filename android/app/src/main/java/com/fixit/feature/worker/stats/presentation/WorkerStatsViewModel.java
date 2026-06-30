package com.fixit.feature.worker.stats.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.fixit.core.ui.BaseViewModel;
import com.fixit.feature.worker.home.domain.model.Appointment;
import com.fixit.feature.worker.home.domain.usecase.GetWorkerHomeUseCase;
import com.fixit.feature.worker.stats.domain.model.WorkerStats;
import com.fixit.feature.worker.stats.domain.usecase.GetWorkerStatsUseCase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WorkerStatsViewModel extends BaseViewModel {

    private final GetWorkerStatsUseCase getWorkerStatsUseCase;
    private final GetWorkerHomeUseCase getWorkerHomeUseCase;

    private final MutableLiveData<WorkerStats> _workerStats = new MutableLiveData<>();
    public LiveData<WorkerStats> workerStats = _workerStats;

    private final MutableLiveData<String> _selectedFilter = new MutableLiveData<>("week"); // Default to Week
    public LiveData<String> selectedFilter = _selectedFilter;

    private final MutableLiveData<Long> _filteredTotalIncome = new MutableLiveData<>(0L);
    public LiveData<Long> filteredTotalIncome = _filteredTotalIncome;

    private final MutableLiveData<Integer> _filteredTotalOrders = new MutableLiveData<>(0);
    public LiveData<Integer> filteredTotalOrders = _filteredTotalOrders;

    private final MutableLiveData<Long> _filteredAvgPerOrder = new MutableLiveData<>(0L);
    public LiveData<Long> filteredAvgPerOrder = _filteredAvgPerOrder;



    private final MutableLiveData<List<Appointment>> _todayAppointments = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Appointment>> todayAppointments = _todayAppointments;

    @Inject
    public WorkerStatsViewModel(
            GetWorkerStatsUseCase getWorkerStatsUseCase,
            GetWorkerHomeUseCase getWorkerHomeUseCase
    ) {
        this.getWorkerStatsUseCase = getWorkerStatsUseCase;
        this.getWorkerHomeUseCase = getWorkerHomeUseCase;
        loadStats();
        loadHomeAppointments();
    }

    public void loadStats() {
        loadStats(_selectedFilter.getValue());
    }

    public void loadStats(String period) {
        setLoading(true);
        getWorkerStatsUseCase.execute(period, result -> {
            setLoading(false);
            if (result.isSuccess()) {
                WorkerStats stats = result.getData();
                _workerStats.postValue(stats);
                calculateFilteredStats(stats, period);
            } else if (result.getError() != null) {
                setError(result.getError().getMessage());
            }
        });
    }

    public void loadHomeAppointments() {
        getWorkerHomeUseCase.execute(result -> {
            if (result.isSuccess() && result.getData() != null) {
                List<Appointment> list = result.getData().getTodayAppointments();
                if (list != null) {
                    _todayAppointments.postValue(list);
                } else {
                    _todayAppointments.postValue(new ArrayList<>());
                }
            }
        });
    }

    public void setFilter(String filter) {
        _selectedFilter.setValue(filter);
        loadStats(filter);
    }

    private void calculateFilteredStats(WorkerStats stats, String filter) {
        if (stats == null || stats.getOverview() == null) {
            return;
        }

        WorkerStats.StatsOverview overview = stats.getOverview();
        long income = 0;
        int orders = 0;

        if ("today".equalsIgnoreCase(filter)) {
            income = overview.getIncomeToday();
            orders = overview.getCompletedJobsToday();
        } else if ("week".equalsIgnoreCase(filter)) {
            income = overview.getIncomeThisWeek();
            // Sum completed jobs from chart point for the week
            if (stats.getIncomeChart() != null) {
                for (WorkerStats.IncomeChartPoint point : stats.getIncomeChart()) {
                    orders += point.getCompletedJobs();
                }
            }
        } else if ("month".equalsIgnoreCase(filter)) {
            income = overview.getIncomeThisMonth();
            orders = overview.getCompletedJobsThisMonth();
        }

        long avg = orders > 0 ? income / orders : 0L;

        _filteredTotalIncome.postValue(income);
        _filteredTotalOrders.postValue(orders);
        _filteredAvgPerOrder.postValue(avg);


    }
}
