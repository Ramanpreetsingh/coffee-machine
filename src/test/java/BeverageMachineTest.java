import com.coffee.machine.Application;
import com.coffee.machine.model.BeverageMachine;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BeverageMachineTest {

    BeverageMachine beverageMachine;

    @AfterEach
    void tearDown(){
        beverageMachine.shutdownMachine();
        resetSingleton(BeverageMachine.class, "instance");
    }

    public static void resetSingleton(Class clazz, String fieldName) {
        Field instance;
        try {
            instance = clazz.getDeclaredField(fieldName);
            instance.setAccessible(true);
            Thread.sleep(1000);
            instance.set(null, null);
        } catch (Exception e) {
            throw new RuntimeException("Exception while resetting singleton");
        }
    }

    @Test
    public void shouldCreateAllBeverages() throws IOException {
        String jsonInput = getJsonStringFromFile("test_input1.json");

        beverageMachine = BeverageMachine.getInstance(jsonInput);
        beverageMachine.makeBeverageParallel(Arrays.asList("hot_tea", "hot_coffee", "green_tea", "black_tea"));

        Map<String, Integer> beveragesProduced = beverageMachine.getBeveragesProduced();


        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> beveragesProduced.size() == 4);

        assertTrue(beveragesProduced.containsKey("hot_tea"));
        assertTrue(beveragesProduced.containsKey("hot_coffee"));
        assertTrue(beveragesProduced.containsKey("green_tea"));
        assertTrue(beveragesProduced.containsKey("black_tea"));
        assertEquals(beveragesProduced.get("hot_coffee"), Integer.valueOf(1));
        assertEquals(beveragesProduced.get("hot_tea"), Integer.valueOf(1));
        assertEquals(beveragesProduced.get("green_tea"), Integer.valueOf(1));
        assertEquals(beveragesProduced.get("black_tea"), Integer.valueOf(1));

    }

    @Test
    public void shouldCreateAllBeveragesAfterRefill() throws IOException {
        String jsonInput = getJsonStringFromFile("test_input2.json");

        beverageMachine = BeverageMachine.getInstance(jsonInput);
        beverageMachine.makeBeverageParallel(Arrays.asList("hot_tea", "hot_coffee", "green_tea", "black_tea"));

        Map<String, Integer> beveragesProduced = beverageMachine.getBeveragesProduced();

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> beveragesProduced.size() == 2);

        assertTrue(beveragesProduced.containsKey("hot_tea"));
        assertTrue(beveragesProduced.containsKey("hot_coffee"));
        assertEquals(beveragesProduced.get("hot_coffee"), Integer.valueOf(1));
        assertEquals(beveragesProduced.get("hot_coffee"), Integer.valueOf(1));

        beverageMachine.refillContent("sugar_syrup", 100);
        beverageMachine.refillContent("green_mixture", 100);
        beverageMachine.refillContent("hot_water", 500);

        beverageMachine.makeBeverageParallel(Arrays.asList("green_tea", "black_tea"));

        Map<String, Integer> beveragesProducedAfterRefill = beverageMachine.getBeveragesProduced();

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> beveragesProducedAfterRefill.size() == 4);

        assertTrue(beveragesProducedAfterRefill.containsKey("hot_tea"));
        assertTrue(beveragesProducedAfterRefill.containsKey("hot_coffee"));
        assertTrue(beveragesProducedAfterRefill.containsKey("green_tea"));
        assertTrue(beveragesProducedAfterRefill.containsKey("black_tea"));
        assertEquals(beveragesProducedAfterRefill.get("hot_coffee"), Integer.valueOf(1));
        assertEquals(beveragesProducedAfterRefill.get("hot_coffee"), Integer.valueOf(1));
        assertEquals(beveragesProducedAfterRefill.get("green_tea"), Integer.valueOf(1));
        assertEquals(beveragesProducedAfterRefill.get("black_tea"), Integer.valueOf(1));
    }

    private String getJsonStringFromFile(String fileName) throws IOException {
        File file = new File(Application.class.getClassLoader().getResource(fileName).getFile());
        return FileUtils.readFileToString(file, "UTF-8");
    }

    @Test
    public void shouldNotCreateBeveragesIfConfigNotAvailable() throws IOException {
        String jsonInput = getJsonStringFromFile("test_input3.json");

        beverageMachine = BeverageMachine.getInstance(jsonInput);
        beverageMachine.makeBeverageParallel(Arrays.asList("hot_tea", "hot_coffee", "green_tea", "black_tea"));

        Map<String, Integer> beveragesProduced = beverageMachine.getBeveragesProduced();

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> beveragesProduced.size() == 0);

    }

    @Test
    public void shouldCreateMultipleBeveragesOfSameType() throws IOException {
        String jsonInput = getJsonStringFromFile("test_input4.json");

        beverageMachine = BeverageMachine.getInstance(jsonInput);
        beverageMachine.makeBeverageParallel(Arrays.asList("hot_tea", "hot_tea", "hot_coffee", "hot_coffee",
                "green_tea", "black_tea"));

        Map<String, Integer> beveragesProduced = beverageMachine.getBeveragesProduced();

        await()
                .atMost(5, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .until(() -> beveragesProduced.size() == 4);

        assertTrue(beveragesProduced.containsKey("hot_tea"));
        assertTrue(beveragesProduced.containsKey("hot_coffee"));
        assertTrue(beveragesProduced.containsKey("green_tea"));
        assertTrue(beveragesProduced.containsKey("black_tea"));
        assertEquals(beveragesProduced.get("hot_coffee"), Integer.valueOf(2));
        assertEquals(beveragesProduced.get("hot_tea"), Integer.valueOf(2));
        assertEquals(beveragesProduced.get("green_tea"), Integer.valueOf(1));
        assertEquals(beveragesProduced.get("black_tea"), Integer.valueOf(1));
    }

}
