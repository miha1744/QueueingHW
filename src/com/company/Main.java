package com.company;

import java.util.Random;
import java.util.Scanner;

///Made by Mikhail Ivanov

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        PipesServer RosNeftServer = new PipesServer("RosneftPipe");
        RosNeftServer.addServiceTableRow(30, 2);
        RosNeftServer.addServiceTableRow(28, 3);
        RosNeftServer.addServiceTableRow(25, 4);
        RosNeftServer.addServiceTableRow(17, 5);

        PipesServer GazpromServer = new PipesServer("GazpromPipe");
        GazpromServer.addServiceTableRow(35, 3);
        GazpromServer.addServiceTableRow(25, 4);
        GazpromServer.addServiceTableRow(20, 5);
        GazpromServer.addServiceTableRow(20, 6);

        int prevArrivalTime = 0;
        int totalWaitingTime = 0;
        int totalServiceTime = 0;
        int customersWhoWait = 0;
        int simulationTime = 0;

        System.out.print("Enter the number of pipes to simulate for: ");
        int totalCustomers = sc.nextInt();

        System.out.println(
                "\n-------------------------------------------------------------------------------------------------------------");
        System.out.println("No\t Pipe\t   IAT\tAT\tWorkshop  \tST\tSB\tSE\tWT\tIdle");
        System.out.println(
                "-------------------------------------------------------------------------------------------------------------");

        for (int i = 0; i < totalCustomers; i++) {
            PipesServer servicedBy = null;

            int randomDigitForInterArrivalTime = rand.nextInt(100);
            int interArrivalTime = 0;
            if (i == 0)
                interArrivalTime = 0;
            else if (randomDigitForInterArrivalTime < 26)
                interArrivalTime = 1;
            else if (randomDigitForInterArrivalTime < 66)
                interArrivalTime = 2;
            else if (randomDigitForInterArrivalTime < 86)
                interArrivalTime = 3;
            else
                interArrivalTime = 4;

            prevArrivalTime += interArrivalTime;

            int randomDigitForCarType = rand.nextInt(100);
            String carType = "";

            if (randomDigitForCarType < 50) {
                carType = "RosneftPipe";
                if (RosNeftServer.busyTill <= prevArrivalTime)
                    servicedBy = RosNeftServer;
                else if (GazpromServer.busyTill <= prevArrivalTime)
                    servicedBy = GazpromServer;
                else
                    servicedBy = RosNeftServer;
            } else {
                carType = "GazpromPipe";
                if (GazpromServer.busyTill <= prevArrivalTime)
                    servicedBy = GazpromServer;
                else if (RosNeftServer.busyTill <= prevArrivalTime)
                    servicedBy = RosNeftServer;
                else
                    servicedBy = GazpromServer;
            }

            int randomDigitForServiceTime = rand.nextInt(100);
            int serviceTime = 0, waitingTime, idleTime, serviceBegin, serviceEnd;
            for (int j = 0; j < servicedBy.higherLimits.size(); j++)
                if (randomDigitForServiceTime <= servicedBy.higherLimits.get(j)) {
                    serviceTime = servicedBy.serviceValues.get(j);
                    break;
                }

            if (servicedBy.busyTill <= prevArrivalTime) {
                waitingTime = 0;
                serviceBegin = prevArrivalTime;
                idleTime = prevArrivalTime - servicedBy.busyTill;
            } else {
                customersWhoWait++;
                waitingTime = servicedBy.busyTill - prevArrivalTime;
                serviceBegin = servicedBy.busyTill;
                idleTime = 0;
            }

            servicedBy.idleTime += idleTime;
            serviceEnd = servicedBy.busyTill = serviceBegin + serviceTime;

            System.out.println((i + 1) + "\t"  + carType + "\t"
                    + interArrivalTime + "\t" + prevArrivalTime + "\t"
                    + servicedBy.getType() +  "\t" + serviceTime + "\t" + serviceBegin
                    + "\t" + serviceEnd + "\t" + waitingTime + "\t" + idleTime);

            totalWaitingTime += waitingTime;
            totalServiceTime += serviceTime;
            simulationTime = serviceEnd;
        }

        System.out.println("\n\n-----------------------------------------------------");
        System.out.println("Simulation statistics");
        System.out.println("-----------------------------------------------------");

        System.out.printf("Average waiting time:\t\t\t\t%.2f\n", (totalWaitingTime * 1.0 / totalCustomers));
        System.out.printf("Probability that a customer has to wait:\t%.2f\n",
                (customersWhoWait * 1.0 / totalCustomers));
        System.out.printf("Rosneft server is being idle:\t\t%.2f\n",
                (RosNeftServer.idleTime * 1.0 / simulationTime));
        System.out.printf("Gazprom server is being idle:\t\t%.2f\n",
                (GazpromServer.idleTime * 1.0 / simulationTime));
        System.out.printf("Average service time:\t\t\t\t%.2f\n", (totalServiceTime * 1.0 / totalCustomers));
        System.out.printf("Average time between intervals:\t\t\t%.2f\n", (prevArrivalTime * 1.0 / totalCustomers));
        System.out.printf("Average waiting time of those who wait:\t\t%.2f\n",
                (totalWaitingTime * 1.0 / customersWhoWait));

        sc.close();
    }
}
