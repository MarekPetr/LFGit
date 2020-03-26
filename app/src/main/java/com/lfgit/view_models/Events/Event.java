package com.lfgit.view_models.Events;

/**
 * source: https://medium.com/androiddevelopers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 */
@SuppressWarnings("unused")
public class Event<T> {

    private boolean hasBeenHandled = false;
    private T mContent;

    public Event(T content) {
        this.mContent = content;
    }

    /**
     * Returns the content and prevents its use again.
     */
    public T getContentIfNotHandled() {
        if (hasBeenHandled) {
            return null;
        } else {
            hasBeenHandled = true;
            return mContent;
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    public T peekContent() {
        return mContent;
    }

    public boolean hasBeenHandled() {
        return hasBeenHandled;
    }
}
