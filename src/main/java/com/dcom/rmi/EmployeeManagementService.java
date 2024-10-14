package com.dcom.rmi;

import com.dcom.dataModel.Employee;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface EmployeeManagementService extends Remote {
    Map.Entry<Boolean, String> createUser(String token, String email, String userType, String pwd, String name, double salary) throws RemoteException;
    Employee retrieveEmployeeInfo(String token, int userId) throws RemoteException;
    boolean updateEmployeeAvailablePaidLeave(String token, int userId, int updatedAvailablePaidLEave) throws RemoteException;
    boolean updateEmployeeSalary(String token, int userId, int updatedSalary) throws RemoteException;
    boolean updateEmployeeTotalDayOfWork(String token, int userId, int updatedTotalDayOfWork) throws RemoteException;
    List<Employee> retrieveAllEmployee(String token) throws RemoteException;

}
