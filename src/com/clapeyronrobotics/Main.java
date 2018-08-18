package com.clapeyronrobotics;

import com.clapeyronrobotics.blockchain.Block;
import com.clapeyronrobotics.udpconnection.UDPNode;

import java.net.*;
import java.util.Date;
import java.util.Scanner;

public class Main {
    private static BlockChainNode blockChainNode;

    public static void main(String[] args) {
        writeLine("Starts separately udp p2p network and separately RAM blockchain test.");
        writeLine("Your IP is "+AppInfo.LocalIP.getHostAddress());
        try {
            writeLine("Your localhost is "+InetAddress.getByName("localhost"));
        } catch (UnknownHostException e) {
            writeLine("Can't detect your localhost");
            e.printStackTrace();
        }
        writeLine("Print <help> if u don't know what to do:");
        blockChainNode = new BlockChainNode();
        while(true) {
            consoleMenu(consoleReadLine());
        }
    }

    private static void consoleMenu(String task) {
        String[] splitTask = task.split(" ");
        switch (splitTask[0]) {
            case "help":
                writeLine( "newblock <data>: creates the last block with your block data");
                writeLine( "showchain: shows all blockchain blocks");
                writeLine( "startnode <port>: starts local port listening and opportunity for sending messages");
                writeLine( "send <IP> <port> <data>: sends data to IP: port. <data> must be without spaces");
                writeLine( "closenode: closes started UDP socket");
                writeLine( "exit: closes the app");
                break;

            case "newblock":
                try {
                    String data = task.split(" ")[1];
                    blockChainNode.getBlockChain().addLastBlock(new StringBuffer(data));
                    writeLine("new block added "+ blockChainNode.getBlockChain().getBlockChainSize());
                } catch (Exception e) {
                    writeLine("Error: no correct block data");
                    break;
                }
                break;

            case "showchain":
                Block nextBlock;
                for(int i = 0; i < blockChainNode.getBlockChain().getBlockChainSize(); i++) {
                    nextBlock = blockChainNode.getBlockChain().getBlockChain().get(i);
                    writeLine(nextBlock.index+" "+new Date(nextBlock.timestamp*60*1000)+" "+nextBlock.data+" "+nextBlock.previousHash);
                }
                writeLine("blockchain printed");
                break;

            case "addremnode":
                try {
                    String IP = splitTask[1];
                    int port = Integer.parseInt(splitTask[2]);
                    InetAddress hostAddr = InetAddress.getByName(IP);
                    blockChainNode.addNode(hostAddr,port);
                } catch (UnknownHostException e) {
                    writeLine("Error: cant find remote URL");
                } catch (Exception e) {
                    writeLine("Error formatting: no correct IP or port or data");
                    e.printStackTrace();
                }
                break;

            case "startnode":
                try {
                    int inPort = Integer.parseInt(splitTask[1]);
                    blockChainNode.closeUDPNode();
                    blockChainNode = new BlockChainNode(inPort);
                } catch (Exception e) {
                    writeLine("Error formatting: no correct port");
                }
                break;

            case "send":
                try {
                    String outIP = splitTask[1];
                    int outPort = Integer.parseInt(splitTask[2]);
                    String outData = splitTask[3];
                    blockChainNode.sendNewString(outIP, outPort, outData);
                } catch (Exception e) {
                    writeLine("Error formatting: no correct IP or port or data");
                    e.printStackTrace();
                }
                break;

            case "closenode":
                blockChainNode.closeUDPNode();
                break;

            case "exit":
                blockChainNode.closeUDPNode();
                System.exit(0);
                break;

            default:
                writeLine("Command does not exist");
                break;
        }
    }

    public static void writeLine(String data) {
        System.out.println(data);
    }

    public static String consoleReadLine() {
        Scanner in = new Scanner(System.in);
        String read = in.nextLine();
        return read;
    }
}

/*
long startTime = System.currentTimeMillis();
long stopTime = System.currentTimeMillis();
long elapsedTime = stopTime - startTime;
*/
