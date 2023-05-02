package com.company;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        ProcessClass p1 = new ProcessClass(new int[] { 6, 41, 5, 42, 7, 40, 8, 38, 6, 44, 5, 41, 9, 31, 7, 43, 8 }, "P1");
        ProcessClass p2 = new ProcessClass(new int[]{ 9, 24, 7, 21, 8, 36, 12, 26, 9, 31, 11, 28, 8, 21, 12, 13, 7, 11, 6 }, "P2");
        ProcessClass p3 = new ProcessClass(new int[]{ 7, 21, 8, 25, 12, 29, 6, 26, 8, 33, 9, 22, 6, 24, 4, 29, 16 }, "P3");
        ProcessClass p4 = new ProcessClass(new int[]{ 5, 35, 7, 41, 14, 45, 4, 51, 9, 61, 10, 54, 11, 82, 5, 77, 3 }, "P4");
        ProcessClass p5 = new ProcessClass(new int[]{ 6, 33, 7, 44, 5, 42, 9, 37, 8, 46, 5, 41, 7, 31, 4, 43, 3 }, "P5");
        ProcessClass p6 = new ProcessClass(new int[]{ 8, 24, 12, 21, 11, 36, 12, 26, 9, 31, 19, 28, 10, 21, 6, 13, 3, 11, 4 }, "P6");
        ProcessClass p7 = new ProcessClass(new int[]{ 7, 46, 3, 41, 12, 42, 8, 21, 4, 32, 6, 19, 12, 33, 10 }, "P7");
        ProcessClass p8 = new ProcessClass(new int[]{ 6, 14, 7, 33, 8, 51, 9, 63, 10, 87, 11 , 74, 8 }, "P8");
        ProcessClass p9 = new ProcessClass(new int[]{ 4, 32, 5, 40, 6, 29, 4, 21, 5, 44, 6, 24, 4, 31, 5, 33, 6 }, "P9");
        Scheduler s = new Scheduler();
        s.addProcess(p1);
        s.addProcess(p2);
        s.addProcess(p3);
        s.addProcess(p4);
        s.addProcess(p5);
        s.addProcess(p6);
        s.addProcess(p7);
        s.addProcess(p8);
        s.addProcess(p9);
        s.run();
    }
}


 class ProcessClass {
    public int turnaroundTime;
    public int responseTime;
    public int waitTime;
    public String name;
    public ArrayList<Integer> burstTimes; //Splitting the given burst & io times for each process into different arrays making it easier to manage the indexes
    public ArrayList<Integer> ioTimes;
    public int arrivalTime;
    public int burstIndex;
    public int ioIndex;
    public boolean hasEnteredOnce;
    public int completedTime;
    public int sumOfData;
    public ProcessClass(int[] data, String name){
        this.responseTime= 0;
        this.turnaroundTime= 0;
        this.waitTime= 0;
        this.arrivalTime = 0;
        this.completedTime = 0;
        this.burstIndex = 0;
        this.burstTimes = new ArrayList<>();
        this.ioTimes = new ArrayList<>();
        this.hasEnteredOnce = false;
        this.name = name;
        for(int i=0; i < data.length; i++){
            if( i % 2 ==0){
                burstTimes.add(data[i]);
            }else{
                ioTimes.add(data[i]);
            }
        }
        for(int i=0; i < data.length; i++){
            sumOfData += data[i];
        }
    }
    public boolean burstCompleted(){                // Two different methods for checking if the process is done with its bursts and IO times
        return burstIndex >= burstTimes.size();
    }
    public boolean ioCompleted(){
        return ioIndex >= ioTimes.size();
    }
    public boolean isCompleted(){
        return burstCompleted() && ioCompleted();   // Returns if both the bursts & IO times are fully done thus marking the Process as complete
    }

    @Override
     public String toString(){
        return "Process " + this.name + "{ Arrival: " + arrivalTime + ", Current Burst Index: " + burstIndex + ", Completed Time: " + completedTime;
    }
}

class Scheduler{
    private ArrayList<ProcessClass> processList;
    private ArrayList<ProcessClass> readyQueue;
    private int currentTime;
    private double averageResponseTime;
    private double averageWaitTime;
    private double averageTurnaroundTime;
    private double cpuUtilization;
    private int timeIdle;
    private static final DecimalFormat decfor = new DecimalFormat("0.00");

    public Scheduler(){
        this.processList = new ArrayList<>();
        this.readyQueue = new ArrayList<>();
        this.currentTime = 0;
        this.timeIdle = 0;
    }

    // Adds a process to the list
    public void addProcess(ProcessClass p){
        processList.add(p);
    }
    public void run(){
        while(!allProcessesComplete()){
            updateReadyQueue();
            if(!readyQueue.isEmpty()){
                ProcessClass p = getNextProcess();
                System.out.println("Current Time: " + currentTime);
                printReadyQueue();
                showIOQueue();
                executeProcess(p);
                updateProcess(p);
                readyQueue.remove(p);
                showNextProcess(p);
                System.out.println();
            }else{
                System.out.println("Current Time (CPU IDLE): " + currentTime);
                currentTime++;
                timeIdle++;
            }
        }
        System.out.println("Current Time: " + currentTime);
        printReadyQueue();
        showIOQueue();
        System.out.println();
        calculateTimes();
        calculateAverages();
        calculateCPUUtilization();
        printResults();
    }

