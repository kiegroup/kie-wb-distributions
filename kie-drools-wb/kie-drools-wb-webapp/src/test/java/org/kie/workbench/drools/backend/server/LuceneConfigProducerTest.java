/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.drools.backend.server;

import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Before;
import org.junit.Test;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.FieldTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeInterfaceIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeNameIndexTerm;
import org.kie.workbench.common.screens.datamodeller.model.index.terms.JavaTypeParentIndexTerm;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.FullyQualifiedClassNameAnalyzer;
import org.kie.workbench.common.services.refactoring.backend.server.indexing.RuleAttributeNameAnalyzer;
import org.kie.workbench.common.services.refactoring.model.index.terms.PackageNameIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.ProjectRootPathIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleAttributeValueIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.RuleIndexTerm;
import org.kie.workbench.common.services.refactoring.model.index.terms.TypeIndexTerm;
import org.uberfire.ext.metadata.backend.lucene.analyzer.FilenameAnalyzer;
import org.uberfire.ext.metadata.backend.lucene.index.LuceneIndex;

import static org.junit.Assert.*;

public class LuceneConfigProducerTest {

    private LuceneConfigProducer producer;

    @Before
    public void setup() {
        this.producer = new LuceneConfigProducer();
    }

    @Test
    public void checkDefaultAnalyzers() {
        final Map<String, Analyzer> analyzers = producer.getAnalyzers();

        assertEquals( 12,
                      analyzers.size() );

        assertTrue( analyzers.get( RuleIndexTerm.TERM ) instanceof RuleAttributeNameAnalyzer );
        assertTrue( analyzers.get( RuleAttributeIndexTerm.TERM ) instanceof RuleAttributeNameAnalyzer );
        assertTrue( analyzers.get( RuleAttributeValueIndexTerm.TERM ) instanceof RuleAttributeNameAnalyzer );

        assertTrue( analyzers.get( ProjectRootPathIndexTerm.TERM ) instanceof FilenameAnalyzer );
        assertTrue( analyzers.get( LuceneIndex.CUSTOM_FIELD_FILENAME ) instanceof FilenameAnalyzer );

        assertTrue( analyzers.get( PackageNameIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
        assertTrue( analyzers.get( FieldTypeIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
        assertTrue( analyzers.get( JavaTypeIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
        assertTrue( analyzers.get( JavaTypeInterfaceIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
        assertTrue( analyzers.get( JavaTypeNameIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
        assertTrue( analyzers.get( JavaTypeParentIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
        assertTrue( analyzers.get( TypeIndexTerm.TERM ) instanceof FullyQualifiedClassNameAnalyzer );
    }

}
