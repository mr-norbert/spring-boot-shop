package bnorbert.onlineshop.binder;

import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

import java.util.Map;

public class OrderWordsBinder implements PropertyBinder {


    @Override
    public void bind(PropertyBindingContext context) {

        context.dependencies()
                .useRootOnly();

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexSchemaObjectField orderWordsField =
                schemaElement.objectField( "backupWords" );

        orderWordsField.fieldTemplate("orderWordsTemplate",
                f -> f.asString().analyzer( "english" )
        );

        context.bridge( Map.class, new OrderWordsBridge( orderWordsField.toReference()) );
    }


    @SuppressWarnings("rawtypes")
    private static class OrderWordsBridge implements PropertyBridge<Map> {

        private final IndexObjectFieldReference orderWordsFieldReference;

        public OrderWordsBridge(IndexObjectFieldReference orderWordsFieldReference) {
            this.orderWordsFieldReference = orderWordsFieldReference;
        }

        @Override
        public void write(DocumentElement target, Map bridgedElement, PropertyBridgeWriteContext context) {
            @SuppressWarnings("unchecked")
            Map<String, String> orderWords = (Map<String, String>) bridgedElement;

            DocumentElement indexedWords = target.addObject(orderWordsFieldReference);

            for ( Map.Entry<String, String> entry : orderWords.entrySet() ) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                indexedWords.addValue( fieldName, fieldValue );
            }
        }
    }


}
