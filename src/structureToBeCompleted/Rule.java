package structureToBeCompleted;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Rule {
	private List<Atom> hypothesisPositif;// l'hypothese : une liste d'atomes (H+)
	private List<Atom> hypothesisNegatif;// l'hypothese : une liste d'atomes (H+)
	private Atom conclusion;// la conclusion : un atome
	private int compteur =0;
	/**
	 * Constructeur
	 * 
	 * @param strRule la regle, sous forme sous forme textuelle ; cette forme est
	 *                "atome1;atome2;...atomek", ou les (k-1) premiers atomes
	 *                forment l'hypothese, et le dernier forme la conclusion
	 */
	public Rule(String strRule) {
		hypothesisPositif = new ArrayList<Atom>();
		hypothesisNegatif= new ArrayList<Atom>();
		StringTokenizer st = new StringTokenizer(strRule, ";");
		while (st.hasMoreTokens()) {
			String s = st.nextToken(); // s represente un atome

			if(s.charAt(0)=='!'){
				s=s.substring(1);
				Atom a = new Atom(s);
				hypothesisNegatif.add(a);
			}
			else{
				Atom a = new Atom(s);
				hypothesisPositif.add(a);// ajout de a a la liste des atomes de l'hypothese (pour l'instant, on ajoute
				// aussi celui de la conclusion)
			}


		}
		// on a mis tous les atomes crees en hypothese
		// il reste a tranferer le dernier en conclusion
		conclusion = hypothesisPositif.get(hypothesisPositif.size() - 1);
		hypothesisPositif.remove(hypothesisPositif.size() - 1);
	}

	/**
	 * accesseur a l'hypothese de la regle positif
	 * 
	 * @return l'hypothese de la regle
	 */
	public List<Atom> getHypothesisPositif() {
		return hypothesisPositif;
	}

	/**
	 * accesseur a l'hypothese de la regle négatif
	 *
	 * @return l'hypothese de la regle
	 */
	public List<Atom> getHypothesisNegatif() {return hypothesisNegatif; }
	/**
	 * retourne la ieme atome de l'hypothese
	 * 
	 * @param i le rang de l'atome a retourner (debut a 0)
	 * @return le ieme atome de l'hypothese
	 */
	public Atom getAtomHyp(int i) {
		return hypothesisPositif.get(i);
	}

	/**
	 * accesseur a la conclusion de la regle
	 * 
	 * @return l'atome conclusion de la regle
	 */
	public Atom getConclusion() {
		return conclusion;
	}

	/**
	 * retourne une description de la regle
	 * 
	 * @return la chaine decrivant la regle (suivant l'ecriture habituelle)
	 */
	@Override
	public String toString() {
		String s = "";
		for (int i = 0; i < hypothesisPositif.size(); i++) {
			s += hypothesisPositif.get(i);
			if (i < hypothesisPositif.size() - 1 || hypothesisNegatif.size()>0)
				s += " ; ";
		}
		for (int i = 0; i < hypothesisNegatif.size(); i++) {
			s += "!"+hypothesisNegatif.get(i);
			if (i < hypothesisNegatif.size() - 1)
				s += " ; ";
		}
		s += " --> ";
		s += conclusion;
		return s;
	}
	public void initialiserCompteur(int i){
		compteur = getHypothesisPositif().size();

	}
	public int getCompteur() {
		return compteur;
	}

	public void setCompteur(int compteur) {
		this.compteur = compteur;
	}
	public void decrementationCompteur() {
		this.compteur--;
	}

	public void setHypothesisNegatif(List<Atom> hypothesisNegatif) {
		this.hypothesisNegatif = hypothesisNegatif;
	}

	String toStringNeg(){
		return "!"+getHypothesisNegatif().toString();
	}

}
