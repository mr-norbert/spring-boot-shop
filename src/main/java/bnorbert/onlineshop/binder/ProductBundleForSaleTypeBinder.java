package bnorbert.onlineshop.binder;

import bnorbert.onlineshop.domain.Bundle;
import bnorbert.onlineshop.domain.Product;
import org.hibernate.search.engine.backend.document.DocumentElement;
import org.hibernate.search.engine.backend.document.IndexFieldReference;
import org.hibernate.search.mapper.pojo.bridge.TypeBridge;
import org.hibernate.search.mapper.pojo.bridge.binding.TypeBindingContext;
import org.hibernate.search.mapper.pojo.bridge.mapping.programmatic.TypeBinder;
import org.hibernate.search.mapper.pojo.bridge.runtime.TypeBridgeWriteContext;
import org.hibernate.search.mapper.pojo.extractor.builtin.BuiltinContainerExtractors;
import org.hibernate.search.mapper.pojo.model.path.PojoModelPath;

public class ProductBundleForSaleTypeBinder implements TypeBinder {

    @Override
    public void bind(TypeBindingContext context) {
        context.dependencies()
                //.useRootOnly();

        .use( PojoModelPath.builder()
                .property( "priceByBundle" )
                .value( BuiltinContainerExtractors.MAP_KEY )
                .property( "name" )
                .toValuePath() );

        IndexFieldReference<String> bundlesForSaleField = context.indexSchemaElement()
                .field( "bundleForSale", f -> f.asString().analyzer( "custom" ))
                .multiValued()
                .toReference();

        context.bridge( Product.class, new Bridge( bundlesForSaleField ));
    }


    private static class Bridge implements TypeBridge<Product> {

        private final IndexFieldReference<String> bundlesForSaleField;

        private Bridge(IndexFieldReference<String> bundlesForSaleField) {
            this.bundlesForSaleField = bundlesForSaleField;
        }

        @Override
        public void write(DocumentElement target, Product product, TypeBridgeWriteContext context) {
            for ( Bundle bundle : product.getPriceByBundle().keySet() ) {
                target.addValue(bundlesForSaleField, bundle.getName());

            }
        }
    }

    
}
