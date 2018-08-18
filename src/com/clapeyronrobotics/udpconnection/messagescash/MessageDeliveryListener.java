package com.clapeyronrobotics.udpconnection.messagescash;

import com.clapeyronrobotics.udpconnection.Message;

import java.net.InetAddress;

/**События, относящиеся к механизму подтверждения доставки собщений.*/
public interface MessageDeliveryListener {
    /**Пришло подтверждение доставки сообщения.*/
    void onDelivered(InetAddress inetAddress, int port, Message message);
    /**Повторная отправка сообщения.*/
    void onDeliveryRepeatSending(InetAddress inetAddress, int port, Message message);
    /**Истек лимит повторных отправок сообщения.*/
    void onDeliveryTimeout(InetAddress inetAddress, int port, Message message);
}
