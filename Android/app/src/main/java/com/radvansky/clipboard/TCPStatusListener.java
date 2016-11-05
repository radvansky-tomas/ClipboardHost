package com.radvansky.clipboard;

/**
 * Created by tomasradvansky on 06/10/2016.
 */

public interface TCPStatusListener {
    public void TCPStatusChanged(boolean isOnline);
    public void FileTransfer(int progress);
}
