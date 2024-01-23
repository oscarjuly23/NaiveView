package org.example;

import java.net.*;
import java.util.Set;
import java.util.Iterator;
import java.nio.channels.*;
import java.nio.ByteBuffer;


public class Main {

    public static int value = 0;
    public static boolean isUpdating = false;
    public static String clientIP = "";

    public static void main (String[] args) {

        System.out.println ("Sessio 0: Servidor");

        try {
            ServerSocketChannel s;
            Selector sel;

            s = ServerSocketChannel.open();
            s.socket().bind(new InetSocketAddress(12000));

            sel = Selector.open();
            s.configureBlocking(false);
            s.register(sel, SelectionKey.OP_ACCEPT);

            while(true) {
                int n = sel.select (100);
                if (n == 0) continue;

                Set<SelectionKey> keys = sel.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    if (key.isAcceptable()) {
                        System.out.println ("! Connection accepted");
                        ServerSocketChannel ss =(ServerSocketChannel) key.channel();
                        SocketChannel sc = ss.accept();
                        sc.configureBlocking (false);
                        sc.register (sel, SelectionKey.OP_READ);
                        System.out.println ("+ Connection from " + sc.getRemoteAddress());
                    } else if (key.isReadable()) {
                        SocketChannel sc =(SocketChannel) key.channel();
                        ByteBuffer buf = ByteBuffer.allocate (2048);
                        int len = sc.read (buf);

                        if (len <= 0) {
                            System.out.println ("! Error Reading from " +  sc.getRemoteAddress());
                            System.out.println ("+ Closing Connection");
                            key.cancel();
                            sc.close();
                            break;
                        }

                        buf.flip();

                        byte[] msg = new byte [len];
                        buf.get(msg);
                        String   desc = new String (msg).trim();
                        System.out.println ("RECV ("+len+" bytes) >> "+ desc);

                        ByteBuffer obuf = null;
                        int valLen = 0;

                        if (isUpdating && !desc.contains("getValue") && clientIP.equals(sc.getRemoteAddress().toString())) {
                            value = Integer.parseInt(desc);
                            isUpdating = false;
                            clientIP = "";
                        } else {
                            if (desc.contains("getValue")) {
                                valLen = String.valueOf(value).length() + 1;
                                obuf = ByteBuffer.allocate(valLen);
                                obuf.put(String.valueOf(value).getBytes());
                                obuf.put("\n".getBytes());
                            } else if (desc.contains("Update")) {
                                obuf = ByteBuffer.allocate(3);
                                if (isUpdating) {
                                    obuf.put("KO\n".getBytes());
                                } else {
                                    isUpdating = true;
                                    clientIP = sc.getRemoteAddress().toString();
                                    obuf.put("OK\n".getBytes());
                                }
                            } else {
                                obuf = ByteBuffer.allocate(0);
                            }
                            obuf.flip();
                            sc.write(obuf);
                            buf.clear();
                        }
                    }
                    it.remove();
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}