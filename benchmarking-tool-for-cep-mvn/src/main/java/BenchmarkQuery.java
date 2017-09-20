


import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

public class BenchmarkQuery {
    public static void query() {
        EPServiceProvider queryEngine = EPServiceProviderManager.getDefaultProvider();
        queryEngine.getEPAdministrator().getConfiguration().addEventType(IntEvent.class);
        String epl = "select myValue from IntEvent";
        EPStatement statement = queryEngine.getEPAdministrator().createEPL(epl);
        statement.addListener( (newData, oldData) -> {
            int myValue = (int) newData[0].get("myValue");
            System.out.println(String.format("New Random value: %d, Received time: %d", myValue, System.currentTimeMillis()));
        });
    }
}