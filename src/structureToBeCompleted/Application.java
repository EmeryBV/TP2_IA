package structureToBeCompleted;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Application {


    public static void main(String args[]) {
        String filename = "réunion d'amis.txt";

        System.out.println("----------------------------------Forward Chaining----------------------------------");
        KnowledgeBase klb = new KnowledgeBase(filename);
        System.out.println(klb.getBf().toString());
        System.out.println(klb.getBr().toString());
        klb.forwardChainingBasic();
        FactBase bfSat = klb.getBfSat();
        System.out.println(bfSat.toString());
        System.out.println("----------------------------------Backward Chaining----------------------------------");
        klb = new KnowledgeBase(filename);


        while(true){
            System.out.println(klb.getBf().toString());
            System.out.println(klb.getBr().toString());
            System.out.println("\nVeulliez rentrer l'atome à prouver");
            Scanner myObj = new Scanner(System.in);
            String atome = myObj.nextLine();
            klb.backwardChaining(atome);

            if(bfSat.toString().contains(atome.toString())){
                System.out.println(atome.toString()+" est prouvé par le forward Chaining car elle se trouve dans la base de fait saturée");
            }
            else {
                System.out.println(atome.toString()+" n'est pas prouvé par le forward Chaining car elle se trouve pas dans la base de fait saturée");
            }
        }


    }
}
