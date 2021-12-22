package bnorbert.onlineshop.binder;

import bnorbert.onlineshop.domain.CartItem;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.engine.backend.types.dsl.IndexFieldTypeFactory;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

import java.util.Set;

public class CartLineItemsDetailBinder implements PropertyBinder {

    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies()
                .use("product") //ManyToOne
                .use("subTotal");

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexSchemaObjectField lineItemsField =
                schemaElement.objectField(
                                "lineItems",
                                ObjectStructure.NESTED
                        )
                        .multiValued();

        context.bridge( Set.class, new Bridge(
                lineItemsField.toReference(),
                lineItemsField.field( "product", IndexFieldTypeFactory::asString)
                        .toReference(),
                lineItemsField.field( "subTotal", IndexFieldTypeFactory::asDouble)
                        .toReference()
        ));
    }

    @SuppressWarnings("rawtypes")
    private static class Bridge implements PropertyBridge<Set> {

        private final IndexObjectFieldReference lineItemsField;
        private final IndexFieldReference<String> categoryField;
        private final IndexFieldReference<Double> subTotalField;

        private Bridge(IndexObjectFieldReference lineItemsField,
                       IndexFieldReference<String> categoryField, IndexFieldReference<Double> subTotalField) {
            this.lineItemsField = lineItemsField;
            this.categoryField = categoryField;
            this.subTotalField = subTotalField;
        }


        @Override
        public void write(DocumentElement target, Set bridgedElement, PropertyBridgeWriteContext context) {
            @SuppressWarnings("unchecked")
            Set<CartItem> lineItems = (Set<CartItem>) bridgedElement;

            for ( CartItem lineItem : lineItems ) {
                DocumentElement indexedLineItem = target.addObject( this.lineItemsField );
                indexedLineItem.addValue( this.categoryField, lineItem.getProduct().getCategoryName());
                //indexedLineItem.addValue( this.categoryField, lineItem.getCategory().name() ); //enum
                indexedLineItem.addValue( this.subTotalField, lineItem.getSubTotal());
            }
        }
    }
}
