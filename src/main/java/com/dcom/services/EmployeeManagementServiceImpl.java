package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.Employee;
import com.dcom.dataModel.User;
import com.dcom.dataModel.UserSessionInfo;
import com.dcom.rmi.EmployeeManagementService;
import com.dcom.serviceLocator.DbServiceLocator;
import com.dcom.utils.JWTUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class EmployeeManagementServiceImpl extends UnicastRemoteObject implements EmployeeManagementService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern pattern = Pattern.compile(EMAIL_REGEX);

    public EmployeeManagementServiceImpl() throws RemoteException {
        super();
    }

    public Map.Entry<Boolean, String> createUser(String token, String email, String userType, String pwd, String name, double salary) {
        boolean isSuccess = false;
        String message;

        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if(userSessionInfo != null){
            if(!userSessionInfo.getUserType().equals("HR")){
                message = "You do not have permission to perform this action";
                return new AbstractMap.SimpleEntry<>(false, message);
            }
        }else{
            System.out.println("Invalid or expired token: Unable to validate user session.");
            message = "invalid request";
            return new AbstractMap.SimpleEntry<>(false, message);
        }

        //predefine workdays and paidleave day
        int totalDayOfWork = 20;
        int availablePaidLeave = 10;

        Map.Entry<Boolean, String> isUserInfoValid = validateUserInfo(email, pwd, name, salary, totalDayOfWork, availablePaidLeave);
        if(!isUserInfoValid.getKey()){
            return new AbstractMap.SimpleEntry<>(false, isUserInfoValid.getValue());
        }


        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        String hashedPwd = hashPassword(pwd);

        User newUser = new User(hashedPwd, "active", userType, email);

        try {
            dbService.createUser(newUser, name, salary, totalDayOfWork, availablePaidLeave);
            isSuccess = true;
            message = "User created successfully!";
        } catch (Exception e) {
            System.out.println("An error occurred while creating the user: " + e.getMessage());
            message = "An error occurred while creating the user";
        }

        return new AbstractMap.SimpleEntry<>(isSuccess, message);
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(10));
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    private Map.Entry<Boolean, String> validateUserInfo(String email, String pwd, String name, double salary, int totalDayOfWork, int availablePaidLeave) {

        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        try{
            if(dbService.getUserCountByEmail(email) > 0){
                return new AbstractMap.SimpleEntry<>(false, "An account with this email already exists");
            }
        }catch (Exception e){
            return new AbstractMap.SimpleEntry<>(false, "Error while validating new user info");
        }


        if (!isValidEmail(email)) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid email format");
        }

        if (pwd.length() < 4 || pwd.length() > 20) {
            return new AbstractMap.SimpleEntry<>(false, "Password must be between 4 and 20 characters");
        }

        if (name.isEmpty()) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid name");
        }

        if (salary < 0) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid salary");
        }

        if (totalDayOfWork < 0) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid totalDayOfWork");
        }

        if (availablePaidLeave < 0) {
            return new AbstractMap.SimpleEntry<>(false, "Invalid AvailablePaidLeave");
        }

        return new AbstractMap.SimpleEntry<>(true, "");
    }


    public Employee retrieveEmployeeInfo(String token, int userId){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if(userSessionInfo != null){
            if(!userSessionInfo.getUserType().equals("HR")){
                if(userSessionInfo.getUserId() != userId){
                    System.out.println("User:" + userSessionInfo.getUserId() + " do not have permission to retrieve information of user:" + userId);
                    return null;
                }
            }
        }else{
            System.out.println("Invalid or expired token: Unable to validate user session.");
            return null;
        }

        Employee employee = null;
        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        try{
            employee = dbService.retrieveEmployee(userId);
        }catch (Exception e){
            System.out.println("An error occur while getting employee info");
        }

        return employee;

    }

    public boolean updateEmployeeAvailablePaidLeave(String token,int userId, int updatedAvailablePaidLeave){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if(userSessionInfo != null){
            if(!userSessionInfo.getUserType().equals("HR")){
                System.out.println("User:" + userSessionInfo.getUserId() + " do not have permission to perform this action");
                return false;
            }
        }else{
            System.out.println("Invalid or expired token: Unable to validate user session.");
            return false;
        }

        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        try{
            Employee employee = dbService.retrieveEmployee(userId);
            employee.setAvailablePaidLeave(updatedAvailablePaidLeave);

            return dbService.updateEmployee(employee);
        }catch (Exception e){
            System.out.println("An error occur while updating employee available paid leave");
            return false;
        }
    }

    public boolean updateEmployeeSalary(String token,int userId, int updatedSalary){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if(userSessionInfo != null){
            if(!userSessionInfo.getUserType().equals("HR")){
                System.out.println("User:" + userSessionInfo.getUserId() + " do not have permission to perform this action");
                return false;
            }
        }else{
            System.out.println("Invalid or expired token: Unable to validate user session.");
            return false;
        }

        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        try{
            Employee employee = dbService.retrieveEmployee(userId);
            employee.setSalary(updatedSalary);

            return dbService.updateEmployee(employee);
        }catch (Exception e){
            System.out.println("An error occur while updating employee salary");
            return false;
        }
    }

    public boolean updateEmployeeTotalDayOfWork(String token,int userId, int updatedTotalDayOfWork){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if(userSessionInfo != null){
            if(!userSessionInfo.getUserType().equals("HR")){
                System.out.println("User:" + userSessionInfo.getUserId() + " do not have permission to perform this action");
                return false;
            }
        }else{
            System.out.println("Invalid or expired token: Unable to validate user session.");
            return false;
        }

        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        try{
            Employee employee = dbService.retrieveEmployee(userId);
            employee.setTotalDaysOfWork(updatedTotalDayOfWork);

            return dbService.updateEmployee(employee);
        }catch (Exception e){
            System.out.println("An error occur while updating employee total day of work");
            return false;
        }
    }

    public List<Employee> retrieveAllEmployee(String token){
        UserSessionInfo userSessionInfo = JWTUtil.validateToken(token);
        if(userSessionInfo != null){
            if(!userSessionInfo.getUserType().equals("HR")){
                System.out.println("User:" + userSessionInfo.getUserId() + " do not have permission to perform this action");
                return null;
            }
        }else{
            System.out.println("Invalid or expired token: Unable to validate user session.");
            return null;
        }

        DataRetrievalInterface dbService = DbServiceLocator.getRmiService();
        List<Employee> employeeList = new ArrayList<>();
        try{
            employeeList = dbService.retrieveAllEmployee();
            return employeeList;
        }catch (Exception e){
            System.out.println("Error occur while retrieving employee list");
            return null;
        }
    }

}
