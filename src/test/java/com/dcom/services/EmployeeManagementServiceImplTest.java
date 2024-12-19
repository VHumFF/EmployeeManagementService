package com.dcom.services;

import com.dcom.DataRetrievalInterface;
import com.dcom.dataModel.Employee;
import com.dcom.dataModel.User;
import com.dcom.dataModel.UserSessionInfo;
import com.dcom.serviceLocator.DbServiceLocator;
import com.dcom.utils.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.rmi.RemoteException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeManagementServiceImplTest {

    private EmployeeManagementServiceImpl employeeManagementService;

    @Mock
    private Employee employee;

    @Mock
    private UserSessionInfo userSessionInfo;

    @Mock
    private User user;

    @BeforeEach
    void setUp() throws RemoteException {
        MockitoAnnotations.openMocks(this);

        employeeManagementService = new EmployeeManagementServiceImpl();

    }

    @Test
    void testCreateUser_withValidToken() throws RemoteException {
        // Arrange
        String email = "test@example.com";
        String userType = "HR";
        UserSessionInfo userSessionInfo = new UserSessionInfo(1, userType);
        String token = "validToken";
        String password = "password123";
        String name = "John Doe";
        double salary = 50000;
        String expectedMessage = "User created successfully!";

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);

            when(mockDbService.getUserCountByEmail(email)).thenReturn(0);
            when(mockDbService.createUser(any(User.class), eq(name), eq(salary), eq(20), eq(10))).thenReturn(1);

            // Act
            Map.Entry<Boolean, String> result = employeeManagementService.createUser(token, email, userType, password, name, salary);

            // Assert
            assertTrue(result.getKey());
            assertEquals(expectedMessage, result.getValue());
        }


    }

    @Test
    void testCreateUser_withInvalidToken() throws RemoteException {
        // Arrange
        String email = "test@example.com";
        String userType = "HR";
        String token = "InvalidToken";
        String password = "password123";
        String name = "John Doe";
        double salary = 50000;

        // Act
        Map.Entry<Boolean, String> result = employeeManagementService.createUser(token, email, userType, password, name, salary);

        // Assert
        assertFalse(result.getKey());
        assertEquals("invalid request", result.getValue());
    }

    @Test
    void testRetrieveEmployeeInfo_withValidToken() throws RemoteException {
        // Arrange
        int userId = 1;
        UserSessionInfo userSessionInfo = new UserSessionInfo(1, "HR");
        String token = "validToken";
        Employee employee = new Employee(1, "John Doe", 50000, 20, 10);

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);
            when(mockDbService.retrieveEmployee(userId)).thenReturn(employee);

            // Act
            Employee result = employeeManagementService.retrieveEmployeeInfo(token, userId);

            // Assert
            assertNotNull(result);
            assertEquals("John Doe", result.getName());
        }


    }

    @Test
    void testRetrieveEmployeeInfo_withNoPermission() throws RemoteException {
        // Arrange
        int userId = 1;
        UserSessionInfo userSessionInfo = new UserSessionInfo(2, "Employee");
        String token = "invalidToken";

        // Act
        Employee result = employeeManagementService.retrieveEmployeeInfo(token, userId);

        // Assert
        assertNull(result);
    }

    @Test
    void testUpdateEmployeeAvailablePaidLeave() throws RemoteException {
        // Arrange
        int userId = 1;
        int newPaidLeave = 15;
        UserSessionInfo userSessionInfo = new UserSessionInfo(1, "HR");
        String token = "validToken";
        Employee employee = new Employee(userId, "John Doe", 50000, 20, 10);

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);
            when(mockDbService.retrieveEmployee(userId)).thenReturn(employee);
            when(mockDbService.updateEmployee(employee)).thenReturn(true);

            // Act
            boolean result = employeeManagementService.updateEmployeeAvailablePaidLeave(token, userId, newPaidLeave);

            // Assert
            assertTrue(result);
            assertEquals(15, employee.getAvailablePaidLeave());
        }

    }

    @Test
    void testUpdateEmployeeSalary() throws RemoteException {
        // Arrange
        int userId = 1;
        int newSalary = 55000;
        UserSessionInfo userSessionInfo = new UserSessionInfo(1, "HR");
        String token = "validToken";
        Employee employee = new Employee(userId, "John Doe", 50000, 20, 10);

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);
            when(mockDbService.retrieveEmployee(userId)).thenReturn(employee);
            when(mockDbService.updateEmployee(employee)).thenReturn(true);

            // Act
            boolean result = employeeManagementService.updateEmployeeSalary(token, userId, newSalary);

            // Assert
            assertTrue(result);
            assertEquals(55000, employee.getSalary());
        }


    }

    @Test
    void testRetrieveAllEmployee() throws RemoteException {
        // Arrange
        UserSessionInfo userSessionInfo = new UserSessionInfo(1, "HR");
        String token = "validToken";
        List<Employee> employeeList = List.of(new Employee(1, "John Doe", 50000, 20, 10));

        try (MockedStatic<JWTUtil> mockedJWTUtil = mockStatic(JWTUtil.class);
             MockedStatic<DbServiceLocator> mockedServiceLocator = mockStatic(DbServiceLocator.class)) {

            mockedJWTUtil.when(() -> JWTUtil.validateToken(token)).thenReturn(userSessionInfo);

            // Mocking the ServiceLocator and DB service
            DataRetrievalInterface mockDbService = mock(DataRetrievalInterface.class);
            mockedServiceLocator.when(DbServiceLocator::getRmiService).thenReturn(mockDbService);
            when(mockDbService.retrieveAllEmployee()).thenReturn(employeeList);

            // Act
            List<Employee> result = employeeManagementService.retrieveAllEmployee(token);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
        }

    }
}
