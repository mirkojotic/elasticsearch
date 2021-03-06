/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */

package org.elasticsearch.xpack.sql.expression.function.scalar;

import org.elasticsearch.test.ESTestCase;
import org.elasticsearch.xpack.sql.analysis.analyzer.Analyzer;
import org.elasticsearch.xpack.sql.analysis.analyzer.Verifier;
import org.elasticsearch.xpack.sql.analysis.index.EsIndex;
import org.elasticsearch.xpack.sql.analysis.index.IndexResolution;
import org.elasticsearch.xpack.sql.expression.function.FunctionRegistry;
import org.elasticsearch.xpack.sql.parser.SqlParser;
import org.elasticsearch.xpack.sql.plan.logical.Project;
import org.elasticsearch.xpack.sql.proto.Mode;
import org.elasticsearch.xpack.sql.proto.Protocol;
import org.elasticsearch.xpack.sql.session.Configuration;
import org.elasticsearch.xpack.sql.stats.Metrics;
import org.elasticsearch.xpack.sql.type.TypesTests;
import org.elasticsearch.xpack.sql.util.DateUtils;

public class UserFunctionTests extends ESTestCase {

    public void testNoUsernameFunctionOutput() {
        SqlParser parser = new SqlParser();
        EsIndex test = new EsIndex("test", TypesTests.loadMapping("mapping-basic.json", true));
        Analyzer analyzer = new Analyzer(
                new Configuration(DateUtils.UTC, Protocol.FETCH_SIZE, Protocol.REQUEST_TIMEOUT,
                                  Protocol.PAGE_TIMEOUT, null, 
                                  randomFrom(Mode.values()), randomAlphaOfLength(10), 
                                  null, randomAlphaOfLengthBetween(1, 15)),
                new FunctionRegistry(),
                IndexResolution.valid(test),
                new Verifier(new Metrics())
        );
        
        Project result = (Project) analyzer.analyze(parser.createStatement("SELECT USER()"), true);
        assertTrue(result.projections().get(0) instanceof User);
        assertNull(((User) result.projections().get(0)).fold());
    }
}
