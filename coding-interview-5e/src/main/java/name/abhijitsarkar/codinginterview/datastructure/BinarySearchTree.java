package name.abhijitsarkar.codinginterview.datastructure;

import java.util.Collection;

public class BinarySearchTree<E extends Comparable<E>> {

	private BinaryTreeNode<E> root = null;
	private int depth = 0;

	public BinarySearchTree() {
	}

	public BinarySearchTree(Collection<E> elements) {
		addAll(elements);
	}

	private void addAll(Collection<E> elements) {
		BinaryTreeNode<E> node = root;
		for (E anElement : elements) {
			node = add(node, anElement);
		}
	}

	public BinaryTreeNode<E> add(E value) {
		return add(root, value);
	}

	public BinaryTreeNode<E> add(BinaryTreeNode<E> node, E value) {
		if (node == null) {
			node = new BinaryTreeNode<E>(value);

			if (depth == 0) {
				root = node;
			}

			return node;
		}

		incrementDepthIfNeeded(node);

		if (value.compareTo(node.getData()) < 0) {
			node.setLeftChild(add(node.getLeftChild(), value));
		} else {
			node.setRightChild(add(node.getRightChild(), value));
		}

		return node;
	}

	private void incrementDepthIfNeeded(BinaryTreeNode<E> node) {
		if (node.isLeaf()) {
			depth++;
		}
	}

	public int getDepth() {
		return this.depth;
	}

	public BinaryTreeNode<E> getRoot() {
		return this.root;
	}

	public static class BinaryTreeNode<T> {
		private T data = null;
		private BinaryTreeNode<T> leftChild = null;
		private BinaryTreeNode<T> rightChild = null;

		public BinaryTreeNode(T data) {
			this(data, null, null);
		}

		public BinaryTreeNode(T data, BinaryTreeNode<T> leftChild,
				BinaryTreeNode<T> rightChild) {
			this.data = data;
			this.leftChild = leftChild;
			this.rightChild = rightChild;
		}

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public BinaryTreeNode<T> getLeftChild() {
			return leftChild;
		}

		public void setLeftChild(BinaryTreeNode<T> leftChild) {
			this.leftChild = leftChild;
		}

		public BinaryTreeNode<T> getRightChild() {
			return rightChild;
		}

		public void setRightChild(BinaryTreeNode<T> rightChild) {
			this.rightChild = rightChild;
		}

		public boolean isLeaf() {
			return (this.leftChild == null && this.rightChild == null);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((data == null) ? 0 : data.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			BinaryTreeNode<T> other = (BinaryTreeNode<T>) obj;
			if (data == null) {
				if (other.data != null)
					return false;
			} else if (!data.equals(other.data))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "BinaryTreeNode [data=" + data + "]";
		}
	}
}
