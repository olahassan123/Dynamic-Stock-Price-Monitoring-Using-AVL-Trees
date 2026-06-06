

public class StockManager {
    private AVLTree tree;
    private AVLTreePrice treePrice;


    public StockManager() {
    }

    // 1. Initialize the system
    public void initStocks() {
        this.tree = new AVLTree(); // Initialize the AVL tree if needed
        this.treePrice = new AVLTreePrice();
    }

    // 2. Add a new stock
    public void addStock(String stockId, long timestamp, Float price) {
        if (tree.find(stockId) == null&&price>0&&stockId!=null) {
            StockValue value = new StockValue(price, timestamp);
            tree.insert(stockId, value);
            treePrice.insert(value,stockId);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 3. Remove a stock
    public void removeStock(String stockId) {
        if (tree.find(stockId) == null) {
            throw new IllegalArgumentException();
        } else {
            StockValue stockValue = tree.find(stockId).getValue(); // Retrieve the StockValue object
            stockValue.clearAllValues(); // Clear historical data in StockValue tree
            treePrice.delete(tree.find(stockId).getValue().getCurrentValue(),stockId);
            tree.remove(stockId);
        }
    }

    // 4. Update a stock price
    public void updateStock(String stockId, long timestamp, Float priceDifference) {
        AVLTree.AVLTreeNode node = tree.find(stockId);
        if (node != null&&priceDifference!=0) {
            float update=tree.find(stockId).getValue().getCurrentValue();
            treePrice.delete(update,stockId);
            node.getValue().addValueAtTime(timestamp, priceDifference);
            treePrice.insert(tree.find(stockId).getValue(),stockId);

        } else {
            throw new IllegalArgumentException();
        }
    }

    // 5. Get the current price of a stock
    public Float getStockPrice(String stockId) {
        AVLTree.AVLTreeNode node = tree.find(stockId);
        if (node != null) {
            return node.getValue().getCurrentValue();
        }
        throw new IllegalArgumentException();
    }

    // 6. Remove a specific timestamp from a stock's history
    public void removeStockTimestamp(String stockId, long timestamp) {
        AVLTree.AVLTreeNode node = tree.find(stockId);
        if (node != null) {
            treePrice.delete(tree.find(stockId).getValue().getCurrentValue(),stockId);
            node.getValue().removeValueAtTime(timestamp);
            treePrice.insert(tree.find(stockId).getValue(),stockId);
        } else {
            throw new IllegalArgumentException();
        }
    }

    // 7. Get the amount of stocks in a given price range
    public int getAmountStocksInPriceRange(Float price1, Float price2) {
        if (price1 == null || price2 == null || price1 > price2) {
            throw new IllegalArgumentException();
        }
        int count1=countLessThan(treePrice.getRoot(),price2);
        int count2=countLessThan(treePrice.getRoot(),price1);
        if (price1==price2){
            return count1;
        }

        return count1-count2;
    }
    public int countLessThan(AVLTreePrice.AVLTreePriceNode node, float key) {
        if (node == null) {
            return 0;
        }

        if (key <node.getValue().getCurrentValue()) {
            return countLessThan(node.getLeft(), key);
        } else if (key ==node.getValue().getCurrentValue()){
            return 1+countLessThan(node.getLeft(), key);
        }
        else {
            return 1 + (node.getLeft() != null ? node.getLeft().getSubtreeSize() : 0) + countLessThan(node.getRight(), key);
        }}
    // 8. Get a list of stock IDs within a given price range
    public String[] getStocksInPriceRange(Float price1, Float price2) {
        if (price1 == null || price2 == null || price1 > price2) {
            throw new IllegalArgumentException();
        }
        // Estimate the size and populate the result array directly
        int size = getAmountStocksInPriceRange( price1, price2);
        String[] result = new String[size];
        String[] result1 = new String[countLessThan(treePrice.getRoot(),price1)];
        String[] result2 = new String[countLessThan(treePrice.getRoot(),price2)];
        int[] count1 = new int[1];
        int[] count2 = new int[1];
        float[] sorting=new float[size];
        count1[0]=0;
        count2[0]=0;

        int[] count = new int[1];
        count[0]=0;


        collectStocksInRange(treePrice.getRoot(), price1, price2, result,count);  // no need to pass the count anymore
        return result;
    }

    private void collectStocksInRange(AVLTreePrice.AVLTreePriceNode node, Float price1, Float price2, String[] result, int[] index) {
        if (node == null) return;

        Float currentPrice = node.getValue().getCurrentValue();

        if (currentPrice >= price1) {
            collectStocksInRange(node.getLeft(), price1, price2, result, index);
        }

        if (currentPrice >= price1 && currentPrice <= price2) {
            result[index[0]++] = node.getStockId();
        }

        if (currentPrice <= price2) {
            collectStocksInRange(node.getRight(), price1, price2, result, index);
        }
    }
}


