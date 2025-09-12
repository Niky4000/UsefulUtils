package ru.selsup.selsup;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Headers;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.sql.DataSource;

public class CrptApi {

    private TimeUnit timeUnit;
    private int requestLimit;
    private String databasePath;
    private String jdbcUrl;
    private String username;
    private String password;

    private static final int BUFFER_SIZE = 1024;
    private static final String ID = "id=";

    public CrptApi(TimeUnit timeUnit, int requestLimit) {
        this.timeUnit = timeUnit;
        this.requestLimit = requestLimit;
        databasePath = FileUtils.getPathToJar().getParentFile().getParent() + File.separator + "myNewDatabase";
        jdbcUrl = "jdbc:h2:" + databasePath;
        username = "sa";
        password = "";
    }

    Map<String, Consumer<HttpServerExchange>> handlerMap = Map.of("/document", exchange -> {
        try (InputStream inputStream = exchange.getInputStream()) {
            String receivedData = readData(inputStream);
            Document document = new ObjectMapper().readValue(receivedData, Document.class);
            insertDocument(document);
            writeHttpResponse(exchange);
        } catch (IOException e) {
            writeErrorHttpResponse(exchange, e);
        }
    }, "/getDocument", exchange -> {
        String queryString = exchange.getQueryString();
        if (queryString != null && queryString.startsWith(ID)) {
            try {
                Long id = Long.valueOf(queryString.substring(queryString.indexOf(ID) + ID.length()));
                Document document = getDocument(id);
                writeHttpDocumentResponse(exchange, document);
            } catch (Exception e) {
                writeErrorHttpResponse(exchange, e);
            }
        }
        writeHttpResponse(exchange);
    });

