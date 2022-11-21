/**
 * Copyright 2019-2022 Secure Dimensions GmbH.
 * <p>
 * This file is part of GeoXACML 3 Community Version.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.securedimensions.geoxacml3.test;

import de.securedimensions.geoxacml3.test.datatype.GeometryValueTest;
import de.securedimensions.geoxacml3.test.function.AnalysisFunctionsTest;
import de.securedimensions.geoxacml3.test.function.BagSetFunctionsTest;
import de.securedimensions.geoxacml3.test.function.CoreFunctionsTest;
import de.securedimensions.geoxacml3.test.function.TopologicalFunctionsTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Suite.class)
@Suite.SuiteClasses(value = {GeometryValueTest.class, TopologicalFunctionsTest.class, CoreFunctionsTest.class, AnalysisFunctionsTest.class, BagSetFunctionsTest.class})
public class MainTest {
    /**
     * the logger we'll use for all messages
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MainTest.class);

    @BeforeClass
    public static void setUpClass() {
        LOGGER.debug("Beginning Tests");

    }

    @AfterClass
    public static void tearDownClass() {
        LOGGER.debug("Finishing Tests");
    }

}
