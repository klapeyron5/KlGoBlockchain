package com.clapeyronrobotics.blockchain;

import com.clapeyronrobotics.HashMethods;

import java.util.Date;

public class Block {

    public final int index;

    /**UNIX timestamp в минутах*/
    public final long timestamp; //TODO требуется оптимизировать до unsigned типа

    public final StringBuffer data;

    public final String previousHash;

    /**
     * @param index индекс блока в цепи
     * @param timestamp java.util.Date формат
     * @param data пока что строковые данные в блоке
     * @param previousBlockToString предыдущий блок, преобразованный в строку
     * */
    public Block(int index, Date timestamp, StringBuffer data, String previousBlockToString) {
        this.index = index;
        this.timestamp = timestamp.getTime()/1000/60;
        this.data = data;
        this.previousHash = HashMethods.GetSha256(HashMethods.StringToBytesUTF8(previousBlockToString));
    }

    /**
     * @param index индекс блока в цепи
     * @param timestamp UNIX timestamp in minutes only
     * @param data пока что строковые данные в блоке
     * @param previousBlockToString предыдущий блок, преобразованный в строку
     * */
    public Block(int index, long timestamp, StringBuffer data, String previousBlockToString) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.previousHash = HashMethods.GetSha256(HashMethods.StringToBytesUTF8(previousBlockToString));
    }

    @Override
    public String toString() {
        return Integer.toString(index)+Long.toString(timestamp)+data+previousHash;
    }
}
