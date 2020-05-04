package atree.core.attacker;

public class Attacker {

	private String name;
	//private Collection<AttackNode> initialKnowledge;

	
	
	public Attacker(String name) {
		this.name = name;
		
		//initialKnowledge = new HashSet<>();
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Attacker other = (Attacker) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	public String getName() {
		return name;
	}
	
	/*public void addInitialKnowledge(AttackNode a)
	{
		initialKnowledge.add(a);
	}*/
	
}
