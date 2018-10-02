import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Optional;

public class Connexion implements Runnable {
    private Socket client;
    Communication communication;

    public Connexion(Socket socket) {
        client = socket;
        communication = new Communication(socket);
    }

    public Optional sourceColl(String _file, String method, int a, int b) {
        File file = FileService.compile(this, FileService.getFile(this, _file));
        return callMethod(file.getName(), method, a, b);
    }

//    public Optional byteColl(String file, String method) {
//        return callMethod(file, method);
//    }

    public Optional receiveObject(String method, int a, int b) {
        try {
            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            Object o = ois.readObject();
            return callMethod(o, method, a, b);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * @param _class The class in plain text
     * @param _method the method to call
     * @return the result of the method
     */
    private Optional callMethod(String _class, String _method, int a, int b) {
        Object o = FileService.getObject(_class.substring(0, _class.indexOf(".")));
        return callMethod(o, _method, a, b);
    }

    /**
     * Right now, we supposed we know the number of parameters, their types and the type return by the method
     * @param o The object called
     * @param _method The method called
     * @return the result of the method
     */
    public Optional<String> callMethod(Object o, String _method, int a, int b) {
        Optional<String> optional;
        Method method = null;
        try {
            method = o.getClass().getMethod(_method, int.class, int.class);
            optional = Optional.of(String.valueOf((int) method.invoke(o, a, b)));
            return optional;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private String getFile() {
        try {
            String file = communication.read();
            String s = communication.read();
            int file_size = Integer.parseInt(s);
            communication.saveFile("serverFiles/clientFiles/" + file, file_size);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    private void sendResponse(Optional<String> optional) {
        String answer;
        answer = optional.orElseGet(Message::getEmptyResult);
        try {
            communication.write(answer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void byteColl() {
        try {
            String file = getFile();
            communication.write(Message.ack());
            String method = communication.read();
            String a = communication.read();
            String b = communication.read();
            int i = Integer.valueOf(a);
            int j = Integer.valueOf(b);
            Optional result = callMethod(file, method, i, j);
            sendResponse(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sourceColl() {
        try {
            String _file = getFile();
            File file = FileService.compile(this, FileService.getFile(this, _file));
            String method = communication.read();
            String a = communication.read();
            String b = communication.read();
            int i = Integer.valueOf(a);
            int j = Integer.valueOf(b);
            Optional result = callMethod(file.getName(), method, i, j);
            sendResponse(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void objectColl() {
        try {
            String method = communication.read();
            communication.write(Message.ack());
            String a = communication.read();
            communication.write(Message.ack());
            String b = communication.read();
            int i = Integer.valueOf(a);
            int j = Integer.valueOf(b);
            Optional result = receiveObject(method, i, j);
            System.out.println(result);
            sendResponse(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        boolean open = true;
        try {
            String choosen = communication.read();
            char c = choosen.charAt(0);
            //If what we received is not what we expect
            while (c != Message.getByteColl().charAt(0) && c != Message.getObjectColl().charAt(0) && c != Message.getSourceColl().charAt(0)) {
                communication.write(Message.getWrongChoice());
                choosen = communication.read();
                c = choosen.charAt(0);
            }
            if ( c== Message.getByteColl().charAt(0)) {
                communication.write(Message.goodChoice());
                byteColl();
            } else if (c == Message.getObjectColl().charAt(0)) {
                communication.write(Message.goodChoice());
                objectColl();
            } else if (c == Message.getSourceColl().charAt(0)) {
                communication.write(Message.goodChoice());
                sourceColl();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        testReceivedFile();
    }

    /**
     * TEST
     */


    public void testReceivedFile() {
        try {
            String string = "";
            string = communication.read();
            communication.write(Message.ack());
            getFile();
            communication.write(Message.ack());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
