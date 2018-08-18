package com.clapeyronrobotics.udpconnection;

import com.clapeyronrobotics.Main;
import com.clapeyronrobotics.AppInfo;
import com.clapeyronrobotics.udpconnection.messagescash.MessageDeliveryListener;
import com.clapeyronrobotics.udpconnection.messagescash.MessagesCash;
import com.clapeyronrobotics.udpconnection.udpsocket.UDPSocket;
import com.clapeyronrobotics.udpconnection.udpsocket.UDPSocketListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Надстройка над виртуальным udp сокетом (UDPSocket.java).
 * Обеспечивает протокольную обертку сообщений,
 * механизм подтверждений доставки сообщений,
 * обработку событий из udp-сокета.
 */
public class UDPNode implements UDPSocketListener,MessageDeliveryListener {
    /**Виртуальный UDP socket для прослушки порта на входящие сообщения и отправки сообщений с этого порта.*/
    private static UDPSocket udpSocket;
    /**Список сообщений, ожидающих доставку. Доступ рекомендуется только с помощью synchronized (специально для этого есть getMessageCash()).*/
    private MessagesCash messagesCash = new MessagesCash(this);

    private UDPSocketListener blockChainNodeUDPSocketListener;
    private MessageDeliveryListener blockChainNodeMessageDeliveryListener;

    /**Одновременно запускает прослушку порта.*/
    public UDPNode(int inPort, UDPSocketListener blockChainNodeUDPSocketListener,MessageDeliveryListener blockChainNodeMessageDeliveryListener) {
        this.blockChainNodeUDPSocketListener = blockChainNodeUDPSocketListener;
        this.blockChainNodeMessageDeliveryListener = blockChainNodeMessageDeliveryListener;
        startNode(inPort);
    }

    /**Запуск прослушки порта.
     * @param inPort - порт для прослушки
     */
    private void startNode(int inPort) {
        udpSocket = new UDPSocket(inPort,this);
    }

    /**Закрытие любой возможности общения через эту ноду (UDPNode.java)*/
    public void closeNode() {
        udpSocket.closeNode();
    }

    /**
     * Отправка вашей строки. Будет создано новое сообщение в спике ожиающих подтверждения доставки.
     * @param outIP - IP адресата
     * @param outPort - порт адресата
     * @param data - строка для отправки
     */
    public void sendNewString(String outIP, int outPort, String data) {
        try {
            InetAddress hostAddr = InetAddress.getByName(outIP);
            Message message = new Message(getMessagesCash().getCurrentID(hostAddr,outPort),data);
            udpSocket.sendMessage(hostAddr,outPort,message);
        } catch (UnknownHostException e) {
            onSocketMessageIsNotSentCantFindRemoteURL(outIP);
            e.printStackTrace();
        }
    }

    /**
     * Отправка форматированного сообщения (Message.java).
     * @param outIP - IP адресата
     * @param outPort - порт адресата
     * @param message - форматированное сообщение для отправки
     */
    private synchronized void sendMessage(InetAddress outIP, int outPort, Message message) {
        udpSocket.sendMessage(outIP,outPort,message);
    }

    @Override
    public void onSocketListeningReady(UDPSocket udpSocket) {
        Main.writeLine("Listening started on localhost: " + udpSocket.getLocalPort() +
                " and on " + AppInfo.LocalIP.getHostAddress() + ": " + udpSocket.getLocalPort());
    }

    @Override
    public void onSocketMessageReceived(InetAddress authorIP, int authorPort, String receivedString) {
        ArrayList<String> processedMsg = parseMsg(receivedString);
        Main.writeLine("RECEIVED from " + authorIP + ":" + authorPort + "| data: " + processedMsg);
        for (int i = 0; i < processedMsg.size(); i++) {
            Message message = Message.fromString(processedMsg.get(i));
            if (message != null)
                if (message.isRequest()) {
                    //отправить ответ
                    message.makeThisAnswer();
                    sendMessage(authorIP, authorPort, message);
                    //TODO обработать полученные данные
                } else
                    if (message.isAnswer())
                        //удалить из ожидания
                        getMessagesCash().removeMessageByAnswer(authorIP,authorPort,message);
        }
    }

    @Override
    public void onSocketMessageSent(InetAddress outIP, int outPort, Message message) {
        getMessagesCash().addMessage(outIP,outPort,message); //добавит, если это REQ
        Main.writeLine("UDP socket log: Message sent");
    }

    @Override
    public void onSocketListeningClosed(UDPSocket udpSocket) {
        Main.writeLine("UDP socket log: UDP listening is closed");
    }

    @Override
    public void onSocketCreationException(UDPSocket udpSocket, String excMsg) {
        Main.writeLine("UDP socket error: cant create local socket =>\n=>" + excMsg);
        blockChainNodeUDPSocketListener.onSocketCreationException(udpSocket,excMsg); //NULL
    }

    @Override
    public void onSocketReceivingException(UDPSocket udpSocket, String excMsg) {
        Main.writeLine("UDP socket error: cant receive msg =>\n=>" + excMsg);
    }

    @Override
    public void onSocketListeningException(UDPSocket udpSocket) {
        Main.writeLine("UDP socket error: UDP listening is closed UNEXPECTEDLY");
    }

    @Override
    public void onSocketMessageIsNotSentLocalNodeIsClosed() {
        Main.writeLine("UDP socket error: cant send message, please, start node");
    }

    @Override
    public void onSocketMessageIsNotSentCantFindRemoteURL(String outIP) {
        Main.writeLine("UDP socket error: cant find remote URL to send");
        blockChainNodeUDPSocketListener.onSocketMessageIsNotSentCantFindRemoteURL(outIP);
    }

    @Override
    public void onSocketMessageIsNotSentIOException() {
        Main.writeLine("UDP socket error: cant send data, try again");
    }

    @Override
    public void onDelivered(InetAddress inetAddress, int port, Message message) {

    }

    @Override
    public void onDeliveryRepeatSending(InetAddress inetAddress, int port, Message message) {
        sendMessage(inetAddress,port,message);
    }

    @Override
    public void onDeliveryTimeout(InetAddress inetAddress, int port, Message message) {
        getMessagesCash().removeMessageByOrigin(inetAddress,port,message);
        Main.writeLine("Message hasn't delivered, timeout has expired: "+inetAddress+":"+port+"|"+message);
    }

    /**
     * Parses String through "?" splitting. For example:
     * data is "d1?d2?d3?d4" => result is array of received messages ["d2","d3"]
     */
    private ArrayList<String> parseMsg(String msg) {
        int msglen = msg.length();
        ArrayList<String> msges = new ArrayList<>();
        if ((msglen==0)||(msglen==1)) return msges;
        String[] spltMsg = msg.split("\\?");
        int startCounter = 0;
        int endCounter = spltMsg.length;
        if (!msg.substring(0).equals("?")) startCounter = 1;
        if (!msg.substring(msglen-1).equals("?")) endCounter -= 1;
        for(int i = startCounter; i < endCounter; i++)
            if(!spltMsg[i].equals("")) msges.add(spltMsg[i]);
        return msges;
    }

    private synchronized MessagesCash getMessagesCash() {
        return messagesCash;
    }
}
