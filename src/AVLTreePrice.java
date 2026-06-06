public class AVLTreePrice {
    static class AVLTreePriceNode {
        private StockValue value;
        private AVLTree stocksAtPrice; // AVL tree to store stocks with same price
        private AVLTreePriceNode left, right, parent;
        private int height;
        private int subtreeSize;
        private String stockId;
        private int numStockPrice;

        public AVLTreePriceNode(StockValue value, String stockId) {
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.height = 1;
            this.subtreeSize = 1;
            this.stockId=stockId;
            this.stocksAtPrice = new AVLTree();
            this.stocksAtPrice.insert(stockId, value);
            this.numStockPrice=1;
        }

        public int getSubtreeSize() {
            return subtreeSize;
        }

        public void setSubtreeSize(int size) {
            this.subtreeSize = size;
        }

        public StockValue getValue() {
            return value;
        }

        public AVLTree getStocksAtPrice() {
            return stocksAtPrice;
        }

        public void setValue(StockValue value) {
            this.value = value;
        }

        public AVLTreePriceNode getLeft() {
            return left;
        }

        public void setLeft(AVLTreePriceNode left) {
            this.left = left;
        }

        public AVLTreePriceNode getRight() {
            return right;
        }

        public void setRight(AVLTreePriceNode right) {
            this.right = right;
        }

        public AVLTreePriceNode getParent() {
            return parent;
        }

        public void setParent(AVLTreePriceNode parent) {
            this.parent = parent;
        }

        public int getHeight() {
            return height;
        }
        public int getNumStockPrice() {
            return numStockPrice;
        }


        public void setHeight(int height) {
            this.height = height;
        }

        public String getStockId() {
            return stockId;
        }
    }


    private AVLTreePriceNode root;

    public AVLTreePrice() {
        this.root = null;
    }

    public AVLTreePriceNode getRoot() {
        return root;
    }

    private int height(AVLTreePriceNode node) {
        return node == null ? 0 : node.getHeight();
    }

    private int getSubtreeSize(AVLTreePriceNode node) {
        return node == null ? 0 : node.getSubtreeSize();
    }

    private void updateNodeMetadata(AVLTreePriceNode node) {
        if (node != null) {
            node.setHeight(1 + Math.max(height(node.getLeft()), height(node.getRight())));
            node.setSubtreeSize(1 + getSubtreeSize(node.getLeft()) + getSubtreeSize(node.getRight()));
        }
    }

    private int getBalance(AVLTreePriceNode node) {
        return node == null ? 0 : height(node.getLeft()) - height(node.getRight());
    }

    private AVLTreePriceNode rotateRight(AVLTreePriceNode y) {
        AVLTreePriceNode x = y.getLeft();
        AVLTreePriceNode T2 = x.getRight();

        x.setRight(y);
        y.setLeft(T2);

        if (T2 != null) T2.setParent(y);

        x.setParent(y.getParent());
        y.setParent(x);

        updateNodeMetadata(y);
        updateNodeMetadata(x);

        return x;
    }

    private AVLTreePriceNode rotateLeft(AVLTreePriceNode x) {
        AVLTreePriceNode y = x.getRight();
        AVLTreePriceNode T2 = y.getLeft();

        y.setLeft(x);
        x.setRight(T2);

        if (T2 != null) T2.setParent(x);

        y.setParent(x.getParent());
        x.setParent(y);

        updateNodeMetadata(x);
        updateNodeMetadata(y);

        return y;
    }

    public void insert(StockValue value, String stockId) {
        root = insert(root, value, stockId, null);
    }

    private AVLTreePriceNode insert(AVLTreePriceNode node, StockValue value, String stockId, AVLTreePriceNode parent) {
        if (node == null) {
            AVLTreePriceNode newNode = new AVLTreePriceNode(value, stockId);
            newNode.parent = parent;
            return newNode;
        }

        if (value.getCurrentValue() < node.getValue().getCurrentValue()) {
            node.setLeft(insert(node.getLeft(), value, stockId, node));
        } else if (value.getCurrentValue() > node.getValue().getCurrentValue()) {
            node.setRight(insert(node.getRight(), value, stockId, node));
        } else {

            // Same price - add to the AVL tree of stocks at this price
            node.getStocksAtPrice().insert(stockId, value);
            node.numStockPrice+=1;

            return node;
        }

        updateNodeMetadata(node);

        int balance = getBalance(node);

        if (balance > 1 && value.getCurrentValue() < node.getLeft().getValue().getCurrentValue()) {
            return rotateRight(node);
        }

        if (balance < -1 && value.getCurrentValue() > node.getRight().getValue().getCurrentValue()) {
            return rotateLeft(node);
        }

        if (balance > 1 && value.getCurrentValue() > node.getLeft().getValue().getCurrentValue()) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        if (balance < -1 && value.getCurrentValue() < node.getRight().getValue().getCurrentValue()) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    // ... (keep all existing code until the delete methods)

    public void delete(float price, String stockId) {
        if (root == null) return;
        root = deleteHelper(root, price, stockId);
    }

    private AVLTreePriceNode deleteHelper(AVLTreePriceNode node, float price, String stockId) {
        // Base case
        if (node == null) return null;

        // Navigate to the correct node
        if (price < node.getValue().getCurrentValue()) {
            node.setLeft(deleteHelper(node.getLeft(), price, stockId));
            if (node.getLeft() != null) node.getLeft().setParent(node);
        }
        else if (price > node.getValue().getCurrentValue()) {
            node.setRight(deleteHelper(node.getRight(), price, stockId));
            if (node.getRight() != null) node.getRight().setParent(node);
        }
        else {
            // Found the price node, remove the stock from the inner AVL tree
            node.getStocksAtPrice().remove(stockId);
            node.numStockPrice-=1;

            // If there are still stocks at this price, don't delete the node
            if (node.getStocksAtPrice().getRoot() != null) {
                return node;
            }

            // If no stocks left at this price, handle node deletion
            if (node.getLeft() == null || node.getRight() == null) {
                AVLTreePriceNode temp = (node.getLeft() != null) ? node.getLeft() : node.getRight();

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    temp.setParent(node.getParent());
                    node = temp;
                }
            } else {
                // Node has two children
                AVLTreePriceNode temp = minValueNode(node.getRight());
                node.setValue(temp.getValue());
                node.stocksAtPrice = temp.stocksAtPrice;
                node.setRight(deleteHelper(node.getRight(), temp.getValue().getCurrentValue(), temp.getStockId()));
                if (node.getRight() != null) node.getRight().setParent(node);
            }
        }

        // If node was deleted
        if (node == null) return null;

        // Update height and rebalance
        updateNodeMetadata(node);
        int balance = getBalance(node);

        // Left Left Case
        if (balance > 1 && getBalance(node.getLeft()) >= 0) {
            return rotateRight(node);
        }

        // Left Right Case
        if (balance > 1 && getBalance(node.getLeft()) < 0) {
            node.setLeft(rotateLeft(node.getLeft()));
            return rotateRight(node);
        }

        // Right Right Case
        if (balance < -1 && getBalance(node.getRight()) <= 0) {
            return rotateLeft(node);
        }

        // Right Left Case
        if (balance < -1 && getBalance(node.getRight()) > 0) {
            node.setRight(rotateRight(node.getRight()));
            return rotateLeft(node);
        }

        return node;
    }

    // Keep the minValueNode method as a helper
    private AVLTreePriceNode minValueNode(AVLTreePriceNode node) {
        if (node == null) return null;
        AVLTreePriceNode current = node;
        while (current.getLeft() != null) {
            current = current.getLeft();
        }
        return current;
    }

}