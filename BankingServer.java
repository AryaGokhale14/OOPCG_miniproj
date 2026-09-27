import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.InetSocketAddress;

import java.nio.charset.StandardCharsets;

import java.util.List;

public class BankingServer {

    private static BankingService bankingService =
            new BankingService();


    public static void main(String[] args)
            throws IOException {


        HttpServer server =
                HttpServer.create(
                        new InetSocketAddress(8080),
                        0
                );


        // Create Account
        server.createContext(
                "/api/account",
                BankingServer::handleAccount
        );


        // Start server
        server.start();


        System.out.println(
                "================================="
        );

        System.out.println(
                " Banking Server Started"
        );

        System.out.println(
                " http://localhost:8080"
        );

        System.out.println(
                "================================="
        );
    }


    // ==================================================
    // ACCOUNT HANDLER
    // ==================================================

    private static void handleAccount(
            HttpExchange exchange) throws IOException {


        addCorsHeaders(exchange);


        try {

            String method =
                    exchange.getRequestMethod();


            String path =
                    exchange.getRequestURI()
                            .getPath();


            // ------------------------------------------
            // CREATE ACCOUNT
            // POST /api/account
            // ------------------------------------------

            if (method.equals("POST")
                    && path.equals("/api/account")) {


                String body =
                        readRequestBody(exchange);


                String name =
                        getJsonValue(body, "name");

                String phone =
                        getJsonValue(body, "phone");

                String email =
                        getJsonValue(body, "email");

                double initialDeposit =
                        Double.parseDouble(
                                getJsonValue(
                                        body,
                                        "initialDeposit"
                                )
                        );


                Account account =
                        bankingService.createAccount(
                                name,
                                phone,
                                email,
                                initialDeposit
                        );


                String response =
                        accountToJson(account);


                sendResponse(
                        exchange,
                        200,
                        response
                );


                return;
            }


            // ------------------------------------------
            // URL:
            // /api/account/SB12345678
            // ------------------------------------------

            String[] parts =
                    path.split("/");


            if (parts.length < 4) {

                sendResponse(
                        exchange,
                        400,
                        errorJson(
                                "Invalid request"
                        )
                );

                return;
            }


            String accountNumber =
                    parts[3];


            // ------------------------------------------
            // GET ACCOUNT
            // ------------------------------------------

            if (method.equals("GET")
                    && parts.length == 4) {


                Account account =
                        bankingService.getAccount(
                                accountNumber
                        );


                sendResponse(
                        exchange,
                        200,
                        accountToJson(account)
                );


                return;
            }


            // ------------------------------------------
            // BALANCE
            // GET /account/id/balance
            // ------------------------------------------

            if (method.equals("GET")
                    && parts.length == 5
                    && parts[4].equals("balance")) {


                double balance =
                        bankingService.checkBalance(
                                accountNumber
                        );


                String response =
                        "{\"balance\":" +
                        balance +
                        "}";


                sendResponse(
                        exchange,
                        200,
                        response
                );


                return;
            }


            // ------------------------------------------
            // PASSBOOK
            // GET /account/id/transactions
            // ------------------------------------------

            if (method.equals("GET")
                    && parts.length == 5
                    && parts[4].equals("transactions")) {


                List<Transaction> transactions =
                        bankingService.getPassbook(
                                accountNumber
                        );


                String response =
                        transactionsToJson(
                                transactions
                        );


                sendResponse(
                        exchange,
                        200,
                        response
                );


                return;
            }


            // ------------------------------------------
            // DEPOSIT
            // POST /account/id/deposit
            // ------------------------------------------

            if (method.equals("POST")
                    && parts.length == 5
                    && parts[4].equals("deposit")) {


                String body =
                        readRequestBody(exchange);


                double amount =
                        Double.parseDouble(
                                getJsonValue(
                                        body,
                                        "amount"
                                )
                        );


                Account account =
                        bankingService.deposit(
                                accountNumber,
                                amount
                        );


                sendResponse(
                        exchange,
                        200,
                        accountToJson(account)
                );


                return;
            }


            // ------------------------------------------
            // WITHDRAW
            // POST /account/id/withdraw
            // ------------------------------------------

            if (method.equals("POST")
                    && parts.length == 5
                    && parts[4].equals("withdraw")) {


                String body =
                        readRequestBody(exchange);


                double amount =
                        Double.parseDouble(
                                getJsonValue(
                                        body,
                                        "amount"
                                )
                        );


                Account account =
                        bankingService.withdraw(
                                accountNumber,
                                amount
                        );


                sendResponse(
                        exchange,
                        200,
                        accountToJson(account)
                );


                return;
            }


            sendResponse(
                    exchange,
                    404,
                    errorJson(
                            "Endpoint not found"
                    )
            );


        } catch (Exception e) {


            sendResponse(
                    exchange,
                    400,
                    errorJson(
                            e.getMessage()
                    )
            );

        }
    }


