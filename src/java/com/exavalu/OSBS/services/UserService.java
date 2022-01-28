/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.exavalu.OSBS.services;

import com.exavalu.OSBS.core.ConnectionManager;
import com.exavalu.OSBS.pojos.City;
import com.exavalu.OSBS.pojos.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.apache.struts2.ServletActionContext;

/**
 *
 * @author AKSHAY
 */
public class UserService {

    public int registerUser(String emailId) {
        int i = 0;
        User user = new User();
        user.setEmailId(emailId);
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            String sql = "INSERT INTO users\n"
                    + "(emailId,\n"
                    + "roleId,\n"
                    + "status)\n"
                    + "VALUES\n"
                    + "(?,?,?);";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, emailId);
            ps.setInt(2, 2);
            ps.setInt(3, 1);
            int r = ps.executeUpdate();
            if (r > 0) {
                i = 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return i;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return i;
    }

    public User fetchUserDetails(String emailId) {
        User user = new User();
        user.setEmailId(emailId);
        Connection con = null;
        try {
            con = ConnectionManager.getConnection();
            String sql = "SELECT * FROM users WHERE status!=0";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                user.setEmailId(rs.getString("emailId"));
                user.setRoleId(rs.getInt("roleId"));
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return user;
        } finally {
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    Logger.getLogger(UserService.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public String generateOTP() {
        String values = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rndm_method = new Random();

        char[] otp = new char[4];

        for (int j = 0; j < 4; j++) {
            otp[j] = values.charAt(rndm_method.nextInt(values.length()));
        }

        String generatedOTP = new String(otp);
        return generatedOTP;
    }

    public int sendMail(String to, String otp) {
        int i = 0;
        String from = "urbanwareservice@gmail.com";
        String password = "exavalu@123";
        String subject = "OTP For Login";
        String msg = otp;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // check the authentication
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));

            ServletActionContext.getRequest().setAttribute("otp", msg);
            ServletActionContext.getRequest().setAttribute("email", to);
            // add the Subject of email
            message.setSubject(subject);

            // message body
            message.setText(msg);

            Transport.send(message);
            return 1;
        } catch (MessagingException e) {
            e.printStackTrace();
            return i;
        } finally {
            return i;
        }

    }

    public List reportPinCode(String cityName) throws SQLException, Exception {
        System.out.println(cityName);
        ResultSet rs = null;
        Connection con = null;
        List<City> pinCodeList = new ArrayList<>();
        try {
            String sql = "SELECT pinCode FROM cities WHERE cityName=? AND status!=0";
            con = ConnectionManager.getConnection();
            System.out.println("Connection is " + con);
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, cityName);
            rs = ps.executeQuery();
            while (rs.next()) {

                City city = new City();
                city.setPinCode(rs.getInt("pinCode"));
                //city.setCityName(rs.getString("cityName"));

                pinCodeList.add(city);
            }
            return pinCodeList;
        } catch (Exception e) {
            return null;
        } finally {
            if (con != null) {
                con.close();
            }
        }
    }
//    public int addOrders(String name, String address, String phoneNo, double totalPrice, String users) throws Exception {
//        int i = 0;
//        Connection con = null;
//        try {
//            con = ConnectionManager.getConnection();
//            String sql = "INSERT INTO servicetype VALUES (?,?,?,?)";
//            PreparedStatement ps = con.prepareStatement(sql);
//            ps.setString(1, type);
//            ps.setDouble(2, price);
//            ps.setInt(3, 1);
//            ps.setInt(4, services_serviceId);
//            System.out.println("SQL for insert=" + ps);
//            i = ps.executeUpdate();
//            return i;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return i;
//        } finally {
//            if (con != null) {
//                con.close();
//            }
//        }
//    }
}