    // Initializes an instance of ProcessClass object to first index in the list. Then loops through the list checking if any other process has a lower arrival time
    public ProcessClass getNextProcess(){
        ProcessClass p = readyQueue.get(0);
        for(int i=1; i < readyQueue.size();i++){
            if(readyQueue.get(i).arrivalTime < p.arrivalTime){
                p = readyQueue.get(i);
            }
        }
        return p;

    }

    // Performs a check to see if this is the first time the process is appearing thus being able to calculate the response time. Then
    // adds the process next burst to the current time by using the process's burst index and getting the value from the bursTimes array at that index
    public void executeProcess(ProcessClass p){
        if(!p.hasEnteredOnce){
            p.responseTime = currentTime;
            p.hasEnteredOnce = true;
        }
        currentTime += p.burstTimes.get(p.burstIndex);

    }
    // Increments the process's burst index by 1 and checks if the IO times are done or not. If they are not then it increments the IO index and calculates the next arrival time.
    // If the IO times are done that means the scheduler just executed the process's final burst and the completed time for the process can be calculated.
    public void updateProcess(ProcessClass p){
        p.burstIndex++;
        if(!p.ioCompleted()){
            p.arrivalTime = currentTime + p.ioTimes.get(p.ioIndex);
            p.ioIndex++;
        }else{
            p.arrivalTime = -1;
            p.completedTime = currentTime;
        }
    }

    // Prints out the next process and its corresponding burst time by using it's burst index decremented by 1 since the updateProcess() method increments it.
    public void showNextProcess(ProcessClass p){
        int index = p.burstIndex;
        index--;
        if(index < 0){
            index =0;
        }
        System.out.println("Next Process: " + p.name + " ( " + p.burstTimes.get(index) + " )");
    }
    // Creates a new HashMap and loops through the process list and adds any process's whose arrival time is greater than the current time i.e. they are not ready
    // I know I'm creating a new HashMap every time this method gets called, but I didn't want to manage 3 data structs in the Scheduler class.
    // Calculates the time remaining for each process by subtracting its arrival time for the current time.
    public void showIOQueue(){
        HashMap<String,Integer> map = new HashMap<>();
        for(ProcessClass p : processList){
            if(p.arrivalTime > currentTime){
                int timeLeft = p.arrivalTime - currentTime;
                map.put(p.name,timeLeft);
            }
        }
        System.out.println("IO Queue: " + map);
    }

    // Loops through the readyQueue and prints out all the process's names in no specific order
    public void printReadyQueue(){
        System.out.print("Ready Queue: ");
        for(ProcessClass p : readyQueue){
            System.out.print(p.name + ", ");
        }
        System.out.println();
    }

    // Loops through the process list and first checks if the process is complete or not. If they are not then it checks if its arrival time is lower or equal to the current time thus adding
    // it to the readyQueue
    public void updateReadyQueue(){
        for (ProcessClass processClass : processList) {
            if (!processClass.isCompleted()) {
                if (processClass.arrivalTime <= currentTime && !readyQueue.contains(processClass)) {
                    readyQueue.add(processClass);
                }
            }
        }
    }

    // Loops through the process list checking if any process is not complete. Returns true if every process is complete or false if otherwise
    public boolean allProcessesComplete(){
        for(ProcessClass p: processList){
            if(!p.isCompleted()){
                return false;
            }
        }
        return true;
    }

    // Loops through the process list and calculates the times for each process
    public void calculateTimes(){
        for(ProcessClass p : processList){
            p.turnaroundTime = p.completedTime - 0;
            p.waitTime = p.turnaroundTime - p.sumOfData;
        }
    }

    // Loops through the process list and calculates the averages
    public void calculateAverages(){
        double sumWait =0;
        double sumResponse = 0;
        double sumTurnaround =0;
        for(ProcessClass p : processList){
            sumWait += p.waitTime;
            sumResponse += p.responseTime;
            sumTurnaround += p.turnaroundTime;
        }
        averageResponseTime = sumResponse / processList.size();
        averageWaitTime = sumWait / processList.size();
        averageTurnaroundTime = sumTurnaround / processList.size();
    }
    public void calculateCPUUtilization(){
        int totalTime = currentTime;
        cpuUtilization = (double) (totalTime - timeIdle) / totalTime * 100;
    }
    public void printResults(){
        System.out.println("Process\t\tResponse Time\t\tWait Time\t\tTurnaround Time");
        System.out.println("-------\t\t--------------\t\t-----------\t\t---------------");
        for(ProcessClass p : processList){
            System.out.println(p.name + "\t\t\t\t" + p.responseTime + "\t\t\t\t\t" + p.waitTime + "\t\t\t\t" + p.turnaroundTime);
        }
        System.out.println();
        System.out.println("Average\t\t\t" + decfor.format(averageResponseTime) + "\t\t\t" + decfor.format(averageWaitTime)+ "\t\t\t" + decfor.format(averageTurnaroundTime));
        System.out.println("Time idle: " + timeIdle);
        System.out.println("Total Time: " + currentTime);
        System.out.println("CPU Utilization: " + decfor.format(cpuUtilization) + "%");
    }
}