package ru.otus.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

class Handler implements Runnable {
    private static final Logger logger = (Logger) LogManager.getLogger(Handler.class);

    private final Socket socket;
    private Map<String, MyWebApplication> router;
    public Handler(Socket socket, Map<String, MyWebApplication> router) {
        this.socket = socket;
        this.router = router;
    }
    @Override
    public void run() {
        try {
//            System.out.println("Клиент подключился");
            logger.info("Клиент подключился");
            byte[] buffer = new byte[2048];
            int n = socket.getInputStream().read(buffer);
            String rawRequest = new String(buffer, 0, n);
            Request request = new Request(rawRequest);

//            System.out.println("Получен запрос:");
            logger.info("Получен запрос:");
            request.show();
            boolean executed = false;
            for (Map.Entry<String, MyWebApplication> e : router.entrySet()) {
                if (request.getUri().startsWith(e.getKey())) {
                    e.getValue().execute(request, socket.getOutputStream());
                    executed = true;
                    break;
                }
            }
            if (!executed) {
                socket.getOutputStream().write(("HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<html><body><h1>Unknown application</h1></body></html>").getBytes(StandardCharsets.UTF_8));
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
