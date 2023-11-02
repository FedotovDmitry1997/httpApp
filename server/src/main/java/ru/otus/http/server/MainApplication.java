package ru.otus.http.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApplication {
    public static final int PORT = 8188;
    private static final Logger logger = (Logger) LogManager.getLogger(MainApplication.class);

    // + К домашнему задания:
    // Добавить логирование!!!

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            Map<String, MyWebApplication> router = new HashMap<>();
            router.put("/calculator", new CalculatorWebApplication());
            router.put("/greetings", new GreetingsWebApplication());
//            System.out.println("Сервер запущен, порт: " + PORT);
            logger.info("Сервер запущен, порт: " + PORT);
            ExecutorService serv = Executors.newFixedThreadPool(10);
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    serv.execute(new Handler(socket, router));
                } catch (IOException e) {
//                    e.printStackTrace();
                    logger.error("ошибка потока", e);
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
            logger.error("ошибка сокета", e);
        }
    }
}
