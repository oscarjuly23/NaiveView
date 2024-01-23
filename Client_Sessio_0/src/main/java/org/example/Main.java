package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {

    static OutputStream out = null;
    static BufferedReader in = null;
    static int value = 0;

    public static void main(String[] args) {
        System.out.println("Sessio 0: Client");

        Boolean isReadOnlyClient = isReadOnly();
        try {
            Socket clientSocket = new Socket("localhost", 12000);

            out = clientSocket.getOutputStream();
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {

                getCurrentValue();
                System.out.println("Send: Get value: " + value);

                if (!isReadOnlyClient) {
                    updateCurrentValue();
                    System.out.println("Send: Update value: " + value);
                }

                TimeUnit.SECONDS.sleep(2);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static void updateCurrentValue() throws IOException, InterruptedException {
        String response = "KO";
        String str = "";

        while (response.contains("KO")) {
            str = "Update";
            out.write(str.getBytes());

            response = in.readLine();
        }

        getCurrentValue();

        value++;

        str = String.valueOf(value);
        out.write(str.getBytes());
    }

    private static void getCurrentValue() throws IOException, InterruptedException {
        out.write("getValue".getBytes());
        String response = in.readLine();
        value = Integer.parseInt(response);
    }

    private static Boolean isReadOnly() {
        System.out.println("Select:");
        System.out.println("\t1. Read only client");
        System.out.println("\t2. Read and modify client");

        Scanner sc = new Scanner(System.in);
        int x = sc.nextInt();

        return x == 1;
    }

}