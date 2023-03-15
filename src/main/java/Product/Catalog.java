package Product;

import java.io.*;
import java.util.*;

public class Catalog {
    private static String NAMES_PATH = "data/product_seeds/names.txt";
    private static List<String> names = new ArrayList<>();

    static {
        loadCatalog();
    }

    private static void loadCatalog() {
        loadNames();
    }


    private static void loadNames() {
        File seed = new File(NAMES_PATH);
        Set<String> names = new HashSet<>();
        Scanner scanner;
        try {
            scanner = new Scanner(seed).useDelimiter("([ \n\r])+");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (scanner.hasNext()) {
            String name = scanner.next();
            names.add(name);
        }
        Catalog.names = new ArrayList<>(names);
    }

    public static Product get(int index) {
        return CatalogSet.get(names.get(index));
    }

    public static Product getProduct(ProductSeed productSeed) {
        Product product = CatalogSet.get(productSeed);
        if (product == null) {
            product = makeProduct(productSeed.getVolume(), productSeed.getBasePrice());
        }
        return product;
    }

    public static ProductSeed getProductSeed(int volume, int basePrice) {
        Product product = CatalogSet.get(volume, basePrice);
        if (product == null) {
            product = makeProduct(volume, basePrice);
        }
        return product.getSeed();
    }


    private static Product makeProduct(int volume, int basePrice) {
        String newName = getName();
        Product newProduct = new Product(newName, volume, basePrice);
        CatalogSet.add(newProduct);
        return newProduct;
    }

    private static String getName() {
        int used = CatalogSet.size();
        if (used >= names.size()) {
            return "Unlabelled_" + (used - names.size());
        }
        return names.get(used);
    }

    public static int size() {
        return CatalogSet.size();
    }

}

class CatalogSet {
    private static String PATH = "data/product_seeds/catalog.txt";


    private static Map<String, Product> catalogByName = new HashMap<>();
    private static Map<Product, Product> catalogByComponents = new HashMap<>();

    static {
        loadProducts();

    }

    private static void loadProducts() {
        File saveFile = new File(PATH);
        Scanner scanner;
        try {
            scanner = new Scanner(saveFile).useDelimiter("([ \n\r])+");
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        while (scanner.hasNext()) {
            String name = scanner.next();
            int volume = scanner.nextInt();
            int basePrice = scanner.nextInt();
            Product product = new Product(name, volume, basePrice);
            add_(product);
        }
        scanner.close();
    }

    private static void add_(Product product) {
        if (catalogByName.containsKey(product.getName())) {
            throw new RuntimeException("Product already exists with that name");
        }
        if (catalogByComponents.containsKey(product)) {
            throw new RuntimeException("Product already exists with that volume and base price");
        }
        catalogByName.put(product.getName(), product);
        catalogByComponents.put(product, product);
    }

    public static void add(Product product) {
        if (catalogByName.containsKey(product.getName())) {
            throw new RuntimeException("Product already exists with that name");
        }
        if (catalogByComponents.containsKey(product)) {
            throw new RuntimeException("Product already exists with that volume and base price");
        }
        catalogByName.put(product.getName(), product);
        catalogByComponents.put(product, product);
        System.out.println("Added product: " + product.getName());
        saveProduct(product);
        System.out.println("Saved product");
    }

    private static void saveProduct(Product product) {
        PrintWriter printWriter;
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(PATH, true);
            printWriter = new PrintWriter(fileWriter, true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String text = product.getName() + " " + product.getVolume() + " " + product.getBasePrice();
        printWriter.println(text);

    }

    public static Product get(ProductSeed seed) {
        return get(seed.getVolume(), seed.getBasePrice());
    }

    public static Product get(String name) {
        Product product = catalogByName.get(name);
        if (product == null) {
            return null;
        }
        return product;
    }

    public static Product get(int volume, int basePrice) {
        Product product = catalogByComponents.get(new Product("", volume, basePrice));
        if (product == null) {
            return null;
        }
        return product;
    }

    public static boolean contains(String name) {
        return catalogByName.get(name) != null;
    }

    public static boolean contains(ProductSeed seed) {
        return catalogByComponents.get(new Product("", seed.getVolume(), seed.getBasePrice())) != null;
    }

    public static int size() {
        return catalogByName.size();
    }

}

class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

class InconsistencyException extends RuntimeException {
    public InconsistencyException(String message) {
        super(message);
    }
}