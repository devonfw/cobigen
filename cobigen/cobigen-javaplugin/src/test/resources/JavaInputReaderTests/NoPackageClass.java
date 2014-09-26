import java.util.List;

public class TestClass extends NoPackageSuperClass implements TestInterface1, TestInterface2{

    private List<String> customList;

    public List<String> getCustomList() {

        return customList;
    }

    public void setCustomList(List<String> customList) {

        this.customList = customList;
    }

}
