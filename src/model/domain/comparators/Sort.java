package model.domain.comparators;

public enum Sort {
	ASCENDING("Asc"),
	DESCENDING("Desc");

	private String direction;

	private Sort(String direction) {
		this.direction = direction;
	}

	public String toString() {
		return this.direction;
	}
}
