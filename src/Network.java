import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

class Router {
	
	
    ArrayList<Device> connections = new ArrayList<Device>();

    public  void addConnection(Device device) {
        connections.add(device);
    }
    
    public void occupy(Device device, Semaphore semaphore) throws IOException { 
       semaphore.decrement(device);
        
    }
    
    public void release(Device device, Semaphore semaphore) {
    	
        semaphore.increment(device);        
    }   
    ArrayList<Device> getConnections(){
    	
    	return connections;
    } 
}



class Semaphore {
    
    int value = 0;   
    String message ;
    FileWriter file;
    int []connectedDevices;
	public int maxConnections, currConnection;
    
    Device nullDevice;
    Router router;
    public Semaphore (int count) {
        this.value = count;  
        this.maxConnections = count;
    	connectedDevices = new int[maxConnections];
    } 
    
    
    public synchronized void decrement(Device device) throws IOException {
        value--;
        
        if(value >= 0) {
    
        	message = device.name + " (" + device.type + ") arrived" ;
        	System.out.println(message);
        	file = new FileWriter("data.txt", true);
            file.append(message + System.lineSeparator());
            file.close();
        	
        }
        if (value < 0) {
            try {
            	message = device.name + " (" + device.type + ") arrived and waiting" ;
            	System.out.println(message);
            	file = new FileWriter("data.txt", true);
                file.append(message + System.lineSeparator());
                file.close();            	
                wait();
                
            } catch (InterruptedException e) {

            }
        }
        for(int i = 0; i < maxConnections; i++) {
    		if(connectedDevices[i] == 0) {
    			
    			device.id = i + 1;
    			currConnection++;
    			connectedDevices[i] = 1;
    			break;
    		}
    	}
    }
    
    public synchronized void increment(Device device) {
        value++;
        currConnection--;
        connectedDevices[device.id - 1] = 0;
        if (value <= 0) {       	
            notify();            
        }
    
    }
}

class Device extends Thread {
    
    String name;
    String message = "";
    FileWriter file;
    public String getDeviceName() {
		return name;
	}
    int id;
	String type;
	Semaphore connections;
	Router router;
    
    public Device (String name, String type, Semaphore connections, Router router) {
        this.name = name;
        this.type = type;
        this.connections = connections;
        this.router = router;
        id = 1;
    }

    public void run() {
    	
    	try {
			router.occupy(this, connections);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
    	message = "Connection " + this.id + ": "   + name + " is occupied";
    	System.out.println(message);
    	try {
            file = new FileWriter("data.txt", true);
            file.append(message + "\n");
			file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    	  	
    	try {
			sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	message = "Connection " + this.id + ": " + name + " login";
    	System.out.println(message);
    	try {
            file = new FileWriter("data.txt", true);
            file.append(message + "\n");
			file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
    	try {
			sleep(3000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	message = "Connection " + this.id + ": " + name + " performs online activity";
    	System.out.println(message);
    	try {
            file = new FileWriter("data.txt", true);
            file.append(message + "\n");
			file.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

                
    	try {
			sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	message = "Connection " + this.id + ": " + name + " logged out";
    	System.out.println(message);
    	try {
            file = new FileWriter("data.txt", true);
            file.append(message + System.lineSeparator());
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    	  	
    	router.release(this, connections);    	    	
    }
}

public class Network {
    
    public static void main(String[] args) throws InterruptedException, IOException {
        Router router = new Router();
        int maxConnections = 0;
        int totalConnections = 0;
        String name;
        String type;
        FileWriter files = new FileWriter("data.txt", true);

        Scanner scanner = new Scanner(System.in);
        
        System.out.println("What is the number of WI-FI Connections?");
        maxConnections = scanner.nextInt();
        
        Semaphore connections = new Semaphore(maxConnections);
        
        System.out.println("What is the number of devices Clients want to connect?");
        totalConnections = scanner.nextInt();
        
        //create a thread for each device and add it to touter's connections
        for (int i = 0; i < totalConnections; i++) {
            name = scanner.next();
            type = scanner.next();
            Device device = new Device(name, type, connections, router);
            router.addConnection(device);                                 
        }
        for(Device device : router.connections) {
        	device.start();
        	
        }        
        
        scanner.close();
        files.close();

    }

}