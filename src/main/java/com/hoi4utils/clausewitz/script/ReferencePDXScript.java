//package com.hoi4utils.clausewitz.script;
//
//import com.hoi4utils.clausewitz_parser.Node;
//import com.hoi4utils.clausewitz_parser.NodeValue;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Collection;
//import java.util.List;
//import java.util.function.Function;
//import java.util.function.Supplier;
//
//// the superclass will still be of type T (the type of the referenced pdxscript object)
//// but the super obj can be null until the reference is resolved.
//// this class will contain a string identifier that identifies the referenced pdxscript object
//// (usually by its 'id' PDXScript field)
//
//public class ReferencePDXScript<T extends AbstractPDX<?>> extends AbstractPDX<T> {
//
//    // the collection of potential pdxscript objects that this reference can point to
//    protected final Supplier<Collection<T>> referenceCollectionSupplier;
//    protected final Function<T, String> idExtractor;
//    protected String referenceName;
//
//    public ReferencePDXScript(Supplier<Collection<T>> referenceCollectionSupplier, Function<T, String> idExtractor, String PDXIdentifier) {
//        super(PDXIdentifier);
//        this.referenceCollectionSupplier = referenceCollectionSupplier;
//        this.idExtractor = idExtractor;
//    }
//
//    public ReferencePDXScript(Supplier<Collection<T>> referenceCollectionSupplier, Function<T, String> idExtractor, String... PDXIdentifiers) {
//        super(PDXIdentifiers);
//        this.referenceCollectionSupplier = referenceCollectionSupplier;
//        this.idExtractor = idExtractor;
//    }
//
//    @Override
//    public void set(Node expression) throws UnexpectedIdentifierException, NodeValueTypeException {
//        usingIdentifier(expression);
//        NodeValue value = expression.value();
//        if (value.isString())
//            referenceName = value.string();
//        else
//            throw new NodeValueTypeException(expression, "string");
//    }
//
//    @Override
//    public T get() {
//        if (obj != null) {
//            return obj;
//        }
//        return resolveReference();
//    }
//
//    @Override
//    public boolean objEquals(PDXScript<?> other) {
//        if (other instanceof ReferencePDXScript<?> pdx) {
//            return referenceName.equals(pdx.referenceName)
//                    && this.referenceCollectionSupplier.equals(pdx.referenceCollectionSupplier)
//                    && this.idExtractor.equals(pdx.idExtractor);
//        }
//        return false;
//    }
//
//    private T resolveReference() {
//        Collection<T> referenceCollection = referenceCollectionSupplier.get();
//        for (T reference : referenceCollection) {
//            var referenceID = idExtractor.apply(reference);
//            if (referenceID == null) continue;
//            if (referenceID.equals(referenceName)) {
//                obj = reference;
//                return reference;
//            }
//        }
//        return null;
//    }
//
//    @Override
//    public boolean nodeEquals(AbstractPDX<?> other) {
//        if (obj == null) {
//            resolveReference();
//            if (obj == null) return false;
//        }
//        return obj.objEquals(other);
//    }
//
//    @Override
//    public @Nullable String toScript() {
//        var scripts = get();
//        if (scripts == null) return null;
//        return (getPDXIdentifier() + " = " + referenceName) + "\n";
//    }
//
//    public String getReferenceName() {
//        return referenceName;
//    }
//
//    public void setReferenceName(String newValue) {
//        referenceName = newValue;
//        obj = null;
//    }
//
//    public Collection<T> getReferenceCollection() {
//        return referenceCollectionSupplier.get();
//    }
//
//    public List<String> getReferenceCollectionNames() {
//        return referenceCollectionSupplier.get().stream().map(idExtractor).toList();
//    }
//
//    @Override
//    public boolean isUndefined() {
//        resolveReference();
//        return obj == null;
//    }
//}
