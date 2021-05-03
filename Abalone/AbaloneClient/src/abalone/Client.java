/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abalone;

import game.Message;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static abalone.Client.sInput;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Furkan ATAK
 */
// listens messages from server



class Listen extends Thread {
    public void run() {
        //while socket connection is on
        while (Client.socket.isConnected()) {
            try {
                Message received = (Message) (sInput.readObject());
                switch (received.type) {
                    case Name:
                        break;
                    case RivalConnected:
                        String name = received.content.toString();
                        if (name.charAt(0) == 'b') {
                            Game.game.setRival(Rival.pink);
                            
                            System.out.println("enemy came!");
                        }
                        break;
                    case Disconnect:
                        break;
                    case Text:
                        break;
                    case Selected:
                        break;
                    case Send:
                        ArrayList arr = new ArrayList<>();
                        arr = (ArrayList) received.content;
                        Game.game.receiveGameState((Rival[]) arr.get(0), (List<Integer>) arr.get(1), (boolean) arr.get(2));
                        break;
                    case Bitis:
                        break;
                }

            } catch (IOException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                //Client.Stop();
                break;
            }
        }

    }
}

public class Client {

    //every clients need a socket
    public static Socket socket;
    //object to get messages
    public static ObjectInputStream sInput;
    //object to send messages
    public static ObjectOutputStream sOutput;
    //thread for listening server
    public static Listen listenMe;

    public static void Start(String ip, int port) {
        try {
            // Client socket object
            Client.socket = new Socket(ip, port);
            Client.Display("Connected to Server");
            // input stream
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            // output stream
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new Listen();
            Client.listenMe.start();

            Message msg = new Message(Message.Message_Type.Name);
            msg.content = "Player";
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //to stop client
    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();

                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //to send message
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
