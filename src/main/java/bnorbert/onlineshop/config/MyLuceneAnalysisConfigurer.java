package bnorbert.onlineshop.config;

import org.apache.lucene.analysis.charfilter.HTMLStripCharFilterFactory;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballPorterFilterFactory;
import org.apache.lucene.analysis.standard.StandardTokenizerFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class MyLuceneAnalysisConfigurer implements LuceneAnalysisConfigurer {

    @Override
    public void configure(LuceneAnalysisConfigurationContext context) {

        context.analyzer( "custom")
                .custom()
                .tokenizer( StandardTokenizerFactory.class)
                .charFilter( HTMLStripCharFilterFactory.class)
                .tokenFilter( LowerCaseFilterFactory.class)
                .tokenFilter( EdgeNGramFilterFactory.class)
                .param("minGramSize", "1")
                .param("maxGramSize", "3")

                .tokenFilter( SnowballPorterFilterFactory.class)
                .param( "language", "English")

                .tokenFilter( ASCIIFoldingFilterFactory.class);

        context.analyzer( "english" )
                .custom()
                .tokenizer( StandardTokenizerFactory.class )
                .charFilter( HTMLStripCharFilterFactory.class )
                .tokenFilter( LowerCaseFilterFactory.class )
                .tokenFilter( SnowballPorterFilterFactory.class )
                .param( "language", "English" )
                .tokenFilter( ASCIIFoldingFilterFactory.class );

        context.normalizer( "lowercase" )
                .custom()
                .tokenFilter( LowerCaseFilterFactory.class)
                .tokenFilter( ASCIIFoldingFilterFactory.class);

        context.analyzer( "review" )
                .custom()
                .tokenizer( StandardTokenizerFactory.class )
                .tokenFilter( LowerCaseFilterFactory.class )
                .tokenFilter( SnowballPorterFilterFactory.class )
                .param( "language", "English" )
                .tokenFilter( EdgeNGramFilterFactory.class)
                .param("minGramSize", "1")
                .param("maxGramSize", "3");
    }
}
