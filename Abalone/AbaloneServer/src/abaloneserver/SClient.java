/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abaloneserver;
import abalone.Rival;
import game.Message;
import static game.Message.Message_Type.Selected;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.Thread.sleep;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Furkan ATAK
 */
public class SClient {

    int id;
    public String name = "NoName";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    //thread for listening message from client
    Listen listenThread;
    //thread for client pairing
    PairingThread pairThread;
    //opponent client
    SClient rival;
    //pairing status
    public boolean paired = false;

    public SClient(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread objects
        this.listenThread = new Listen(this);
        this.pairThread = new PairingThread(this);

    }

    //client message sending
    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //thread for client listening
    //every client has it's own listening thread
    class Listen extends Thread {

        SClient TheClient;
        Listen(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //while client connected
            while (TheClient.soket.isConnected()) {
                try {
                    Message received = (Message) (TheClient.sInput.readObject());
                    switch (received.type) {
                        case Name:
                            TheClient.name = received.content.toString();
                            TheClient.pairThread.start();
                            break;
                        case Send:
                            Server.Send(TheClient.rival, received);
                            System.out.println("Came");
                            break;
                    }

                } catch (IOException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //delete from the list if client connection lost
                    Server.Clients.remove(TheClient);

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(SClient.class.getName()).log(Level.SEVERE, null, ex);
                    //delete from the list if client connection lost
                    Server.Clients.remove(TheClient);
                }
            }

        }
    }

    //pairing thread
    class PairingThread extends Thread {

        SClient TheClient;

        PairingThread(SClient TheClient) {
            this.TheClient = TheClient;
        }

        public void run() {
            //client has connected and paired
            while (TheClient.soket.isConnected() && TheClient.paired == false) {
                try {
                    //lock mechanism
                    //just one client could get in
                    //others wait till the one is released
                    Server.pairTwo.acquire(1);
                    
                    //get in if client paired
                    if (!TheClient.paired) {
                        SClient crival = null;
                        //while pairing is true
                        while (crival == null && TheClient.soket.isConnected()) {
                            //looking for pair
                            for (SClient clnt : Server.Clients) {
                                if (TheClient != clnt && clnt.rival == null) {
                                    //pairing has been done
                                    crival = clnt;
                                    crival.paired = true;
                                    crival.rival = TheClient;
                                    TheClient.rival = crival;
                                    TheClient.paired = true;
                                    System.out.println("Paired");
                                    break;
                                }
                            }
                            //loop is arranged as loop over in every 1 sec
                            sleep(1000);
                        }
                        //has paired
                        //send both sides pairing info
                        //game started
                        Message msg1 = new Message(Message.Message_Type.RivalConnected);
                        msg1.content = "b" + TheClient.name; // b is for setting opponent color
                        Server.Send(TheClient.rival, msg1);

                        Message msg2 = new Message(Message.Message_Type.RivalConnected);
                        msg2.content = TheClient.rival.name;
                        Server.Send(TheClient, msg2);
                    }
                    //release lock mechanism
                    //if not it's deadlock
                    Server.pairTwo.release(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PairingThread.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
