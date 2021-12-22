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

public class ImageWordsBinder implements PropertyBinder {

    @Override
    public void bind(PropertyBindingContext context) {

        context.dependencies().useRootOnly();
        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexSchemaObjectField imageWordsField =
                schemaElement.objectField( "words" );

        imageWordsField.fieldTemplate("imageWordsTemplate",
                f -> f.asString().analyzer( "custom" )
        );

        context.bridge( Map.class, new ImageWordsBridge( imageWordsField.toReference()) );
    }


    @SuppressWarnings("rawtypes")
    private static class ImageWordsBridge implements PropertyBridge<Map> {

        private final IndexObjectFieldReference imageWordsFieldReference;

        public ImageWordsBridge(IndexObjectFieldReference imageWordsFieldReference) {
            this.imageWordsFieldReference = imageWordsFieldReference;
        }

        @Override
        public void write(DocumentElement target, Map bridgedElement, PropertyBridgeWriteContext context) {
            @SuppressWarnings("unchecked")
            Map<String, String> imageWords = (Map<String, String>) bridgedElement;

            DocumentElement indexedWords = target.addObject(imageWordsFieldReference);

            for ( Map.Entry<String, String> entry : imageWords.entrySet() ) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                indexedWords.addValue( fieldName, fieldValue );
            }
        }
    }



}
