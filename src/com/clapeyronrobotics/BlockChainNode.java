package com.clapeyronrobotics;

import com.clapeyronrobotics.blockchain.BlockChain;
import com.clapeyronrobotics.udpconnection.Message;
import com.clapeyronrobotics.udpconnection.UDPNode;
import com.clapeyronrobotics.udpconnection.messagescash.MessageDeliveryListener;
import com.clapeyronrobotics.udpconnection.udpsocket.UDPSocket;
import com.clapeyronrobotics.udpconnection.udpsocket.UDPSocketListener;

import java.net.InetAddress;
import java.util.ArrayList;

public class BlockChainNode implements UDPSocketListener,MessageDeliveryListener {
    private BlockChain blockChain;
    private UDPNode udpNode;
    private int defaultPort = 8333;
    private ArrayList<RemoteNodeInfo> nodesList = new ArrayList<>();
    private ArrayList<RemoteNodeInfo> dnsNodesList = new ArrayList<>();

    public BlockChainNode() {
        blockChain = new BlockChain();
        udpNode = new UDPNode(defaultPort,this,this);
    }

    public BlockChainNode(int inPort) {
        blockChain = new BlockChain();
        udpNode = new UDPNode(inPort,this,this);
    }

    public void sendNewString(String outIP,int outPort,String outData) {
        udpNode.sendNewString(outIP, outPort, outData);
    }

    public void closeUDPNode() {
        udpNode.closeNode();
    }

    public ArrayList<RemoteNodeInfo> getNodeList() {
        return nodesList;
    }

    public void addNode(InetAddress inetAddress, int port) {
        nodesList.add(new RemoteNodeInfo(inetAddress,port));
    }

    public void nodesListUpdate() {

    }

    private void checkNode() {

    }

    public void initialBlockChainDownload() {

    }

    public void consensus() {

    }

    public void blockBroadcasting() {

    }

    public BlockChain getBlockChain() {
        return blockChain;
    }





    @Override
    public void onSocketListeningReady(UDPSocket udpSocket) {

    }

    @Override
    public void onSocketMessageReceived(InetAddress authorIP, int authorPort, String receivedString) {

    }

    @Override
    public void onSocketMessageSent(InetAddress outIP, int outPort, Message data) {

    }

    @Override
    public void onSocketListeningClosed(UDPSocket udpSocket) {

    }

    @Override
    public void onSocketCreationException(UDPSocket udpSocket, String excMsg) {
        udpNode = new UDPNode(udpSocket.getLocalPort()+1,this,this);
        //getLocalPort - NULL
    }

    @Override
    public void onSocketReceivingException(UDPSocket udpSocket, String excMsg) {

    }

    @Override
    public void onSocketListeningException(UDPSocket udpSocket) {

    }

    @Override
    public void onSocketMessageIsNotSentLocalNodeIsClosed() {

    }

    @Override
    public void onSocketMessageIsNotSentCantFindRemoteURL(String outIP) {
        //TODO delete remote node from list
    }

    @Override
    public void onSocketMessageIsNotSentIOException() {

    }

    @Override
    public void onDelivered(InetAddress inetAddress, int port, Message message) {

    }

    @Override
    public void onDeliveryRepeatSending(InetAddress inetAddress, int port, Message message) {

    }

    @Override
    public void onDeliveryTimeout(InetAddress inetAddress, int port, Message message) {

    }

    public class RemoteNodeInfo {
        InetAddress IP;
        int port;

        boolean online;

        RemoteNodeInfo(InetAddress inetAddress, int port) {
            this.IP = inetAddress;
            this.port = port;

            online = false;
        }
    }
}
