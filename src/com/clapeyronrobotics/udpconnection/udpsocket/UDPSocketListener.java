package com.clapeyronrobotics.udpconnection.udpsocket;

import com.clapeyronrobotics.udpconnection.Message;

import java.net.InetAddress;

/**
 * События, относящиеся к созданию udp-сокета, прослушке входящих подключений на заданном порту,
 * отправке сообщениий с заданного порта.
 */
public interface UDPSocketListener {

    /**UDPSocket начал прослушку заданного порта.*/
    void onSocketListeningReady(UDPSocket udpSocket);

    /**Получено сообщение на заданный в UDPSocket порт.*/
    void onSocketMessageReceived(InetAddress authorIP, int authorPort, String receivedString);

    /**С заданного в UDPSocket порта отправлено сообщение.*/
    void onSocketMessageSent(InetAddress outIP, int outPort, Message data);

    /**
     * Это событие может вызваться ТОЛЬКО (я, блять, очень надеюсь) при ручном закрытии udpSocket.
     * Т.е. вызова netProtocol.closeNode().
     */
    void onSocketListeningClosed(UDPSocket udpSocket);

    /**
     * udpSocket != null, но отправлять сообщения не получится, т.к.
     * udpSocket.localSocket.isClosed() == true, а также
     * udpSocket.isAlive() == false
     */
    void onSocketCreationException(UDPSocket udpSocket, String excMsg);

    /**
     * Вызывается только если прослушка должна продолжиться, т.е. это событие не означает закрытие сокета.
     */
    void onSocketReceivingException(UDPSocket udpSocket, String excMsg);

    /**
     * Внезапная неожидаемая остановка прослушки.
     */
    void onSocketListeningException(UDPSocket udpSocket);

    /**
     * Закрыли ноду перед отправкой, либо уже было вызвано onSocketCreationException.
     */
    void onSocketMessageIsNotSentLocalNodeIsClosed();

    /**
     * Что-то не так с адресом доставки.
     */
    void onSocketMessageIsNotSentCantFindRemoteURL(String outIP);

    /**
     * Что-то не так с localSocket.send(), наверное, стоит еще раз попытаться отправить сообщение.
     */
    void onSocketMessageIsNotSentIOException();
}
