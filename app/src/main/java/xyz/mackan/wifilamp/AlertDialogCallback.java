package xyz.mackan.wifilamp;

public interface AlertDialogCallback<T, String> {
    public void alertDialogCallback(T ret, String buttonID);
    public void alertDialogCancelCallback(T ret, String buttonID);
}
