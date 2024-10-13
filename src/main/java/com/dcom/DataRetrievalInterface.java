package com.dcom;


import com.dcom.dataModel.Employee;
import com.dcom.dataModel.User;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DataRetrievalInterface extends Remote {
    int createUser(User user, String name, double salary, int totalDayOfWork, int AvailablePaidLeave) throws RemoteException;
    int getUserCountByEmail(String email) throws RemoteException;


    Employee retrieveEmployee(int userId) throws RemoteException;
    boolean updateEmployee(Employee employee) throws RemoteException;
    List<Employee> retrieveAllEmployee() throws RemoteException;
}