    public static void main(String[] args) throws Exception {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 10);
        crptApi.startServer();
    }

    public void startServer() throws InterruptedException {
        createDatabase();
        final TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(timeUnit, requestLimit);
        Undertow server = Undertow.builder()
                .addHttpListener(8080, "0.0.0.0")
                .setHandler(new BlockingHandler(exchange -> {
                    if (limiter.tryAcquire()) {
                        String requestPath = exchange.getRequestPath();
                        System.out.println("requestPath: " + requestPath);
                        handlerMap.getOrDefault(requestPath, requestExchange -> {
                            requestExchange.getResponseHeaders()
                                    .put(Headers.CONTENT_TYPE, "text/html");
                            requestExchange.getResponseSender()
                                    .send("<html><head><title>Hello!</title></head>"
                                            + "<body>"
                                            + "<h1>Hello World!</h1>"
                                            + "<h2>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "</h2>"
                                            + "</body>"
                                            + "</html>");
                        }).accept(exchange);
                    } else {
                        System.out.println("Request denied!");
                        exchange.getResponseSender().close();
                    }
                })).
                build();
        server.start();
    }

    private String readData(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead;
        StringBuilder requestBody = new StringBuilder();
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            requestBody.append(new String(buffer, 0, bytesRead, StandardCharsets.UTF_8));
        }
        String receivedData = requestBody.toString();
        System.out.println("Received POST data: " + receivedData);
        return receivedData;
    }

    private void writeHttpResponse(HttpServerExchange exchange) {
        exchange.getResponseHeaders()
                .put(Headers.CONTENT_TYPE, "text/html");
        exchange.getResponseSender()
                .send("<html><head><title>Ok!</title></head>"
                        + "<body>"
                        + "<h1>Document was received!</h1>"
                        + "<h2>" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "</h2>"
                        + "</body>"
                        + "</html>");
    }

    private void writeHttpDocumentResponse(HttpServerExchange exchange, Document document) throws JsonProcessingException {
        exchange.getResponseHeaders()
                .put(Headers.CONTENT_TYPE, "text/html");
        exchange.getResponseSender()
                .send("<html><head><title>Ok!</title></head>"
                        + "<body>"
                        + "<h1>Document was received!</h1>"
                        + "<h2>" + (document != null ? new ObjectMapper().writeValueAsString(document) : "There is no such document!") + "</h2>"
                        + "</body>"
                        + "</html>");
    }

    private void writeErrorHttpResponse(HttpServerExchange exchange, Exception e) {
        e.printStackTrace();
        exchange.setStatusCode(500);
        exchange.getResponseSender().send("Error processing request.");
    }

    private void insertDocument(Document document) {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                PreparedStatement statement = connection.prepareStatement("insert into documents(id,content,signature) values(?,?,?)");) {
            connection.setAutoCommit(false);
            statement.setLong(1, document.getId());
            statement.setString(2, document.getContent());
            statement.setString(3, document.getSignature());
            try {
                statement.execute();
                connection.commit();
            } finally {
                statement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Document getDocument(Long id) throws SQLException {
        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
                PreparedStatement statement = connection.prepareStatement("select id,content,signature from documents where id=?");) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long docId = resultSet.getLong("id");
                    String content = resultSet.getString("content");
                    String signature = resultSet.getString("signature");
                    return new Document(id, content, signature);
                } else {
                    return null;
                }
            }
        }
    }

    private static void insertDocContent(DataSource dataSource, String uuid, String content) throws SQLException {
        try (Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement("insert into doc_content (uuid,content,create_date) values(?,?,?)");) {
            connection.setAutoCommit(false);
            statement.setString(1, uuid);
            statement.setString(2, content);
            statement.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
            try {
                statement.execute();
                connection.commit();
            } finally {
                statement.close();
            }
        }
    }

    private void createDatabase() {
        if (!new File(databasePath + ".mv.db").exists()) {
            try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
                    PreparedStatement statement = conn.prepareStatement("create table documents(id long, content text, signature text, primary key (id))");) {
                System.out.println("H2 database file created successfully at: " + jdbcUrl);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    static class TokenBucketRateLimiter {

        private final long capacity;
        private final long refillRate;
        private long currentTokens;
        private long lastRefillTimestamp;

        public TokenBucketRateLimiter(TimeUnit timeUnit, int requestLimit) {
            long requestsPerSecond = timeUnit.convert(requestLimit, TimeUnit.SECONDS);
            if (requestsPerSecond <= 0) {
                throw new RuntimeException("RequestLimit value is incorrect!");
            }
            this.capacity = requestsPerSecond;
            this.refillRate = requestsPerSecond;
            this.currentTokens = capacity;
            this.lastRefillTimestamp = System.nanoTime();
        }

        public synchronized boolean tryAcquire() {
            refillTokens();
            if (currentTokens > 0) {
                currentTokens--;
                return true;
            }
            return false;
        }

        private void refillTokens() {
            long now = System.nanoTime();
            long timeElapsed = now - lastRefillTimestamp;
            if (timeElapsed > 0) {
                long tokensToAdd = (timeElapsed * refillRate) / TimeUnit.SECONDS.toNanos(1);
                if (tokensToAdd > 0) {
                    currentTokens = Math.min(capacity, currentTokens + tokensToAdd);
                    lastRefillTimestamp = now;
                }
            }
        }
    }

    static class Document {

        private Long id;
        private String content;
        private String signature;

        public Document() {
        }

        public Document(Long id, String content, String signature) {
            this.id = id;
            this.content = content;
            this.signature = signature;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
    }

    static class FileUtils {

        private static final String JAR = "jar:file:";
        private static final String FILE = "file:";
        private static final String EXCLAMATION = "!";

        public static File getPathToJar() {
            try {
                File file = new File(handleUri(FileUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().toString()));
                if (file.getAbsolutePath().contains("classes")) { // Launched from debugger!
                    file = Arrays.stream(file.getParentFile().listFiles()).filter(localFile -> localFile.getName().endsWith(".jar")).findFirst().get();
                }
                return file;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static String handleUri(String uri) {
            return removeBefore(removeBefore(removeAfter(uri, EXCLAMATION), JAR), FILE);
        }

        private static String removeBefore(String str, String whatToRemove) {
            if (str.contains(whatToRemove)) {
                return str.substring(str.indexOf(whatToRemove) + whatToRemove.length());
            } else {
                return str;
            }
        }

        private static String removeAfter(String str, String whatToRemove) {
            if (str.contains(whatToRemove)) {
                return str.substring(0, str.indexOf(whatToRemove));
            } else {
                return str;
            }
        }
    }
}
