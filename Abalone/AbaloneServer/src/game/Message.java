/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game;

/**
 *
 * @author INSECT
 */

public class Message implements java.io.Serializable {
    //message types are enum
    public static enum Message_Type {None, Name, Disconnect,RivalConnected, Text, Selected, Bitis,Start,Send}
    //message types 
    public Message_Type type;
    //object type of message to satisfy generic variables
    public Object content;
    public Message(Message_Type t)
    {
        this.type=t;
    }   
    
}
