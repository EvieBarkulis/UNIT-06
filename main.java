import java.util.*;

public class Scenario {


    static int items;
    static int controlItemNumber;
    static int numberOfItems;
    static int totalRooms;
    static int numberOfCustomers;


    public static void main(String[] args) {

        // Using Scanner for Getting Input from User
        //Evie - This lets the user make input
        Scanner in = new Scanner(System.in);

        //ClothingItems = 0 will indicate the use of a random number.
        //ClothingItems = 1 - 20 will allow for load testing by forcing a specific number of items.
        // Evie - This is taking input from user for the number of items.1
        /

        System.out.print("What ClothingItems value do you want? (0 = random)");
        controlItemNumber = in.nextInt();

        //Set the number of customers
        // Evie - This takes the user input for number of customers
        System.out.print("How many customers do you want? ");
        numberOfCustomers = in.nextInt();

        //Set the number of dressing rooms
        // Evie - This takes the number of items for the dressing room
        System.out.println("How many dressing rooms do you want? ");
        totalRooms = in.nextInt();

        //Close the input scanner
        in.close();

        //Create the dressing room object once
        // Evie - This creates the instance of the object DressingRoom with the number of rooms provided by the user
        DressingRoom dr = new DressingRoom(totalRooms);

        //Create threads based on number of customers
        // Evie - This takes the number entered by the user for customers and creates many customer threads at the same time
        // Evie - The customer threads are created with the number of items entered by the user
        // Evie - The .get returns the value of the customer to provide an integer for the numberOfItems
        / Evie - a dressing room object with the number of items in the dressing room.
        for(int i = 0; i < numberOfCustomers; i++) {

            //Create the customer object
            Customer cust = new Customer(controlItemNumber);

            //Get the number of items
            numberOfItems = cust.getNumberOfItems();

            //Track total number of items
            items += numberOfItems;

            //Create the thread
            dr.RequestRoom(numberOfItems);

        }


        //Built in delay waiting for threads to finish
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //Get the average items per customers and print
        int averageItemsPerCustomer = items/numberOfCustomers;
        System.out.println("Average number of items was: " +
                averageItemsPerCustomer);

        //Print the average run time
        System.out.println("Average Run time in nanoseconds  " + dr.getRunTime()/numberOfCustomers);

        //Print the average wait time
        System.out.println("Average Wait time in nanoseconds " + dr.getWaitTime()/numberOfCustomers);

        //Print the number of customers
        System.out.println("Total customers is " + numberOfCustomers);
    }

}

public class Customer {

    int NumberOfItems;

    //Default constructor
    public Customer()

    {
        NumberOfItems = 6;
    }

    //Constructor that takes in ClothingItems
    public Customer(int items)

    {
        int ClothingItems = items;
        //If 0 get random number of items

        if (ClothingItems == 0)
        {
            NumberOfItems = GetRandomNumber(20);
        }

        //Otherwise set to value sent
        else
        {
            NumberOfItems = ClothingItems;
        }

    }

    //Return the number of items

    public int getNumberOfItems()
    {
        return NumberOfItems;
    }

    //Random number methods
    public static int GetRandomNumber(int maxValue)
    {
        int x = 1 + (int) (Math.random() * maxValue );
        return x;

    }

}
public class DressingRoom  implements Runnable {

    int rooms;
    Semaphore semaphore;
    long waitTimer;
    long startWait;
    long endWait;
    long startRun;
    long endRun;
    long runTimer;
    int numberOfItems;
    Thread runner;

    //Default constructor
    public DressingRoom()
    {
        rooms = 3;
        //Set the semaphore object
        semaphore = new Semaphore(rooms);
    }

    //Constructor that accepts number of rooms for simulation
    // Evie - rooms come from Scenerio where input is from the user
    public DressingRoom(int r) {
        rooms = r;
        //Set the semaphore object
        semaphore = new Semaphore(rooms);
    }

    //Run the threads
    // Evie - uses input from the user and sent from Scenerio
    // Evie - Creates the thread for the amount of rooms
    public void RequestRoom(int num) {
        numberOfItems = num;
        //Create thread object
        this.runner = new Thread(this);
        //Start timer and thread
        startRun = System.nanoTime();
        this.runner.start();
    }

    //Random number methods

    public static int GetRandomNumber(int maxValue)
    {
        int x = 1 + (int) (Math.random() * maxValue );
        return x;
    }

    @Override
    public void run() {
        try {
            //Check for connection via semaphore
            // Evie - This section of code looks to see if permits are available for the thread
            // Evie - if available, then it provides the thread to the requestiing code
            semaphore.acquire();
            //End run time
            // Evie - This ends the aount of time it took for the thread to get a permit
            endRun = System.nanoTime();
            runTimer += (endRun - startRun);

            //Get the wait time based on 1-3 minutes per item
            // Evie - This randomizes the time between 1 and 3 to put on the clothing items.
            int wait = GetRandomNumber(3);
            //Print the status
            System.out.println(Thread.currentThread().getName() + " thread waiting");
            //Print finishing statement and release the semaphore reference
            System.out.println(Thread.currentThread().getName() + " thread ran");
            //Start wait time
            // Evie - If the thread has no available permits it must wait
            startWait = System.nanoTime();
            //Sleep based on items and wait time
            // Evie - The wait time is how long it takes to put on clothes times the number of cloths to put on.
            Thread.sleep(wait * numberOfItems );
            //End wait time
            // Evie - The wait time ends and the thread is able to acquire a permit
            endWait = System.nanoTime();
            waitTimer += (endWait - startWait);

            //Release thread
            // Evie - this puts a permit back in availability for other threads to use.
            semaphore.release();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    //Return the total run time
    public long getRunTime() {
        return runTimer;
    }

    //Return the total wait time
    public long getWaitTime() {
        return waitTimer;
    }

}