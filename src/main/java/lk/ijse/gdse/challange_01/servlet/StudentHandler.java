package lk.ijse.gdse.challange_01.servlet;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import lk.ijse.gdse.challange_01.dto.StudentDTO;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/student")
public class StudentHandler extends HttpServlet {
    private Connection connection;

    @Override
    public void init() throws ServletException {
        try {

            Class.forName(getServletContext().getInitParameter("Class_path"));
            String userName = getServletContext().getInitParameter("user");
            String password = getServletContext().getInitParameter("password");
            String url = getServletContext().getInitParameter("connection");
            this.connection = DriverManager.getConnection(url,userName,password);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getContentType() == null || !request.getContentType().toLowerCase().startsWith("application/json")){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        try {

            Jsonb jsonb = JsonbBuilder.create();
            StudentDTO studentObj = jsonb.fromJson(request.getReader(), StudentDTO.class);

            if(studentObj.getName() == null || !studentObj.getName().matches("[A-Za-z ]+")){
                throw new RuntimeException("Invalid Name");
            } else if (studentObj.getCity() == null || !studentObj.getCity().matches("[A-Za-z ]+")) {
                throw new RuntimeException("Invalid City");
            } else if (studentObj.getEmail()==null) {
                throw new RuntimeException("Invalid Email");
            } else if (studentObj.getLevel() <= 0) {
                throw new RemoteException("Invalid Level");
            }

            PreparedStatement preparedStatement = connection.prepareStatement("insert into student values(?,?,?,?,?)");
            preparedStatement.setObject(1,studentObj.getId());
            preparedStatement.setObject(2,studentObj.getName());
            preparedStatement.setObject(3,studentObj.getEmail());
            preparedStatement.setObject(4,studentObj.getCity());
            preparedStatement.setObject(5,studentObj.getLevel());

            boolean isAdded = preparedStatement.executeUpdate() > 0;

            if (!isAdded) {
                throw new RuntimeException("Save Failed");
            } else {
                System.out.println("Student Added..");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getContentType() == null || !request.getContentType().toLowerCase().startsWith("application/json")){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO studentDTO = jsonb.fromJson(request.getReader(), StudentDTO.class);

        if(studentDTO.getName() == null || !studentDTO.getName().matches("[A-Za-z ]+")){
            throw new RuntimeException("Invalid Name");
        } else if (studentDTO.getCity() == null || !studentDTO.getCity().matches("[A-Za-z ]+")) {
            throw new RuntimeException("Invalid City");
        } else if (studentDTO.getEmail()==null) {
            throw new RuntimeException("Invalid Email");
        } else if (studentDTO.getLevel() <= 0) {
            throw new RemoteException("Invalid Level");
        }

        try {

            PreparedStatement ps = connection.prepareStatement("update student set name=?,email=?,city=?,level=? where studentId=?");
            ps.setObject(1, studentDTO.getName());
            ps.setObject(2, studentDTO.getEmail());
            ps.setObject(3, studentDTO.getCity());
            ps.setObject(4, studentDTO.getLevel());
            ps.setObject(5, studentDTO.getId());

            boolean isUpdated = ps.executeUpdate() > 0;

            if (!isUpdated) {
                throw new RuntimeException("Update Failed");
            } else {
                System.out.println("Student Updated..");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if(request.getContentType() == null || !request.getContentType().toLowerCase().startsWith("application/json")){
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }

        Jsonb jsonb = JsonbBuilder.create();
        StudentDTO studentDTO = jsonb.fromJson(request.getReader(), StudentDTO.class);

        try {

            PreparedStatement ps = connection.prepareStatement("delete from student where studentId=?");
            ps.setObject(1,studentDTO.getId());

            boolean isDelete = ps.executeUpdate() > 0;

            if (!isDelete) {
                throw new RuntimeException("Delete Failed");
            } else {
                System.out.println("Student deleted..");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
