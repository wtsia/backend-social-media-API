package Service;

import DAO.SocialMediaDAO;
import Model.Account;
import Model.Message;

import java.util.List;

public class SocialMediaService {
    private SocialMediaDAO DAO;

    public SocialMediaService(SocialMediaDAO DAO) {
        this.DAO = DAO;
    }

    // user registration
    public Account registerAccount(Account account) throws IllegalArgumentException {
        // username must not be blank.
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            return null;
        }
        // password length.
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            return null;
        }
        // username is unique.
        if (DAO.getAccountByUsername(account.getUsername()) != null) {
            return null;
        }
        return DAO.registerAccount(account);
    }

    // user login
    public Account login(String username, String password) throws IllegalArgumentException {
        if (username == null || username.trim().isEmpty() ||
            password == null || password.isEmpty()) {
                return null;
        }
        Account account = DAO.login(username, password);
        if (account == null) {
            return null;
        }
        return account;
    }

    // new message
    public Message postMessage(Message message) throws IllegalArgumentException {
        // must not be blank and must be 255 characters or fewer.
        if (message.getMessage_text() == null || message.getMessage_text().trim().isEmpty()) {
            return null;
        }
        if (message.getMessage_text().length() > 255) {
            return null;
        }
        return DAO.createMessage(message);
    }

    // retrieve all msgs
    public List<Message> getAllMessages() {
        return DAO.getAllMessages();
    }

    // retreive msg by id
    public Message getMessageById(int messageId) {
        return DAO.getMessageById(messageId);
    }

    // delete msg by id
    public Message deleteMessageById(int messageId) {
        return DAO.deleteMessageById(messageId);
    }

    // update msg by id
    public Message updateMessageById(int messageId, String newMessageText) throws IllegalArgumentException {
        if (newMessageText == null || newMessageText.trim().isEmpty()) {
            return null;
        }
        if (newMessageText.length() > 255) {
            return null;
        }
        Message updatedMessage = DAO.updateMessageById(messageId, newMessageText);
        if (updatedMessage == null) {
            return null;
        }
        return updatedMessage;
    }

    // retrieve all msgs by user
    public List<Message> getMessagesByUserId(int accountId) {
        return DAO.getMessagesByUserId(accountId);
    }
}
