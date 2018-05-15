package utils.resources.dataaccess.api;

public class DeepEntity {

    private TestEntityComponent testEntityComponent = new TestEntityComponent();

    public TestEntityComponent getTestEntityComponent() {
        return testEntityComponent;
    }

    public void setTestEntityComponent(TestEntityComponent testEntityComponent) {
        this.testEntityComponent = testEntityComponent;
    }
}