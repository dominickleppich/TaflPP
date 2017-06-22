package taflPP.preset;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Player extends Remote {
    Move request() throws Exception, RemoteException;

    void confirm(Status boardStatus) throws Exception, RemoteException;

    void update(Move opponentMove, Status boardStatus) throws Exception, RemoteException;

    void reset(int man) throws Exception, RemoteException;
}
