package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import DAO.SocialMediaDAO;
import Service.SocialMediaService;
import Model.Account;
import Model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    SocialMediaService service;

    public SocialMediaController() {
        SocialMediaDAO DAO = new SocialMediaDAO();
        this.service = new SocialMediaService(DAO);
    }

    public Javalin startAPI() {
        Javalin app = Javalin.create();
        // POST localhost:8080/register
        app.post("/register", this::handleRegister);
        // POST localhost:8080/login
        app.post("/login", this::handleLogin);
        // POST localhost:8080/messages
        app.post("/messages", this::handleCreateMessage);
        // GET localhost:8080/messages
        app.get("/messages", this::handleGetAllMessages);
        // GET localhost:8080/messages/{message_id}
        app.get("/messages/{message_id}", this::handleGetMessageById);
        // DELETE localhost:8080/messages/{message_id}
        app.delete("/messages/{message_id}", this::handleDeleteMessage);
        // PATCH localhost:8080/messages/{message_id}
        app.patch("/messages/{message_id}", this::handleUpdateMessage);
        // GET localhost:8080/accounts/{account_id}/messages
        app.get("/accounts/{account_id}/messages", this::handleGetMessagesByUser);

        // example
        // app.get("example-endpoint", this::exampleHandler);

        return app;
    }

    /**
     * This is an example an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    // private void exampleHandler(Context context) {
    //     context.json("sample text");
    // }

    // user registration
    private void handleRegister(Context ctx) {
        try {
            Account account = ctx.bodyAsClass(Account.class);
            Account registered = service.registerAccount(account);
            if (registered == null) {
                ctx.status(400).result("");
            } else {
                ctx.json(registered);
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500);
        }
    }

    // user login
    private void handleLogin(Context ctx) {
        try {
            Account account = ctx.bodyAsClass(Account.class);
            Account loggedIn = service.login(account.getUsername(), account.getPassword());
            
            if (loggedIn == null) {
                ctx.status(401).result("");
            } else {
                ctx.json(loggedIn);
            }
        } catch (IllegalArgumentException e) {
            ctx.status(401).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500);
        }
    }

    // creating a new message
    private void handleCreateMessage(Context ctx) {
        try {
            Message message = ctx.bodyAsClass(Message.class);
            Message created = service.postMessage(message);
            
            if (created == null) {
                ctx.status(400).result("");
            } else {
                ctx.json(created);
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        } catch (Exception e) {
            ctx.status(500);
        }
    }

    // retrieving all messages
    private void handleGetAllMessages(Context ctx) {
        List<Message> messages = service.getAllMessages();
        if (messages == null) {
            messages = new ArrayList<>();
        }
        ctx.json(messages);
    }

    // retrieving a message by ID
    private void handleGetMessageById(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message message = service.getMessageById(messageId);
            
            if (message == null) {
                ctx.result("");
            } else {
                ctx.json(message);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("invalid message ID.");
        }
    }

    // deleting a message by ID
    private void handleDeleteMessage(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message deleted = service.deleteMessageById(messageId);
            ctx.json(deleted != null ? deleted : "");
        } catch (NumberFormatException e) {
            ctx.status(400).result("invalid message ID.");
        }
    }

    // updating a message text by ID
    private void handleUpdateMessage(Context ctx) {
        try {
            int messageId = Integer.parseInt(ctx.pathParam("message_id"));
            Message updateData = ctx.bodyAsClass(Message.class);
            String newText = updateData.getMessage_text();
            Message updated = service.updateMessageById(messageId, newText);
                        
            if (updated == null) {
                ctx.status(400).result("");
            } else {
                ctx.json(updated);
            }
        } catch (IllegalArgumentException e) {
            ctx.status(400).result(e.getMessage());
        }
    }

    // retrieving all messages by user
    private void handleGetMessagesByUser(Context ctx) {
        try {
            int accountId = Integer.parseInt(ctx.pathParam("account_id"));
            List<Message> messages = service.getMessagesByUserId(accountId);
                        
            if (messages == null) {
                ctx.status(400).result("");
            } else {
                ctx.json(messages);
            }
        } catch (NumberFormatException e) {
            ctx.status(400).result("invalid account ID.");
        }
    }
}