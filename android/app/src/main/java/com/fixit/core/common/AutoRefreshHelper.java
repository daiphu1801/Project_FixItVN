package com.fixit.core.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

public class AutoRefreshHelper {
    private final Context context;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable refreshRunnable;
    private final RefreshAction action;
    private final String[] broadcastActions;
    private final long intervalMs;
    private boolean isRunning = false;

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            triggerRefresh();
        }
    };

    public interface RefreshAction {
        void refresh();
    }

    public AutoRefreshHelper(Context context, RefreshAction action, String... broadcastActions) {
        this(context, 5000L, action, broadcastActions);
    }

    public AutoRefreshHelper(Context context, long intervalMs, RefreshAction action, String... broadcastActions) {
        this.context = context.getApplicationContext();
        this.intervalMs = intervalMs;
        this.action = action;
        this.broadcastActions = broadcastActions;
        this.refreshRunnable = new Runnable() {
            @Override
            public void run() {
                if (isRunning) {
                    if (intervalMs > 0) {
                        triggerRefresh();
                        handler.postDelayed(this, intervalMs);
                    }
                }
            }
        };
    }

    private void triggerRefresh() {
        if (action != null) {
            action.refresh();
        }
    }

    public void start() {
        if (isRunning) return;
        isRunning = true;

        // Kích hoạt load lần đầu ngay khi mở màn hình
        triggerRefresh();

        // Nếu có intervalMs > 0 thì mới chạy polling định kỳ
        if (intervalMs > 0) {
            handler.postDelayed(refreshRunnable, intervalMs);
        }

        // Đăng ký nhận broadcast sự kiện
        if (broadcastActions != null && broadcastActions.length > 0) {
            IntentFilter filter = new IntentFilter();
            for (String act : broadcastActions) {
                filter.addAction(act);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED);
            } else {
                context.registerReceiver(receiver, filter);
            }
        }
    }

    public void stop() {
        if (!isRunning) return;
        isRunning = false;

        handler.removeCallbacks(refreshRunnable);

        if (broadcastActions != null && broadcastActions.length > 0) {
            try {
                context.unregisterReceiver(receiver);
            } catch (Exception ignored) {
            }
        }
    }
}
