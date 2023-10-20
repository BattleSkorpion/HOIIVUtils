package clausewitz_parser_new;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class Node implements NodeStreamable<Node> {
	public String name;
	public String operator;

	/* never null, stores null */
	private NodeValue value;
	public SymbolNode valueAttachment;
	public Token valueAttachmentToken;
	public Token nameToken;
	public Token operatorToken;

	public Node (String name, String operator, NodeValue value, SymbolNode valueAttachment,
	             Token valueAttachmentToken, Token nameToken, Token operatorToken) {
		this.name = name;
		this.operator = operator;
		this.value = (value == null) ?  new NodeValue() : value;
		this.valueAttachment = valueAttachment;
		this.valueAttachmentToken = valueAttachmentToken;
		this.nameToken = nameToken;
		this.operatorToken = operatorToken;
	}

	public Node(NodeValue value) {
		this(null, null, value, null, null, null, null);
	}

	public Node() {
		this((NodeValue) null);
	}

	public Node(ArrayList<Node> value) {
		this(new NodeValue(value));
	}

	public String name() {
		return name;
	}

	public NodeValue value() {
		return value;
	}

	public Object valueObject() {
		return value.valueObject();
	}

//	// todo token type
//	public Node get(TokenType tokenType, String str) {
//		Object value = value();
//		if (value instanceof ArrayList<?>) {
//			for (Node node : (ArrayList<Node>) value) {
//				if (node.name.equals(str)) {
//					return node;
//				}
//			}
//			return null; // not found in the list
//		}
////		else if (value instanceof SymbolNode) {
////			return ((SymbolNode) value).name.equals(str) ? value : null;
////		}
//		return null;
//	}
//
//	public ArrayList<Node> getAll(TokenType tokenType, String str) {
//		Object value = value();
//		ArrayList<Node> result = new ArrayList<>();
//		if (value instanceof ArrayList<?>) {
//			for (Node node : (ArrayList<Node>) value) {
//				if (node.name.equals(str)) {
//					result.add(node);
//				}
//			}
//		} else {
//			return null;
//		}
//
//		return result;
//	}

	// no clue
	NodeStream<Node> stream() {
		NodeStream<Node> nodeStream = new NodeStream<>(this);

		return nodeStream;
	}

	@Override
	public Stream<Node> getStream() {
		return stream().getStream();
	}

	@Override
	public NodeStreamable<Node> filter(Predicate<? super Node> predicate) {
		return new NodeStream<>(this).filter(predicate);
	}

	@Override
	public <R extends Node> NodeStreamable<R> map(Function<? super Node, ? extends R> mapper) {
		return new NodeStream<>(this).map(mapper);
	}

	@Override
	public <R extends Node> NodeStreamable<R> flatMap(Function<? super Node, ? extends NodeStreamable<R>> mapper) {
		return new NodeStream<>(this).flatMap(mapper);
	}

	@Override
	public List<Node> toList() {
		return new NodeStream<>(this).toList();
	}

	@Override
	public void forEach(Consumer<? super Node> action) {
		new NodeStream<>(this).forEach(action);
	}

	@Override
	public Node findFirst() {
		return new NodeStream<>(this).findFirst();
	}

	@Override
	public Node findFirst(Predicate<Node> predicate) {
		var result = new NodeStream<>(this).findFirst(predicate);
		return (result != null) ? result : new Node();
	}

	@Override
	// note: was findFirstName, refactored
	public Node findFirst(String str) {
		var result = new NodeStream<>(this).findFirst(str);
		return (result != null) ? result : new Node();
	}

	@Override
	public NodeStreamable<Node> filterName(String str) {
		return new NodeStream<>(this).filterName(str);
	}

	@Override
	public NodeStreamable<Node> filter(String str) {
		return filterName(str);
	}

	public NodeValue getValue(String id) {
		return findFirst(id).value;
	}

}