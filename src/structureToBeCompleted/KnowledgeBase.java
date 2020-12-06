package structureToBeCompleted;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KnowledgeBase {

    private FactBase bf; // base de faits initiale
    private RuleBase br; // base de règles
    private FactBase bfSat; // base de faits saturée - vide initialement
    StringBuilder resultat = new StringBuilder();
    List<Atom> prouvé = new ArrayList<>();
    List<Atom> échec = new ArrayList<>();
    StringBuilder détail = new StringBuilder();

    public KnowledgeBase() {
        bf = new FactBase();
        br = new RuleBase();
        bfSat = new FactBase();

    }

    public KnowledgeBase(String fic) {
        this(); // initialisation des bases à vide
        BufferedReader lectureFichier;
        try {
            lectureFichier = new BufferedReader(new FileReader(fic));
        } catch (FileNotFoundException e) {
            System.err.println("Fichier base de connaissances absent: " + fic);
            e.printStackTrace();
            return;
        }
        try {
            String s = lectureFichier.readLine();
            if (s != null) { // si non vide
                bf = new FactBase(s); // 1ere ligne : factbase
                s = lectureFichier.readLine();
                while (s != null && s.length() != 0) { // arret si fin de fichier ou ligne vide
                    br.addRule(new Rule(s));
                    s = lectureFichier.readLine();
                }
            }
            lectureFichier.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FactBase getBf() {
        return bf;
    }

    public FactBase getBfSat() {
        return bfSat;
    }

    public RuleBase getBr() {
        return br;
    }

    /**
     * Retourne une description de la base de connaissances
     *
     * @return description de la base de connaissances
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "**********\nBase de connaissances: \n" + bf + br + "\n**********";
    }

    boolean estSemiPos(Rule rule){
        Atom conclusion = rule.getConclusion();
        for (int i = 0; i <getBr().size(); i++) {
            if(getBr().getRule(i).getHypothesisNegatif().contains(conclusion)){
                System.err.println("La regle suivante n'est pas semi-positif: "+getBr().getRule(i).toString()+
                " car "+conclusion.toString()+" est en conclusion de  "+ rule.toString());
                return false;
            }
        }
            return true;
    }
    public void forwardChainingBasic() {
        // algo basique de forward chaining

        bfSat = new FactBase(); // ré-initialisation de bfSat
        bfSat.addAtoms(bf.getAtoms()); // avec les atomes de bf

        boolean fin = false;
        boolean[] Appliquee = new boolean[br.size()];
        for (int i = 0; i < br.size(); i++) {
            Appliquee[i] = false;
        }
        while (!fin) {
            FactBase newFacts = new FactBase();
            for (int i = 0; i < br.size(); i++) {
                if (!Appliquee[i]) {
                    Rule r = br.getRule(i);

                    // test d'applicabilite de la regle i
                    boolean applicable = true;
                    if(estSemiPos(r)) applicable=false;
                    List<Atom> hp = r.getHypothesisPositif();
                    for (int j = 0; applicable && j < hp.size(); j++)
                        if (!bfSat.contains(r.getAtomHyp(j)))
                            applicable = false;
                    if (applicable) {
                        Appliquee[i] = true;
                        Atom c = r.getConclusion();
                        if (!bfSat.contains(c) && !newFacts.contains(c))
                            newFacts.addAtomWithoutCheck(c);
                    }
                }
            }
            if (newFacts.isEmpty())
                fin = true;
            else
                bfSat.addAtoms(newFacts.getAtoms());
        }
    }

    public void forwardChainingBasicOpt() {
        // algo basique de forward chaining
        FactBase aTraiter = getBf();
        bfSat = new FactBase(); // ré-initialisation de bfSat
        bfSat.addAtoms(bf.getAtoms()); // avec les atomes de bf

        for (int i = 0; i < br.size(); i++) {
            br.getRule(i).initialiserCompteur(i);
        }
        while (aTraiter.size() != 0) {
            Atom F = aTraiter.getAtoms().get(0);
            aTraiter.getAtoms().remove(0);
            for (int i = 0; i < br.size(); i++) {
                if (br.getRule(i).getHypothesisPositif().contains(F)) {
                    br.getRule(i).decrementationCompteur();
                    if (br.getRule(i).getCompteur() == 0) {
                        Atom R = br.getRule(i).getConclusion();
                        if (!aTraiter.contains(R)) {
                            aTraiter.addAtomWithoutCheck(R);
                            this.bfSat.addAtomWithoutCheck(R);

                        }
                    }
                }
            }
        }
    }


    public void backwardChaining(String atome) {
        Chrono chrono = new Chrono();
        boolean result = false;
        int nombreDeSolution = 0;
        chrono.start();
        for (int i = 0; i < br.size(); i++) {
            if (br.getRule(i).getConclusion().toString().equals(atome)) {
//                if (backwardChainingOneSolution(i,result)) {
                if (backwardChainingAllSolution(i,nombreDeSolution)) {
                    result = true;
                    nombreDeSolution++;
                    resultat = new StringBuilder();
                }
            }
        }
        if (!result && resultat.toString().equals("")) {
            System.out.println("erreur lors de la saisie ou bien aucune conclusion de contient "+atome +", merci de recommencer ");
            return;
        }
        else if (!result) {
            System.out.println(resultat);
            System.out.println(atome + " n'a pas de solution");
        }
        chrono.stop();
        System.out.println("la simulation a durée: " +chrono.getDureeMs()+"ms pour la recherche d'une solution");

    }

    private boolean backwardChainingOneSolution(int i, boolean result) {
        prouvé = new ArrayList<>();
        échec = new ArrayList<>();
        List<List<Atom>> lb = new ArrayList<>();
        if (backwardChainingOPT(br.getRule(i).getConclusion(), lb, 0,0) && !result) {
            System.out.println("\n---------solution en partant de la régle n°" + (i + 1) + "---------");
            System.out.println(resultat);
            System.out.println(br.getRule(i).getConclusion() +" est prouvé en partant de la règle n°" + (i + 1));
            //          System.out.println(détail);
            return true;
        }
        return false;
    }

    private boolean backwardChainingAllSolution(int i,int nombreSolutionTrouvee) {
        prouvé = new ArrayList<>();
        échec = new ArrayList<>();
        boolean result = false;
        List<List<Atom>> lb = new ArrayList<>();

        if (backwardChainingOPT(br.getRule(i).getConclusion(), lb, 0,nombreSolutionTrouvee)) {
            System.out.println("\n---------solution en partant de la régle n°" + (i + 1) + "---------");
            System.out.println(resultat);
            System.out.println(br.getRule(i).getConclusion() + " est prouvé en partant de la règle n°" + (i + 1));
//          System.out.println(détail);
            result = true;
        }
        return result;
    }


    private boolean backwardChaining(Atom Q, List<List<Atom>> Lb, int nombreTiret, int solutionTrouvee) {
        détail.append("\n").append("\nJe rentre dans l'analyse de l'atome de ").append(Q.toString());
//        System.out.println("La méthode non optmimal est utilisée");
        int y;
        int compteur = solutionTrouvee;
        resultat.append(Q.toString());
        if (bf.getAtoms().contains(Q)) {
            détail.append("\n").append("Cette atome est dans la base de fait");
            return true;
        }

        for (int i = 0; i < br.size(); i++) {
            if(br.getRule(i).getConclusion().equals(Q)&&compteur!=0){
                compteur--;
            }
            else if (br.getRule(i).getConclusion().equals(Q)) {
                nombreTiret++;
                resultat.append("\n").append(nombreTiret(nombreTiret)).append("R").append(i + 1);
                détail.append("\n").append("Je consulte la régle ").append(br.getRule(i));

                for (int j = 0; j < Lb.size(); j++) {
                    if (Lb.contains(br.getRule(i).getHypothesisPositif())) {
                        détail.append("/!\\/!\\/!\\je contient une regle de LB/!\\/!\\/!\\");
                        break;
                    }
                }
                for (y = 0; y < br.getRule(i).getHypothesisPositif().size(); y++) {
                    resultat.append("\n").append(nombreTiret(nombreTiret));

                    if (backwardChaining(br.getRule(i).getHypothesisPositif().get(y), Lb, nombreTiret,0)) {
                        Lb.add(br.getRule(i).getHypothesisPositif());
                    } else break;
                }

                détail.append("\n").append("Je retourne ").append(y == br.getRule(i).getHypothesisPositif().size());
                détail.append("\n").append(Lb);
                if (y == br.getRule(i).getHypothesisPositif().size() == true) {
                    return true;
                }
                nombreTiret--;
            }

        }
        détail.append("\n").append("--->Echec");
        return false;
    }


    private boolean backwardChainingOPT(Atom Q, List<List<Atom>> Lb, int nombreTiret,int solutionTrouvee) {
//        System.out.println("La méthode optimal est utilisée");
        détail.append("\nJe rentre dans l'analyse de l'atome de ").append(Q.toString());
        int y;
        int compteur = solutionTrouvee;
        resultat.append(Q.toString());
        if (bf.getAtoms().contains(Q)) {
            détail.append("\n").append("Cette atome est dans la base de fait");
            resultat.append("--->BF");
            return true;
        }
        for (int i = 0; i < br.size(); i++) {
            if(br.getRule(i).getConclusion().equals(Q)&&compteur!=0){
                compteur--;
            }
            else if (br.getRule(i).getConclusion().equals(Q)) {
                nombreTiret++;
                resultat.append("\n").append(nombreTiret(nombreTiret)).append("R").append(i + 1);
                détail.append("\n").append("Je consulte la régle ").append(br.getRule(i));
                for (int j = 0; j < Lb.size(); j++) {
                    if (Lb.contains(br.getRule(i).getHypothesisPositif())) {
                        détail.append("\n").append("/!\\/!\\/!\\je contient une regle de LB/!\\/!\\/!\\");
                        break;
                    }
                }
                for (y = 0; y < br.getRule(i).getHypothesisPositif().size(); y++) {

                    Atom atomTest = br.getRule(i).getHypothesisPositif().get(y);
                    resultat.append("\n").append(nombreTiret(nombreTiret));

                    if (échec.contains(atomTest)) {
                        resultat.append(atomTest).append("--->Déjà échoué");
                        break;
                    }

                    if (prouvé.contains(atomTest)) {
                        resultat.append(atomTest).append("--->Déjà prouvé");
                    }

                    else if (backwardChainingOPT(br.getRule(i).getHypothesisPositif().get(y), Lb, nombreTiret,0)) {
                        Lb.add(br.getRule(i).getHypothesisPositif());
                        prouvé.add(atomTest);

                    } else {
                        échec.add(atomTest);
                        break;
                    }
                }

                détail.append("\n").append("Je retourne " + (y == br.getRule(i).getHypothesisPositif().size()));
                détail.append("\n").append(Lb);
                if (y == br.getRule(i).getHypothesisPositif().size() == true) {
                    return true;
                }
                nombreTiret--;
            }

        }
        resultat.append("\n").append(nombreTiret(nombreTiret)).append(">Echec");
        return false;
    }

    String nombreTiret(int n) {
        StringBuilder resultat = new StringBuilder();
        for (int i = 0; i < n; i++) {
            resultat.append("---");
        }
        return resultat.toString();
    }


}
