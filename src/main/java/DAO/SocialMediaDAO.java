package DAO;

import Model.Account;
import Model.Message;
import Util.ConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SocialMediaDAO {

    // get account by username
    public Account getAccountByUsername(String username) {
        String sql = "SELECT account_id, username, password FROM account WHERE username = ?";
        Account account = null;
        try (Connection conn = ConnectionUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                account = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    
    // user registration
    public Account registerAccount(Account account) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO account (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS); 
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                int generated_acc_id = (int) keys.getLong(1);
                return new Account(generated_acc_id, account.getUsername(), account.getPassword());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // user logins
    public Account login(String username, String password) {
        Connection conn = ConnectionUtil.getConnection();
        Account account = null;
        try {
            String sql = "SELECT account_id, username, password FROM account WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql); 
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                account = new Account(rs.getInt("account_id"), rs.getString("username"), rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return account;
    }
    
    // new message
    public Message createMessage(Message message) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "INSERT INTO message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";

            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, message.getPosted_by());
            ps.setString(2, message.getMessage_text());
            ps.setLong(3, message.getTime_posted_epoch());
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                message.setMessage_id(keys.getInt(1));
            } else {
                throw new SQLException("no ID");
            }
            return message;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // retrieve all messages
    public List<Message> getAllMessages() {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message";
            
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql); 
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
    
    // retrieve message by ID
    public Message getMessageById(int messageId) {
        Connection conn = ConnectionUtil.getConnection();
        Message message = null;
        try {    
            String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE message_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, messageId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                return message;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }
    
    // delete message by ID
    public Message deleteMessageById(int messageId) {
        Connection conn = ConnectionUtil.getConnection();
        Message message = getMessageById(messageId);
        
        if (message == null) {
            return null;
        }

        try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, messageId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return message;
    }
    
    // update message by ID
    public Message updateMessageById(int messageId, String newMessageText) {
        Connection conn = ConnectionUtil.getConnection();
        try {
            String sql = "UPDATE message SET message_text = ? WHERE message_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
             
            ps.setString(1, newMessageText);
            ps.setInt(2, messageId);

            ps.executeUpdate();
            return getMessageById(messageId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // retrieve messages by user
    public List<Message> getMessagesByUserId(int accountId) {
        Connection conn = ConnectionUtil.getConnection();
        List<Message> messages = new ArrayList<>();
        try {
            String sql = "SELECT message_id, posted_by, message_text, time_posted_epoch FROM message WHERE posted_by = ? ORDER BY time_posted_epoch DESC";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"), rs.getInt("posted_by"), rs.getString("message_text"), rs.getLong("time_posted_epoch"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messages;
    }
}
