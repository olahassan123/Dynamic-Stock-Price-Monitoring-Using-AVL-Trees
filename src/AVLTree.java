public class AVLTree {

    static class AVLTreeNode {
        private StockValue value;
        private String key;
        private AVLTreeNode left, right, parent;
        private int height;

        public AVLTreeNode(String key, StockValue value) {
            this.key = key;
            this.value = value;
            this.left = null;
            this.right = null;
            this.parent = null;
            this.height = 1;
        }

        // Getter and setter for value
        public StockValue getValue() {
            return value;
        }


        // Getter and setter for key
        public String getKey() {
            return key;
        }


        // Getter and setter for left
        public AVLTreeNode getLeft() {
            return left;
        }


        // Getter and setter for right
        public AVLTreeNode getRight() {
            return right;
        }






    }

    private AVLTreeNode root;

    public AVLTree() {
        this.root = null;
    }

    private int getHeight(AVLTreeNode node) {
        return (node != null) ? node.height : 0;
    }

    private int getBalance(AVLTreeNode node) {
        return (node != null) ? getHeight(node.left) - getHeight(node.right) : 0;
    }

    private void updateHeight(AVLTreeNode node) {
        if (node != null) {
            node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        }
    }

    private AVLTreeNode rightRotate(AVLTreeNode y) {
        if (y == null || y.left == null) return y;

        AVLTreeNode x = y.left;
        AVLTreeNode T2 = x.right;
        AVLTreeNode parent = y.parent;

        x.right = y;
        y.parent = x;

        y.left = T2;
        if (T2 != null) T2.parent = y;

        x.parent = parent;
        if (parent != null) {
            if (parent.left == y) parent.left = x;
            else parent.right = x;
        }

        updateHeight(y);
        updateHeight(x);

        return x;
    }

    private AVLTreeNode leftRotate(AVLTreeNode x) {
        if (x == null || x.right == null) return x;

        AVLTreeNode y = x.right;
        AVLTreeNode T2 = y.left;
        AVLTreeNode parent = x.parent;

        y.left = x;
        x.parent = y;

        x.right = T2;
        if (T2 != null) T2.parent = x;

        y.parent = parent;
        if (parent != null) {
            if (parent.left == x) parent.left = y;
            else parent.right = y;
        }

        updateHeight(x);
        updateHeight(y);

        return y;
    }

    private AVLTreeNode insertHelper(AVLTreeNode node, AVLTreeNode parent, String key, StockValue data) {
        if (node == null) {
            AVLTreeNode newNode = new AVLTreeNode(key, data);
            newNode.parent = parent;
            return newNode;
        }

        if (key.compareTo(node.key) < 0) {
            node.left = insertHelper(node.left, node, key, data);
        } else if (key.compareTo(node.key) > 0) {
            node.right = insertHelper(node.right, node, key, data);
        } else {
            node.value = data;
            return node;
        }

        updateHeight(node);
        int balance = getBalance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return leftRotate(node);
        }

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private AVLTreeNode removeHelper(AVLTreeNode node, String key) {
        if (node == null) return null;

        if (key.compareTo(node.key) < 0) {
            node.left = removeHelper(node.left, key);
            if (node.left != null) node.left.parent = node;
        } else if (key.compareTo(node.key) > 0) {
            node.right = removeHelper(node.right, key);
            if (node.right != null) node.right.parent = node;
        } else {
            if (node.left == null || node.right == null) {
                AVLTreeNode temp = (node.left != null) ? node.left : node.right;

                if (temp == null) {
                    temp = node;
                    node = null;
                } else {
                    temp.parent = node.parent;
                    node = temp;
                }
            } else {
                AVLTreeNode temp = getMinHelper(node.right);
                node.key = temp.key;
                node.value = temp.value;
                node.right = removeHelper(node.right, temp.key);
                if (node.right != null) node.right.parent = node;
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

    private AVLTreeNode findHelper(AVLTreeNode node, String key) {
        if (node == null || node.key.equals(key)) return node;
        if (key.compareTo(node.key) < 0) return findHelper(node.left, key);
        return findHelper(node.right, key);
    }

    private AVLTreeNode getMinHelper(AVLTreeNode node) {
        if (node == null) return null;
        while (node.left != null) node = node.left;
        return node;
    }



    public void insert(String key, StockValue data) {
        root = insertHelper(root, null, key, data);
    }

    public void remove(String key) {
        root = removeHelper(root, key);
        if (root != null) root.parent = null;
    }

    public AVLTreeNode find(String key) {
        return findHelper(root, key);
    }



    public AVLTreeNode getRoot() {
        return root;
    }
}
