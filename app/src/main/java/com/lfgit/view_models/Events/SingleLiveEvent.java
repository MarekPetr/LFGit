package com.lfgit.view_models.Events;

import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;


/**
 * source: https://github.com/NordicSemiconductor/Android-nRF-Blinky/blob/master/app/src/main/java/no/nordicsemi/android/blinky/viewmodels/SingleLiveEvent.java
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 * <p>
 * This avoids a common problem with events: on configuration change (like rotation) an update
 * can be emitted if the observer is active. This LiveData only calls the observable if there's an
 * explicit call to setValue() or call().
 * <p>
 * Note that only one observer is going to be notified of changes.
 */
@SuppressWarnings("unused")
public class SingleLiveEvent<T> extends MutableLiveData<T> {
    private static final String TAG = "SingleLiveEvent";

    private final AtomicBoolean mPending = new AtomicBoolean(false);

    @MainThread
    public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<? super T> observer) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.");
        }

        // Observe the internal MutableLiveData
        super.observe(owner, t -> {
            if (mPending.compareAndSet(true, false)) {
                observer.onChanged(t);
            }
        });
    }
    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    @MainThread
    @Override
    public void setValue(@Nullable final T t) {
        mPending.set(true);
        super.setValue(t);
    }
    @MainThread
    public void setCall() {
        setValue(null);
    }

    @Override
    public void postValue(@Nullable final T t) {
        mPending.set(true);
        super.postValue(t);
    }

    public void postCall() {
        postValue(null);
    }
}