    // ==================================================
    // READ REQUEST BODY
    // ==================================================

    private static String readRequestBody(
            HttpExchange exchange)
            throws IOException {


        InputStream input =
                exchange.getRequestBody();


        return new String(
                input.readAllBytes(),
                StandardCharsets.UTF_8
        );
    }


    // ==================================================
    // GET VALUE FROM SIMPLE JSON
    // ==================================================

    private static String getJsonValue(
            String json,
            String key) {


        String search =
                "\"" + key + "\"";


        int keyIndex =
                json.indexOf(search);


        if (keyIndex == -1) {

            throw new IllegalArgumentException(
                    "Missing field: " + key
            );
        }


        int colonIndex =
                json.indexOf(
                        ":",
                        keyIndex
                );


        int start =
                colonIndex + 1;


        while (
                start < json.length()
                        &&
                Character.isWhitespace(
                        json.charAt(start)
                )
        ) {

            start++;

        }


        // String value

        if (json.charAt(start) == '"') {


            start++;


            int end =
                    json.indexOf(
                            "\"",
                            start
                    );


            return json.substring(
                    start,
                    end
            );
        }


        // Number value

        int end =
                start;


        while (
                end < json.length()
                        &&
                json.charAt(end) != ','
                        &&
                json.charAt(end) != '}'
        ) {

            end++;

        }


        return json.substring(
                start,
                end
        ).trim();
    }


    // ==================================================
    // ACCOUNT -> JSON
    // ==================================================

    private static String accountToJson(
            Account account) {


        return "{"
                + "\"accountNumber\":\""
                + account.getAccountNumber()
                + "\","

                + "\"name\":\""
                + account.getName()
                + "\","

                + "\"phone\":\""
                + account.getPhone()
                + "\","

                + "\"email\":\""
                + account.getEmail()
                + "\","

                + "\"balance\":"
                + account.getBalance()
                + ","

                + "\"totalDeposit\":"
                + account.getTotalDeposit()
                + ","

                + "\"totalWithdraw\":"
                + account.getTotalWithdraw()
                + ","

                + "\"dailyWithdraw\":"
                + account.getDailyWithdraw()

                + "}";
    }


    // ==================================================
    // TRANSACTIONS -> JSON
    // ==================================================

    private static String transactionsToJson(
            List<Transaction> transactions) {


        StringBuilder json =
                new StringBuilder("[");


        for (int i = 0;
             i < transactions.size();
             i++) {


            Transaction t =
                    transactions.get(i);


            json.append("{")

                    .append("\"date\":\"")
                    .append(t.getDate())
                    .append("\",")

                    .append("\"type\":\"")
                    .append(t.getType())
                    .append("\",")

                    .append("\"amount\":")
                    .append(t.getAmount())
                    .append(",")

                    .append("\"balance\":")
                    .append(t.getBalance())

                    .append("}");


            if (i < transactions.size() - 1) {

                json.append(",");

            }
        }


        json.append("]");


        return json.toString();
    }


    // ==================================================
    // ERROR JSON
    // ==================================================

    private static String errorJson(
            String message) {


        if (message == null) {

            message = "Unknown error";

        }


        return "{\"error\":\""
                + message.replace("\"", "\\\"")
                + "\"}";
    }


    // ==================================================
    // SEND RESPONSE
    // ==================================================

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response)
            throws IOException {


        byte[] bytes =
                response.getBytes(
                        StandardCharsets.UTF_8
                );


        exchange.getResponseHeaders()
                .set(
                        "Content-Type",
                        "application/json"
                );


        exchange.sendResponseHeaders(
                statusCode,
                bytes.length
        );


        OutputStream output =
                exchange.getResponseBody();


        output.write(bytes);

        output.close();
    }


    // ==================================================
    // CORS
    // ==================================================

    private static void addCorsHeaders(
            HttpExchange exchange) {


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Origin",
                        "*"
                );


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Methods",
                        "GET, POST, OPTIONS"
                );


        exchange.getResponseHeaders()
                .set(
                        "Access-Control-Allow-Headers",
                        "Content-Type"
                );
    }
}