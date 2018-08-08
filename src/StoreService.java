import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;


public class StoreService {

    ObjectMapper mapper = new ObjectMapper();
    List<Sale> sales = new ArrayList<Sale>();
    String prevousOrder = null;
    HashMap<Integer, HashMap<String, Double>> salesbyHourandDepartment = new HashMap<>();

    protected void run() {
        readJson();
    }

    public List<Sale> readJson() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\\\test\\\\sales.json"));
            String line;

            HashMap<String, HashMap<Integer, BigInteger>> salesByDeptbyHour = new HashMap<String, HashMap<Integer, BigInteger>>();
            while ((line = br.readLine()) != null) {
                process(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }

    private void process(String line) throws IOException {
        Sale sale = mapper.readValue(line, Sale.class);
        sales.add(sale);

        if (prevousOrder == null || !prevousOrder.equals(sale.getOrder_id())) {
            prevousOrder = sale.getOrder_id();
            System.out.println(String.format("State of Register Number: %s is change at  Time %s",
                    sale.getRegister(), sale.getOrder_time()));

        }

        int hour = getHour(sale.getOrder_time());
        if (salesbyHourandDepartment.containsKey(hour)) {
            HashMap<String, Double> val = salesbyHourandDepartment.get(hour);
            if (val.containsKey(sale.getDepartment())) {
                val.put(sale.getDepartment(), (Double) val.get(sale.getDepartment()) + sale.getAmount());
            } else {
                //System.out.println("New department found " + sale.getDepartment());
                val.put(sale.getDepartment(), sale.getAmount());
            }
        } else {
            HashMap<String, Double> aSaleByDepartment = new HashMap<>();
            aSaleByDepartment.put(sale.getDepartment(), sale.getAmount());
            salesbyHourandDepartment.put(hour, aSaleByDepartment);

            if (salesbyHourandDepartment.size() > 1) {
                for (Map.Entry<Integer, HashMap<String, Double>> hourEntry : salesbyHourandDepartment.entrySet()) {
                    HashMap<String, Double> test = salesbyHourandDepartment.get(hourEntry.getKey());
                    SortedSet<Double> keys = new TreeSet<Double>(test.values());
                    System.out.println("Lowest sale for hour " + hourEntry.getKey() + " is" + keys.first());
                }
            }
        }
    }

    private Integer getHour(long epoch) {
        Calendar cal = Calendar.getInstance();
        Date date = new Date(epoch);
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }

    public List<Sale> readline() {
        ObjectMapper mapper = new ObjectMapper();
        List<Sale> sales = new ArrayList<Sale>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("C:\\\\test\\\\sales.json"));
            String line;
            while ((line = br.readLine()) != null) {
                process(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }
}