package com.clapeyronrobotics.blockchain;

import com.clapeyronrobotics.HashMethods;

import java.util.ArrayList;
import java.util.Date;

/**
 * Список блоков + методы для взаимодействия со списком
 * @author Nikita Kakurnikov
 * @version v0.1 блокчейн хранится в RAM
 */
public class BlockChain {

    /**Список блоков*/
    private ArrayList<Block> blockChain;

    /**Создает новый список блоков, стартуя с hardcoded genesis блока
     */
    public BlockChain() {
        blockChain = new ArrayList<Block>();
        Block genesisBlock = new Block(0,new Date(),new StringBuffer("Genesis block|ClapeyronGo"),"Nopreviousblock");
        if (isValidBlockProtocol(genesisBlock))
            blockChain.add(genesisBlock);
    }

    /**Проверка всех блоков текущего блокчейна на соответствие протоколу,
     * а также проверка previousHash для каждого блока
     */
    public boolean isValidBlockChain() {
        boolean isValidBlockChain = true;
        for(int i = 1; i < blockChain.size(); i++) {
            if (    (!isValidBlockProtocol(blockChain.get(i)))||
                    (!isValidBlockLinks(blockChain.get(i)))
                    ) isValidBlockChain = false;
        }
        return isValidBlockChain;
    }

    /**Добавляет новый блок в конец блокчейна после успешной проверки нового блока
     * @param data данные для нового блока
     */
    public void addLastBlock(StringBuffer data) throws Exception {
        Block newBlock = new Block(blockChain.size(),new Date(),data,blockChain.get(blockChain.size()-1).toString());
        if (    (newBlock.index == blockChain.size())&&
                (isValidBlockProtocol(newBlock))&&
                (isValidBlockLinks(newBlock))
                ) blockChain.add(newBlock);
        else throw new Exception("Error: no correct block data");
    }

    /**Проверка блока на соответствие протоколу*/
    private boolean isValidBlockProtocol(Block block) {
        boolean isValidBlockProtocol = false;
        if (    (block != null)
                ) isValidBlockProtocol = true;
        return isValidBlockProtocol;
    }

    /** Проверка хеша предыдущего блока*/
    private boolean isValidBlockLinks(Block block) {
        boolean isValidBlockLinks = false;
        if (    (block.index >= 1)&&
                (block.previousHash.equals(HashMethods.GetSha256(HashMethods.StringToBytesUTF8(blockChain.get(block.index-1).toString()))))
                ) isValidBlockLinks = true;
        return isValidBlockLinks;
    }

    public ArrayList<Block> getBlockChain() {
        return blockChain;
    }

    public int getBlockChainSize() {
        return blockChain.size();
    }
}
