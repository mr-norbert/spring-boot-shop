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

public class UserMetadataBinder implements PropertyBinder {

    @Override
    public void bind(PropertyBindingContext context) {
        context.dependencies().useRootOnly();

        IndexSchemaElement schemaElement = context.indexSchemaElement();

        IndexSchemaObjectField userMetadataField =
                schemaElement.objectField( "userMetadata" );

        userMetadataField.fieldTemplate("userMetadataValueTemplate",
                f -> f.asString().analyzer( "custom" ));

        context.bridge( Map.class, new UserMetadataBridge( userMetadataField.toReference() ) );
    }

    @SuppressWarnings("rawtypes")
    private static class UserMetadataBridge implements PropertyBridge<Map> {

        private final IndexObjectFieldReference userMetadataFieldReference;

        private UserMetadataBridge(IndexObjectFieldReference userMetadataFieldReference) {
            this.userMetadataFieldReference = userMetadataFieldReference;
        }

        @Override
        public void write(DocumentElement target, Map bridgedElement, PropertyBridgeWriteContext context) {
            @SuppressWarnings("unchecked")
            Map<String, String> userMetadata = (Map<String, String>) bridgedElement;

            DocumentElement indexedUserMetadata = target.addObject( userMetadataFieldReference );

            for ( Map.Entry<String, String> entry : userMetadata.entrySet() ) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                indexedUserMetadata.addValue( fieldName, fieldValue );
            }
        }
    }
}
