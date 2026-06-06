
public class StockValue {

    // AVL Tree Node Class
    static class StockValueNode {
        private Float value;
        private long time;
        StockValueNode left, right, parent;
        private int height;

        StockValueNode(long time, Float value) {
            this.time = time;
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.height = 1;
        }
    }

    private StockValueNode root;
    private Float currentValue;
    private final float initialValue;
    private final long initialDate;

    public StockValue(Float initial, long time) {
        this.initialValue = initial;
        this.initialDate = time;
        this.root = null;
        this.currentValue = initial;
    }

    private int getHeight(StockValueNode node) {
        return (node != null) ? node.height : 0;
    }

    private int getBalance(StockValueNode node) {
        return (node != null) ? getHeight(node.left) - getHeight(node.right) : 0;
    }

    private void updateHeight(StockValueNode node) {
        if (node != null) {
            node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        }
    }

    private StockValueNode rightRotate(StockValueNode y) {
        StockValueNode x = y.left;
        StockValueNode T2 = x.right;

        x.right = y;
        y.left = T2;

        if (T2 != null) T2.parent = y;
        x.parent = y.parent;
        y.parent = x;

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private StockValueNode leftRotate(StockValueNode x) {
        StockValueNode y = x.right;
        StockValueNode T2 = y.left;

        y.left = x;
        x.right = T2;

        if (T2 != null) T2.parent = x;
        y.parent = x.parent;
        x.parent = y;

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private StockValueNode insertHelper(StockValueNode node, StockValueNode parent, long time, float value) {
        if (node == null) {
            StockValueNode newNode = new StockValueNode(time, value);
            newNode.parent = parent;
            return newNode;
        }

        if (time < node.time) {
            node.left = insertHelper(node.left, node, time, value);
        } else if (time > node.time) {
            node.right = insertHelper(node.right, node, time, value);
        } else {
            node.value = value; // Update value if timestamp already exists
            return node;
        }

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && time < node.left.time) {
            return rightRotate(node);
        }

        if (balance < -1 && time > node.right.time) {
            return leftRotate(node);
        }

        if (balance > 1 && time > node.left.time) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && time < node.right.time) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private StockValueNode removeHelper(StockValueNode node, long time) {
        if (node == null) return null;

        if (time < node.time) {
            node.left = removeHelper(node.left, time);
        } else if (time > node.time) {
            node.right = removeHelper(node.right, time);
        } else {
            if (node.left == null || node.right == null) {
                StockValueNode temp = (node.left != null) ? node.left : node.right;

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    node = temp;
                }
            } else {
                StockValueNode temp = getMinHelper(node.right);
                node.time = temp.time;
                node.value = temp.value;
                node.right = removeHelper(node.right, temp.time);
            }
        }

        if (node == null) return null;

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && getBalance(node.left) >= 0) {
            return rightRotate(node);
        }

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0) {
            return leftRotate(node);
        }

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private StockValueNode getMinHelper(StockValueNode node) {
        while (node != null && node.left != null) node = node.left;
        return node;
    }

    public void addValueAtTime(long time, float value) {

        root = insertHelper(root, null, time, value);
        currentValue += value;
    }
    public StockValueNode find(long time) {
        return findHelper(root, time);
    }


    public void removeValueAtTime(long time) {
        if(time ==initialDate) {
            throw new IllegalArgumentException();
        }
        StockValueNode node = findHelper(root, time);
        if (node ==null) {
            throw new IllegalArgumentException();
        }
        currentValue -= node.value;
        root = removeHelper(root, time);
    }

    private StockValueNode findHelper(StockValueNode node, long time) {
        if (node == null || node.time == time) return node;
        if (time < node.time) return findHelper(node.left, time);
        return findHelper(node.right, time);
    }

    public float getCurrentValue() {
        return currentValue;
    }

    public float getInitialValue() {
        return initialValue;
    }

    public void printTree() {
        printTreeHelper(root);
    }

    private void printTreeHelper(StockValueNode node) {
        if (node != null) {
            printTreeHelper(node.left);
            System.out.println("Time: " + node.time + ", Value: " + node.value);
            printTreeHelper(node.right);
        }
    }
    public void clearAllValues() {
        root = null; // Reset the root
        currentValue = initialValue; // Reset the current value to the initial value
    }

}
