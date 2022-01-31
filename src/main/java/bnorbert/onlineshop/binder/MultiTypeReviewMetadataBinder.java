package bnorbert.onlineshop.binder;

import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexObjectFieldReference;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaElement;
import org.hibernate.search.engine.backend.document.model.dsl.IndexSchemaObjectField;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.bridge.PropertyBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.PropertyBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.PropertyBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.PropertyBridgeWriteContext;

import java.util.Map;

public class MultiTypeReviewMetadataBinder implements PropertyBinder {

    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies()
                .useRootOnly();


        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexSchemaObjectField reviewMetadataField =
                schemaElement.objectField( "multiTypeReviewMetadata" );

       reviewMetadataField.fieldTemplate(
                        "reviewMetadataValueTemplate_int",
                        f -> f.asInteger().sortable( Sortable.YES )
                )
                .matchingPathGlob( "*_long" );

        reviewMetadataField.fieldTemplate(
                "reviewMetadataValueTemplate_default",
                f -> f.asString().analyzer( "review" )
        );

        context.bridge( Map.class, new Bridge( reviewMetadataField.toReference() ) );
    }

    @SuppressWarnings("rawtypes")
    private static class Bridge implements PropertyBridge<Map> {

        private final IndexObjectFieldReference reviewMetadataFieldReference;

        public Bridge(IndexObjectFieldReference reviewMetadataFieldReference) {
            this.reviewMetadataFieldReference = reviewMetadataFieldReference;
        }

        @Override
        public void write(DocumentElement target, Map bridgedElement, PropertyBridgeWriteContext context) {
            @SuppressWarnings("unchecked")
            Map<String, Object> reviewMetadata = (Map<String, Object>) bridgedElement;

            DocumentElement indexedReviewMetadata = target.addObject( reviewMetadataFieldReference );

            for ( Map.Entry<String, Object> entry : reviewMetadata.entrySet() ) {
                String fieldName = entry.getKey();
                Object fieldValue = entry.getValue();
                indexedReviewMetadata.addValue( fieldName, fieldValue );
            }
        }
    }
}
