package com.dcom;

import com.dcom.rmi.EmployeeManagementService;
import com.dcom.services.EmployeeManagementServiceImpl;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Main {
    public static void main(String[] args) {
        try {
            EmployeeManagementService employeeManagementService = new EmployeeManagementServiceImpl();
            Registry registry = LocateRegistry.createRegistry(8081);
            registry.rebind("employeeManagementService", employeeManagementService);
            System.out.println("Employee Management Service is running...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}