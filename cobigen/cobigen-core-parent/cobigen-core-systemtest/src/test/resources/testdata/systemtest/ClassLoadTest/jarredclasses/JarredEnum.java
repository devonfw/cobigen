public enum JarredEnum {
    NAME("name"),
    NUMBER("number");
	
    private String name;
    
	JarredEnum(String name){
	    this.name=name;
	}
	
	@Override
	public String toString(){
	    return name;
	}
}
