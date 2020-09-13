package com.lfgit.utilites;

/** Result wrapper */
public class ErrorWrapper {
    private String mResult;
    private Boolean mSuccess;

    public ErrorWrapper(String result, Boolean success) {
        this.mResult = result;
        this.mSuccess = success;
    }

    public String getResult() {
        return mResult;
    }
    public void setResult(String result) {
        this.mResult = result;
    }
    public Boolean getSuccess() {
        return mSuccess;
    }
    public void setSuccess(Boolean success) {
        this.mSuccess = success;
    }
}